package model;

import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * While (not(eof)) do
 *     detect(token);
 *     if token is reserved word OR operator OR separator
 *         then genPIF(token, 0)
 *         else
 *         if token is identifier OR constant
 *             then index = pos(token, ST);
 *                 genPIF(token, index)
 *             else message “Lexical error”
 *         endif
 *     endif
 * end while
 **/

public class MyScanner {
    private String fileName;

    private List<String> tokenList = new ArrayList<String>();
    private List<String> separatorList = new ArrayList<String>();
    private List<Pair<String, Integer>> detectedTokens = new ArrayList<>();
    private Map<String, Integer> PIF = new HashMap<String, Integer>();
    private Map<Integer, String> ST = new HashMap<Integer, String>();

    private int capacity = 97;
    private MySymbolTable symbolTable = new MySymbolTable(capacity);

    private MyScanner(String fileName) {
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
            for (int i = 0; i < 18; i++){
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
        return token.matches("\\-?[1-9]+[0-9]*|0") // numberconst
                || token.matches("'[a-zA-Z0-9._-]'") // charconst
                || token.matches("\"[a-zA-Z0-9_.-]*\"") //stringconst
                || token.equals("true")
                || token.equals("false");
    }

    public void classifyTokens() throws IOException {
        PrintWriter pw = new PrintWriter("D:/Facultate/Semestrul-5/Formal-Languages-and-Compiler-Design/Labs/Lab2/SymbolTableImplementation//scanner-files/pif.out");
        pw.printf("%-20s %s\n", "Token", "ST_Pos");

        Integer lastLine = 0;
        for (Pair<String, Integer> pair: this.detectedTokens) {
            if (isReservedOperatorSeparator(pair.getKey())) {
                pw.printf("%-20s %d\n", pair.getKey(), -1);
            } else if (isIdentifier(pair.getKey()) || isConstant(pair.getKey())) {
                // index is the position from the ST

                symbolTable.insert(pair.getKey());
                int position = symbolTable.lookup(pair.getKey());
                pw.printf("%-20s %d\n", pair.getKey(), position);
            } else {
                System.out.println("LEXICAL ERROR " + pair.getKey() + " AT LINE " + (pair.getValue()));
            }
            lastLine = pair.getValue();
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

    public void scan() {
        try {
            File myObj = new File(this.fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                Scanner data = new Scanner(myReader.nextLine());
                while (data.hasNext()) {
                    String word = data.next();
                    boolean hasSeparator = false;

                    for (String separator : separatorList) {
                        // General separator cases
                        if (word.contains(separator)) {
                            hasSeparator = true;
                           // TODO: split the word by separator
                            break;
                        }
                    }

                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void splitWordWithSeparator(String word, String separator)
    {
    }
}
