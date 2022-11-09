package model;

import java.util.Arrays;

public class MySymbolTable {

    private String[] hashTable;
    private int capacity;

    public MySymbolTable(int capacity) {
        this.hashTable = new String[capacity];
        this.capacity = capacity;
    }

    private int hashFunction(String identifier) {
        int asciiSum = 0;
        for(int i = 0; i < identifier.length(); i++){
            asciiSum += identifier.charAt(i);
        }
        return asciiSum % this.capacity;
    }

    /*
     * Insert a new identifier to the symbol table
     * We solve collision using Linear Probing -> searching for the next available position
     *
     * params: String identifier
     * return: TRUE if the identifier doesn't exist in symbol table
     *       FALSE otherwise
     *
     * */
    public boolean insert(String identifier) {
        // Check if already in sym table
        for (String s : this.hashTable) {
            if (s != null && s.equals(identifier)) {
                System.out.println("Identifier already in the symbol table.");
                return false;
            }
        }

        int hashValue = this.hashFunction(identifier);
        if (this.hashTable[hashValue] == null) {
            this.hashTable[hashValue] = identifier;
            System.out.println("Inserted " + identifier + " at position " + hashValue);
            return true;
        }

        // Else, we have a collision
        int nextAvailablePosition = hashValue;
        while (this.hashTable[nextAvailablePosition] != null) {
            nextAvailablePosition++;
        }
        if (this.hashTable[nextAvailablePosition] == null) {
            this.hashTable[nextAvailablePosition] = identifier;
            System.out.println("Inserted " + identifier + " at position " + nextAvailablePosition);
            return true;
        }

        System.out.println("Insert failed.");
        return false;
    }

    /*
     * Search for an identifier in the symbol table
     *
     * params: String identifier
     * return: position (Integer) if the identifier exists in symbol table
     *       -1 otherwise
     *
     * */

    public int lookup(String identifier) {
        int hashValue = hashFunction(identifier);
        while (this.hashTable[hashValue] != null) {
            if(this.hashTable[hashValue].equalsIgnoreCase(identifier)) {
                return hashValue;
            }
            hashValue ++;
        }
        return -1;
    }

    public String[] getSymTable() {
        return this.hashTable;
    }

    @Override
    public String toString() {
        return "HashTable{" +
                "symbolTable=" + Arrays.toString(this.hashTable) +
                '}';
    }
}
