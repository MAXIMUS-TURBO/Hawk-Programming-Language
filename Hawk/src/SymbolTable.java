/***********************************************************

This program will deploy a symbol table that will track 
    identifiers that have been declared

***********************************************************/

import java.util.*;

public class SymbolTable {
    private Map<String, String> table = new HashMap<>();

    public void declare(String name, String type, int line) {
        if (table.containsKey(name)) {
            System.err.println("Error: Redeclaration of variable '" + name + "' at line " + line);
            System.exit(1);
        }
        table.put(name, type);
    }

    public void checkDeclared(String name, int line) {
        if (!table.containsKey(name)) {
            System.err.println("Error: Undeclared identifier '" + name + "' at line " + line);
            System.exit(1);
        }
    }
}
