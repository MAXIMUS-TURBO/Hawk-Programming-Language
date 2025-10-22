//token tracks type, value, and line number of each token

public class Token {

    //use enum to type tokens because token values that don’t change. 
    public enum Type {//lists all possible kinds of tokens in the language
        RESERVED, //program, begin, if, etc.
        ID, //variable names
        NUM, //numbers or .
        ASSIGN, //:=
        OP, //+ - * /
        COMPARE, //< > = <>
        LPAREN, RPAREN, //( )
        SEMI, COMMA, COLON, //; , :
        EOF //special token marking the end of input
    }

    public Type type; 
    public String value;
    public int line;


    public Token(Type type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }


    //for testing purposes
    public String toString() {
        return "(" + type + ", " + value + ", line " + line + ")";
    }

    //contructor examples 
    //Token t = new Token(Token.Type.ID, "x", 3);
    //System.out.println(new Token(Token.Type.RESERVED, "program", 1)); 
                //prints (RESERVED, program, line 1)

}
