package model;

import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    private final Grammar grammar;
    private HashMap<String, Set<String>> firstSet;
    private HashMap<String, Set<String>> followSet;

    public Parser(Grammar grammar) {
        this.grammar = grammar;
        this.firstSet = new HashMap<>();
        this.followSet = new HashMap<>();

        generateFirst();
        generateFollow();
    }

    public HashMap<String, Set<String>> getFirstSet() {
        return firstSet;
    }

    public HashMap<String, Set<String>> getFollowSet() {
        return followSet;
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
                if (firstSymbol.equals("E"))
                    temp.add("E");
                else if (terminals.contains(firstSymbol)) {
                    temp.add(firstSymbol);
                    break;
                }
                else {
                    var firstOfSymbol = firstOf(firstSymbol);
                    temp.addAll(firstOfSymbol);
                    if(!firstOfSymbol.contains("E")) {
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
                System.out.println(productionsWithNonTerminalInRhs);
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

}
