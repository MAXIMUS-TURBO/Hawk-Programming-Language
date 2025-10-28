/***********************************************************

This parser will apply all of the rules 
and raise errors if rules are violated.

The Grammar Rules:
Rule 01: PROGRAM 🡪 program DECL_SEC begin STMT_SEC end; |
program begin STMT_SEC end; 
Rule 02: DECL_SEC 🡪 DECL | DECL DECL_SEC
Rule 03: DECL 🡪 ID_LIST : TYPE ; 
Rule 04: ID_LIST 🡪 ID | ID , ID_LIST
Rule 05: ID 🡪 (_ | a | b | … | z | A | … | Z) (_ | a | b | … | z | A |
                … | Z | 0 | 1 | … | 9)*
Rule 06: STMT_SEC 🡪 STMT | STMT STMT_SEC
Rule 07: STMT 🡪	ASSIGN | IFSTMT | WHILESTMT | INPUT | OUTPUT 
Rule 08: ASSIGN 🡪	ID := EXPR ;
Rule 09: IFSTMT 🡪	if COMP then STMT_SEC end if ; |
                    if COMP then STMT_SEC else STMT_SEC end if ; 
Rule 10: WHILESTMT 🡪	while COMP loop STMT_SEC end loop ;
Rule 11: INPUT 🡪	input ID_LIST;
Rule 12: OUTPUT 🡪 output ID_LIST; | output NUM;
Rule 13: EXPR 🡪 FACTOR | FACTOR + EXPR | FACTOR - EXPR
Rule 14: FACTOR 🡪 OPERAND | OPERAND * FACTOR | OPERAND / FACTOR 
Rule 15: OPERAND 🡪 NUM | ID | ( EXPR )
Rule 16: NUM 🡪 (0 | 1 | ... | 9)+ [.(0 | 1 | … | 9)+]
Rule 17: COMP 🡪 ( OPERAND = OPERAND ) | ( OPERAND <> OPERAND ) | ( OPERAND > OPERAND ) | ( OPERAND < OPERAND )
Rule 18: TYPE 🡪 int | float | double

>IMPORTANT: store all declared variables in a symbol table
>unusual identifiers
    -redeclared
    -undeclared

this should be handled in the parser using the symbol table because scanner doesnt know the difference between ID and RESERVED words
The parser knows the grammar context and can tell which rule is being processed (DECL, ASSIGN, etc.)

***********************************************************/
import java.io.IOException;

public class Parser {
    //parser will use scanner to get tokens and track declared variables in symbol table
    private Scanner scanner;
    private Token currentToken;
    private SymbolTable symbols = new SymbolTable();

    //parser constructor is passed filename to create scanner object 
    public Parser(String filename) throws IOException {
        scanner = new Scanner(filename);
        currentToken = scanner.getNextToken(); //initialize first token
    }

    //match to find expected type
    private void match(Token.Type expectedType) {
        if (currentToken.type == expectedType) {
            currentToken = scanner.getNextToken();
        } else {
            error("Expected " + expectedType + " but found " + currentToken.value);
        }
    }
    //match to find expected value
    private void match(String expectedValue) {
        if (currentToken.value.equals(expectedValue)) {
            currentToken = scanner.getNextToken();
        } else {
            error("Expected '" + expectedValue + "' but found '" + currentToken.value + "'");
        }
    }
    //error handling
    private void error(String message) {
        System.err.println("Parse error at line " + currentToken.line + ": " + message);
        System.exit(1);
    }


