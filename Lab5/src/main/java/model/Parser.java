package model;

import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    private final Grammar grammar;
    private HashMap<String, Set<String>> firstSet;
    private HashMap<String, Set<String>> followSet;
    private Map<Pair<String, String>, Integer> productionsNumbered = new HashMap<>();
    private HashMap<Pair, Pair> parseTable;
    private Stack<String> alpha = new Stack<>(); // input stack
    private Stack<String> beta = new Stack<>(); // working stack
    private Stack<String> pi = new Stack<>(); // output stack

    public Parser(Grammar grammar) {
        this.grammar = grammar;
        this.firstSet = new HashMap<>();
        this.followSet = new HashMap<>();
        this.parseTable = new HashMap<>();

        generateFirst();
        generateFollow();
        generateParseTable();
    }

    public HashMap<String, Set<String>> getFirstSet() {
        return firstSet;
    }

    public HashMap<String, Set<String>> getFollowSet() {
        return followSet;
    }

    public HashMap<Pair, Pair> getParseTable() {
        return parseTable;
    }

    private void generateFirst() {
        for (String nonTerminal : grammar.getSetOfNonTerminals()) {
            firstSet.put(nonTerminal, this.firstOf(nonTerminal));
        }
    }

    private Set<String> firstOf(String nonTerminal) {
        if (firstSet.containsKey(nonTerminal))
            return firstSet.get(nonTerminal);
        Set<String> temp = new HashSet<>();
        List<String> terminals = grammar.getSetOfTerminals();
        for (String production : grammar.productionForNonTerminal(nonTerminal)) {
            var rules = production.split(" ");
            for(String firstSymbol : rules) {
                if (firstSymbol.equals("E")) // For a production rule X → E, First(X) = { E }
                    temp.add("E");
                else if (terminals.contains(firstSymbol)) { // For any terminal symbol ‘a’ First(a) = { a }
                    temp.add(firstSymbol);
                    break;
                }
                else { // For a production rule X → Y1Y2Y3,
                    var firstOfSymbol = firstOf(firstSymbol); // If E ∈ First(Y1), then First(X) = { First(Y1) – E } ∪ First(Y2Y3)
                    temp.addAll(firstOfSymbol);
                    if(!firstOfSymbol.contains("E")) { // If E ∉ First(Y1), then First(X) = First(Y1)
                        break;
                    }
                }
            }
        }
        return temp;
    }

    private void generateFollow() {
        //initialization
        for(String nonTerminal : grammar.getSetOfNonTerminals()) {
            followSet.put(nonTerminal, new HashSet<>());
        }

        followSet.get(grammar.getStartingSymbol()).add("E");

        //rest of iterations
        var isChanged = true;
        while(isChanged) {
            isChanged = false;
            HashMap<String, Set<String>> newColumn = new HashMap<>();

            var productionsWithNonTerminalInRhs = new HashMap<String, List<String>>();
            for(String nonTerminal : grammar.getSetOfNonTerminals()) {
                newColumn.put(nonTerminal, new HashSet<>());
                var allProductions = grammar.getSetOfProductions();
                allProductions.forEach((key, value) -> {
                    for(var production : value) {
                        if(production.contains(nonTerminal)) {
                            if(!productionsWithNonTerminalInRhs.containsKey(key)) {
                                productionsWithNonTerminalInRhs.put(key, new ArrayList<>());
                            }
                            if(!productionsWithNonTerminalInRhs.get(key).contains(production))
                                productionsWithNonTerminalInRhs.get(key).add(production);
                        }
                    }
                });
                var toAdd = new HashSet<>(followSet.get(nonTerminal));
                productionsWithNonTerminalInRhs.forEach((key, value) -> {
                    for(var production : value) {
                        var productionList = production.split(" ");
                        for(var indexOfNonTerminal = 0; indexOfNonTerminal < productionList.length; indexOfNonTerminal++){
                            if(productionList[indexOfNonTerminal].equals(nonTerminal)) {
                                if(indexOfNonTerminal + 1 == productionList.length) { //For any production rule A → αB, Follow(B) = Follow(A)
                                    toAdd.addAll(followSet.get(key));
                                } else {
                                    var followSymbol = productionList[indexOfNonTerminal + 1];
                                    if(grammar.getSetOfTerminals().contains(followSymbol)) { // if followSymbol is a terminal
                                        toAdd.add(followSymbol);
                                    } else { // // For any production rule A → αBβ,
                                        for (var symbol : firstSet.get(followSymbol)) {
                                            if(symbol.equals("E")) {
                                                toAdd.addAll(followSet.get(key)); // If epsilon ∈ First(β), then Follow(B) = Follow(B) ∪ Follow(A)
                                                toAdd.addAll(firstSet.get(followSymbol).stream().filter(elem -> !Objects.equals(elem, "E")).collect(Collectors.toSet()));
                                            } else {
                                                toAdd.addAll(firstSet.get(followSymbol)); // If epsilon ∉ First(β), then Follow(B) = First(β)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
                if(!toAdd.equals(followSet.get(nonTerminal))) {
                    isChanged = true;
                }
                newColumn.put(nonTerminal, toAdd);
            }
            followSet = newColumn;
        }
    }

    private void generateParseTable() {
        numberingProductions();

        System.out.println(productionsNumbered);

        List<String> columnSymbols = new LinkedList<>(grammar.getSetOfTerminals());
        columnSymbols.add("$");

        // M(a, a) = pop
        // M($, $) = acc
        parseTable.put(new Pair<>("$", "$"), new Pair<>("acc", -1));
        for (String terminal: grammar.getSetOfTerminals())
            parseTable.put(new Pair<>(terminal, terminal), new Pair<>("pop", -1));
        //        1) M(A, a) = (α, i), if:
        //            a) a ∈ first(α)
        //            b) a != ε
        //            c) A -> α production with index i
        //
        //        2) M(A, b) = (α, i), if:
        //            a) ε ∈ first(α)
        //            b) whichever b ∈ follow(A)
        //            c) A -> α production with index i

        productionsNumbered.forEach((key, value) -> {
            String rowSymbol = key.getKey();
            String rule = key.getValue();

            String[] rules = rule.split(" ");

            Pair<String, Integer> parseTableValue = new Pair<>(rule, value);

            for (String columnSymbol : columnSymbols) {
                Pair<String, String> parseTableKey = new Pair<>(rowSymbol, columnSymbol);

                // if our column-terminal is exactly first of rule
                if (rules[0].equals(columnSymbol) && !columnSymbol.equals("E"))
                    parseTable.put(parseTableKey, parseTableValue);

                // if the first symbol is a non-terminal, and it's first contain our column-terminal
                else if (grammar.getSetOfNonTerminals().contains(rules[0]) && firstSet.get(rules[0]).contains(columnSymbol)) {
                    if (!parseTable.containsKey(parseTableKey)) {
                        parseTable.put(parseTableKey, parseTableValue);
                    }
                }
                else {
                    // if the first symbol is ε then everything of FOLLOW(rowSymbol) will be in parse table
                    if (rules[0].equals("E")) {
                        for (String b : followSet.get(rowSymbol))
                        {
                            if ( b.equals("E"))
                                b = "$";
                            parseTable.put(new Pair<>(rowSymbol, b), parseTableValue);
                        }
                    }
                    // if ε is in FIRST(rule)
                    else {
                        Set<String> firsts = new HashSet<>();
                        for (String symbol : rules)
                            if (grammar.getSetOfNonTerminals().contains(symbol))
                                firsts.addAll(firstSet.get(symbol));

                        if (firsts.contains("E")) {
                            for (String b : firstSet.get(rowSymbol)) {
                                if (b.equals("E"))
                                    b = "$";
                                parseTableKey = new Pair<>(rowSymbol, b);
                                if (!parseTable.containsKey(parseTableKey)) {
                                    parseTable.put(parseTableKey, parseTableValue);
                                }
                            }
                        }
                    }
                }

            }
        });
    }

    private void numberingProductions() {
        final int[] index = {1};
        grammar.getSetOfProductions().forEach((key,value) -> {
            for(var production : value) {
                productionsNumbered.put(new Pair<>(key, production), index[0]++);
            }
        });
    }

    public String getProductionByOrderNumber(int index) {
        final String[] production = {""};
        productionsNumbered.forEach( (elem, i) -> {
            if (i.equals(index))
                production[0] = elem.getValue();
        });
        return production[0];
    }
    public String printParseTable() {
        StringBuilder builder = new StringBuilder();
        parseTable.forEach((k, v) -> builder.append("(").append(k.getKey()).append(",").append(k.getValue()).append(")").append(" -> ").append(v).append("\n"));
        return builder.toString();
    }

    public boolean parse(List<String> w) {
        initializeStacks(w);

        boolean go = true;
        boolean result = true;

        while (go) {
            String betaHead = beta.peek();
            String alphaHead = alpha.peek();

            if (betaHead.equals("$") && alphaHead.equals("$")) {
                return result;
            }

            Pair<String, String> heads = new Pair<>(betaHead, alphaHead);
            Pair parseTableEntry = parseTable.get(heads);

            //the case where alpha contains only $
            if (parseTableEntry == null) {
                heads = new Pair<>(betaHead, "E");
                parseTableEntry = parseTable.get(heads);
                if (parseTableEntry != null) {
                    beta.pop();
                    continue;
                }
            }

            if (parseTableEntry == null) {
                go = false;
                result = false;
            } else {
                String production = (String) parseTableEntry.getKey();
                Integer productionPos = (Integer) parseTableEntry.getValue();

                String[] productions = production.split(" ");
                if (productionPos == -1 && productions[0].equals("acc")) {
                    go = false;
                } else if (productionPos == -1 && productions[0].equals("pop")) {
                    beta.pop();
                    alpha.pop();
                } else {
                    beta.pop();
                    if (!productions[0].equals("E")) {
                        pushAsChars(List.of(productions), beta);
                    }
                    if(pi.size() == 1 && pi.peek().equals("E")){
                        pi.pop();
                    }
                    pi.push(productionPos.toString());
                }
            }
        }

        return result;
    }

    private void initializeStacks(List<String> w) {
        alpha.clear();
        alpha.push("$");
        pushAsChars(w, alpha);

        beta.clear();
        beta.push("$");
        beta.push(grammar.getStartingSymbol());

        pi.clear();
        pi.push("E");
    }

    private void pushAsChars(List<String> sequence, Stack<String> stack) {
        for (int i = sequence.size() - 1; i >= 0; i--) {
            stack.push(sequence.get(i));
        }
    }

    public Stack<String> getPi() {
        return pi;
    }

    public Grammar getGrammar() {
        return grammar;
    }
}
