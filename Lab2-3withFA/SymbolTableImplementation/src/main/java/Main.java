import model.MyScanner;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        MyScanner scanner = new MyScanner("programs/p3.txt");
        scanner.scan();

        // PIF
        scanner.classifyTokens();

        // ST
        scanner.writeToSymbolTable();
    }
}
