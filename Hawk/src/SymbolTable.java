/***********************************************************

This program will deploy a symbol table that will track 
    identifiers that have been declared

    // This will help in semantic analysis during parsing
    // The symbol table will be used to store and retrieve variable information
    symbol table will support:
        > Storing variable names along with their types (int, float, double) (type checking is not implemented here)
        > Checking for redeclaration of variables
        > Checking for use of undeclared variables 
    

***********************************************************/

import java.util.*;

public class SymbolTable {
    private Map<String, String> table = new HashMap<>();
    //use hashmap to implement symbol table because of its efficient key-value pair storage and retrieval capabilities

    public void declare(String name, String type, int line) {
        if (table.containsKey(name)) { //check for redeclaration first
            System.err.println("Error: Redeclaration of variable '" + name + "' at line " + line);
            System.exit(1);
        }
        table.put(name, type); //store variable name and type if unique
    }

    public void checkDeclared(String name, int line) {
        if (!table.containsKey(name)) { //check for undeclared variable usage
            System.err.println("Error: Undeclared identifier '" + name + "' at line " + line);
            System.exit(1);
        }
    }
}