    //---------------------------------------------------------------------------------------------------------------------------
    //rule 1: PROGRAM 🡪 program DECL_SEC begin STMT_SEC end; |
    //                   program begin STMT_SEC end; 
    //program followed by optional declarations, must follow up with begin, statements, end, and semicolon
    public void PROGRAM() {
    System.out.println("PROGRAM");

    match("program");  // must start with 'program'

    if (currentToken.type == Token.Type.ID) { // if there are optional declarations
        DECL_SEC(); //program x, y: int;
    }

    match("begin"); //must have 'begin' after declarations or program
    STMT_SEC(); //statements
    match("end"); //must have 'end' after statements
    match(Token.Type.SEMI); //must end with semicolon
}
    //Rule 02: DECL_SEC 🡪 DECL | DECL DECL_SEC
    //declaration section can have multiple declarations or just a single declaration
    private void DECL_SEC() {
        System.out.println("DECL_SEC");
        DECL();
        while (currentToken.type == Token.Type.ID) { // More declarations
            DECL();
        }
    }
    //Rule 03: DECL 🡪 ID_LIST : TYPE ; //match id list to its type eg x, y: double;
    private void DECL() {
        System.out.println("DECL"); 
        ID_LIST(true); // indicates we are declaring identifiers
        match(Token.Type.COLON);

        if (currentToken.value.equals("int") || currentToken.value.equals("float") || currentToken.value.equals("double")) {
            String type = currentToken.value;
            currentToken = scanner.getNextToken();
            match(Token.Type.SEMI);
        } else {
            error("Expected type (int, float, double)");
        }
    }
    // Rule 04: ID_LIST 🡪 ID | ID , ID_LIST //id folowed a comma followed by more ids
    //declare parameter indicates if we are declaring or using identifiers
    //stores declared identifiers in symbol table
    private void ID_LIST(boolean declare) {
    System.out.println("ID_LIST");
        //if token is has a comma after this id, it must be followed by another id or else error
    if (currentToken.type == Token.Type.ID) { // At least one ID
        String name = currentToken.value; //identifier name
        //if we are declaring the identifier, store in symbol table with unknown type for now else check if it has been declared
        if (declare) 
            symbols.declare(name, "unknown", currentToken.line); //declare in symbol table
        else
            symbols.checkDeclared(name, currentToken.line); 
        match(Token.Type.ID); 
        if (currentToken.type == Token.Type.COMMA) { // More IDs must be separated by commas, followed by another IDs
            match(Token.Type.COMMA); 
            // Token peek = scanner.getNextToken();
            if (currentToken.type == Token.Type.ID) {
                ID_LIST(declare); //recursive call to process next ID
            } else {
                error("Expected identifier after ','"); // Error if no ID after comma
            }
            
            // if (currentToken.type == Token.Type.ID) {
            //     name = currentToken.value;
            //     if (declare)
            //         symbols.declare(name, "unknown", currentToken.line); //declare in symbol table
            //     else
            //         symbols.checkDeclared(name, currentToken.line);
            //     match(Token.Type.ID);
            // } 
        }
            // else {
            //     error("Expected identifier after ','"); // Error if no ID after comma
            // }
        
    } else {
        error("Expected identifier in ID_LIST"); // Error if no ID found
    }
}
// Rule 06: STMT_SEC 🡪 STMT | STMT STMT_SEC 
//statement section can have multiple statements or just a single statement
private void STMT_SEC() { 
    System.out.println("STMT_SEC");
    STMT();
    //while current token is a reserved words input, output, if, and while, or an identifier type (for assign) then continue processing statements
    // Rule 07: STMT 🡪	ASSIGN | IFSTMT | WHILESTMT | INPUT | OUTPUT 
    while (currentToken.type == Token.Type.RESERVED && (currentToken.value.equals("input") || currentToken.value.equals("output") ||
            currentToken.value.equals("if") || currentToken.value.equals("while")) ||
            currentToken.type == Token.Type.ID) 
    {STMT_SEC();}

}
// Rule 07: STMT 🡪	ASSIGN | IFSTMT | WHILESTMT | INPUT | OUTPUT 
private void STMT() {
    System.out.println("STMT");

    if (currentToken.type == Token.Type.ID) { 
        ASSIGN(); // Assignment starts with ID
    } else if (currentToken.value.equals("input")) {
        INPUT(); 
    } else if (currentToken.value.equals("output")) {
        OUTPUT();
    }else if (currentToken.value.equals("if")) {
        System.out.println("tbc");
    }
    else if (currentToken.value.equals("while")) {
        System.out.println("tbc");
    }else {
        error("Invalid statement start: " + currentToken.value); //if statement does not start with ID, input, or output raise error
    }
}
// Rule 11: INPUT 🡪	input ID_LIST; //input followed by list of id already declared in symbol table followed by ;
private void INPUT() {
    System.out.println("INPUT");
    match("input");
    ID_LIST(false); // must already be declared
    match(Token.Type.SEMI); //must end with semicolon
}
// Rule 12: OUTPUT 🡪 output ID_LIST; | output NUM;
private void OUTPUT() {
    System.out.println("OUTPUT");
    match("output");
    if (currentToken.type == Token.Type.NUM)
        match(Token.Type.NUM);
    else
        ID_LIST(false);
    match(Token.Type.SEMI);
}
// Rule 08: ASSIGN 🡪	ID := EXPR ; //id followed by := followed by an expression 
private void ASSIGN() {
    System.out.println("ASSIGN");
    symbols.checkDeclared(currentToken.value, currentToken.line); //check if ID has been declared
    match(Token.Type.ID);
    match(Token.Type.ASSIGN);
    EXPR(); //match expression after assignment
    match(Token.Type.SEMI);
}

// Rule 13: EXPR 🡪 FACTOR | FACTOR + EXPR | FACTOR - EXPR //expression enters factor first followed by + or -
private void EXPR() {
    System.out.println("EXPR");
    FACTOR();
    if (currentToken.value.equals("+") || currentToken.value.equals("-")) {
        currentToken = scanner.getNextToken();
        EXPR();
    }
}
// Rule 14: FACTOR 🡪 OPERAND | OPERAND * FACTOR | OPERAND / FACTOR 
//factor enters operand first followed by * or /
private void FACTOR() {
    System.out.println("FACTOR");
    OPERAND();
    if (currentToken.value.equals("*") || currentToken.value.equals("/")) {
        currentToken = scanner.getNextToken();
        FACTOR();
    }
}
// Rule 15: OPERAND 🡪 NUM | ID | ( EXPR )
//operand can be a number, identifier, or  parentheses around an expression 
private void OPERAND() {
    System.out.println("OPERAND");
    if (currentToken.type == Token.Type.NUM) {
        match(Token.Type.NUM);
    } else if (currentToken.type == Token.Type.ID) {
        symbols.checkDeclared(currentToken.value, currentToken.line);
        match(Token.Type.ID);
    } else if (currentToken.type == Token.Type.LPAREN) {
        match(Token.Type.LPAREN);
        EXPR();
        match(Token.Type.RPAREN);
    } else {
        error("Invalid operand: " + currentToken.value); //raise error if operand is not num, id, or (expr)
    }
}


// Rule 09: IFSTMT 🡪	if COMP then STMT_SEC end if ; |
//                     if COMP then STMT_SEC else STMT_SEC end if ; 
// Rule 10: WHILESTMT 🡪	while COMP loop STMT_SEC end loop ;
// Rule 17: COMP 🡪 ( OPERAND = OPERAND ) | ( OPERAND <> OPERAND ) | ( OPERAND > OPERAND ) | ( OPERAND < OPERAND )



}
