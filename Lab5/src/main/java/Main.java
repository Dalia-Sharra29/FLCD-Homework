import model.Grammar;
import model.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Grammar grammar = new Grammar("D:/Facultate/Semestrul-5/Formal-Languages-and-Compiler-Design/FLCD-Homework/Lab5/src/main/java/input/g1.txt");
        grammar.readFromFile();

        Parser parser = new Parser(grammar);

        while (true) {
            display_menu();
            String command = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter command: ");
            command = reader.readLine();

            switch (command) {
                case "1" -> {
                    System.out.println("Non-terminals: ");
                    System.out.println(grammar.getSetOfNonTerminals());
                    System.out.println("\n");
                }
                case "2" -> {
                    System.out.println("Terminals: ");
                    System.out.println(grammar.getSetOfTerminals());
                    System.out.println("\n");
                }
                case "3" -> {
                    System.out.println("Production: ");
                    System.out.println(grammar.getSetOfProductions());
                    System.out.println("\n");
                }
                case "4" -> {
                    System.out.println("Starting symbol: ");
                    System.out.println(grammar.getStartingSymbol());
                    System.out.println("\n");
                }
                case "5" -> {
                    System.out.println("Enter non-terminal: ");
                    Scanner input = new Scanner(System.in);
                    String non_terminal = input.next();
                    System.out.println(grammar.productionForNonTerminal(non_terminal));
                    System.out.println("\n");
                }
                case "6" -> {
                    System.out.println(parser.getFirstSet());
                }
                case "7" -> {
                    System.out.println(parser.getFollowSet());
                }
                case "0" -> System.exit(0);
                default -> System.err.println("Unrecognized option");
            }
        }
    }

    private static void display_menu() {
        System.out.println("1 - Show non-terminals");
        System.out.println("2 - Show terminals");
        System.out.println("3 - Show productions");
        System.out.println("4 - Show starting symbol");
        System.out.println("5 - Production for given non-terminal");
        System.out.println("6 - first set");
        System.out.println("7 - follow set");
        System.out.println("0 - Exit \n");
    }
}
