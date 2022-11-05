package model;

import javafx.util.Pair;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyScanner {
    private String fileName;

    private List<String> tokenList = new ArrayList<>();
    private List<String> separatorList = new ArrayList<>();
    private List<Pair<String, Integer>> detectedTokens = new ArrayList<>();
    private Map<String, Integer> PIF = new HashMap<>();
    private Map<Integer, String> ST = new HashMap<>();
    private final String delimitersRegex = "\\s|;|,|:";

    private int capacity = 97;
    private MySymbolTable symbolTable = new MySymbolTable(capacity);

    public MyScanner(String fileName) {
        this.fileName = fileName;
        this.readTokens();
        this.readSeparators();
    }

    private void readTokens() {
        try {
            File file_in = new File("scanner-files/token.in");
            Scanner myReader = new Scanner(file_in);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                tokenList.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void readSeparators() {
        try {
            File file_in = new File("scanner-files/token.in");
            Scanner myReader = new Scanner(file_in);
            for (int i = 0; i < 22; i++){
                String data = myReader.nextLine();
                separatorList.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private Boolean isReservedOperatorSeparator(String myToken) {
        for (String token : this.tokenList) {
            if (myToken.equals(token)) {
                return true;
            }
        }
        return false;
    }

    private Boolean  isIdentifier(String token) {
        return token.matches("(^[a-zA-Z]+[_0-9a-zA-Z]*)");
    }

    private Boolean isConstant(String token){
        // check if string
        Matcher m = null;
        if(token.charAt(0) == '"' && token.charAt(token.length()-1) == '"'){
            StringBuffer sb= new StringBuffer(token);
            sb.deleteCharAt(0);
            sb.deleteCharAt(sb.length() - 1);
            Pattern p = Pattern.compile("(^[a-zA-Z]+[ !_0-9a-zA-Z]*)");
            m = p.matcher(sb);
        }
        return token.matches("\\-?[1-9]+[0-9]*|0") // numberconst
                || token.matches("'[a-zA-Z0-9._-]'") // charconst
                || token.equals("true")
                || token.equals("false")
                || (m!=null && m.matches());
    }

    public void classifyTokens() throws IOException {
        PrintWriter pw = new PrintWriter("D:/Facultate/Semestrul-5/Formal-Languages-and-Compiler-Design/Labs/Lab2/SymbolTableImplementation//scanner-files/pif.out");
        pw.printf("%-20s %s\n", "Token", "ST_Pos");

        for (Pair<String, Integer> pair: this.detectedTokens) {
            if (isReservedOperatorSeparator(pair.getKey())) {
                pw.printf("%-20s %d\n", pair.getKey(), -1);
            } else if (isIdentifier(pair.getKey()) || isConstant(pair.getKey())) {
                // index is the position from the ST

                symbolTable.insert(pair.getKey());
                int position = symbolTable.lookup(pair.getKey());
                pw.printf("%-20s %d\n", pair.getKey(), position);
            } else {
                System.out.println("LEXICAL ERROR: " + pair.getKey() + " AT LINE " + (pair.getValue()));
            }
        }
        pw.flush();
    }

    public void writeToSymbolTable() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter("D:/Facultate/Semestrul-5/Formal-Languages-and-Compiler-Design/Labs/Lab2/SymbolTableImplementation//scanner-files/st.out");
        pw.printf("%-20s %s\n", "Symbol Table as:", "Hash Table");
        pw.printf("%-20s %s\n", "Symbol", "ST_Pos");
        String[] symTable = this.symbolTable.getSymTable();

        for(int i = 0; i < capacity; i++) {
            if (symTable[i] != null) {
                pw.printf("%-20s %s\n", symTable[i], i);
            }
        }
        pw.flush();
    }

    public void scan(){
        BufferedReader reader;
        int lineNo = 1;
        try {
            reader = new BufferedReader(new FileReader(this.fileName));
            String line = reader.readLine();
            while (line != null) {
                scanLine(line, lineNo);
                lineNo++;
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void scanLine(String line, Integer numberOfLine) {
        // check if line contains string
        if(line.contains("\"")){
            int indexOfQuote1 = line.indexOf("\"");
            int indexOfQuote2 = line.indexOf("\"", indexOfQuote1 + 1);

            String stringConst = line.substring(indexOfQuote1, indexOfQuote2 + 1);
            line = line.replace(stringConst, "");
            detectedTokens.add(new Pair<>(stringConst, numberOfLine));
        }

        String splitters = "((?="+delimitersRegex+")|(?<="+delimitersRegex+"))";
        String[] tokens = line.split(splitters);
        for (String symbol : tokens) {
            if(!Objects.equals(symbol, " ") && !Objects.equals(symbol, "\t")) {
                detectedTokens.add(new Pair<>(symbol, numberOfLine));
            }
        }
    }
}
