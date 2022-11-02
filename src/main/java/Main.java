import model.MySymbolTable;

public class Main {

    public static void main(String[] args) {
        System.out.println("Inserting values ...");
        MySymbolTable symTable = new MySymbolTable(17);

        // Inserting some values
        symTable.insert("c");
        symTable.insert("ca");
        symTable.insert("fg");
        symTable.insert("gf");
        symTable.insert("ac");
        symTable.insert("c");

        System.out.println("\nStarting tests ...");
        // Test if all values have been inserted (all should return false)
        assert !symTable.insert("c");
        assert !symTable.insert("ca");
        assert !symTable.insert("fg");
        assert !symTable.insert("gf");
        assert !symTable.insert("ac");

        // Check the position on which the identifiers have been added
        assert 14 == symTable.lookup("c");
        assert 9 == symTable.lookup("ca");
        assert -1 == symTable.lookup("banana");

        System.out.println(symTable);
    }
}
