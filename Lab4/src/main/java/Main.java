import model.FiniteAutomata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String args[]) throws IOException {
        FiniteAutomata fa = new FiniteAutomata("D:/Facultate/Semestrul-5/Formal-Languages-and-Compiler-Design/Labs/Lab4/Lab4/src/main/java/fa.txt");
        fa.readFromFile();

        while(true){
            display_menu();
            String command = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter command: ");
            command = reader.readLine();

            switch (command) {
                case "1":
                    System.out.println("States: ");
                    System.out.println(fa.getSetOfStates());
                    System.out.println("\n");
                    break;
                case "2":
                    System.out.println("Alphabet: ");
                    System.out.println(fa.getAlphabet());
                    System.out.println("\n");
                    break;
                case "3":
                    System.out.println("Transitions: ");
                    System.out.println(fa.getTransitionsList());
                    System.out.println("\n");
                    break;
                case "4":
                    System.out.println("Final States: ");
                    System.out.println(fa.getFinalStates());
                    System.out.println("\n");
                    break;
                case "5":
                    System.out.println("Initial State: ");
                    System.out.println(fa.getInitialState());
                    System.out.println("\n");
                    break;
                case "6":
                    if(!fa.isDFA()){
                        System.out.println("FA needs to be a DFA.");
                        break;
                    }
                    BufferedReader reader2 =  new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("Enter sequence: ");
                    String sequence = reader2.readLine();
                    String message2 = fa.isAccepted(sequence) ? "Sequence is accepted" : "Sequence is not accepted";
                    System.out.println(message2);
                    System.out.println("\n");
                    break;
                case "0":
                    System.exit(0);
                default:
                    System.err.println("Bad command!");
                    break;
            }
        }

    }
    private static void display_menu() {
        System.out.println("1 - Show states");
        System.out.println("2 - Show alphabet");
        System.out.println("3 - Show transitions");
        System.out.println("4 - Show final states");
        System.out.println("5 - Show initial state");
        System.out.println("6 - Verify if a sequence is accepted by FA");
        System.out.println("0 - Exit \n");
    }
}
