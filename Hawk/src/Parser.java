/***********************************************************

This parser will apply all of the rules 
and raise errors if rules are violated.

***********************************************************/


public class Parser {
    private Scanner scanner;
    private Token currentToken;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        currentToken = scanner.getNextToken();
    }

    private void match(Token.Type expected) {
        if (currentToken.type == expected) {
            currentToken = scanner.getNextToken();
        } else {
            error("Expected " + expected + " but found " + currentToken.value);
        }
    }

    private void error(String message) {
        System.err.println("Parse error at line " + currentToken.line + ": " + message);
        System.exit(1);
    }

    // Example: PROGRAM → program DECL_SEC begin STMT_SEC end;
    public void PROGRAM() {
        System.out.println("PROGRAM");
        match(Token.Type.RESERVED); // "program"
        // ... handle DECL_SEC or skip if not present
    }
}
