

/***********************************************************
    

This scanner is like the lexical analyzer of the program

>keep track of line numbers
>Throw errors for illegal tokens

Reserved words: program, begin, end, if, then, else, input, output, int, float, double, while, loop
Operators: :=, <, >, =, <>, +, -, *, /, (, )
Special symbols: ; , :
Identifiers: [a-zA-Z_][a-zA-Z0-9_]* but not reserved words.
Numbers: integers or floats, max 10 digits.

Throw errors for: 
    >Illegal symbol DONE 
    >Redeclared identifier
    >Use of undeclared identifier
    >Illegal number format DONE

steps to implement:
1. Reads file line by line.
2. Uses regex or manual character scanning.
3. Returns a list (or stream) of Token objects.


notes: missing implementation 
    >for symbol table and undeclared/redeclared checks
    >max 10 digit number check DONE
    >illegal number format (too many decimals or ending with decimal) DONE
***********************************************************/


//scanner implements rules 5,16 and 18
//Rule 05: ID 🡪 (_ | a | b | … | z | A | … | Z) (_ | a | b | … | z | A |
//                  … | Z | 0 | 1 | … | 9)*

//Rule 16: NUM 🡪 (0 | 1 | ... | 9)+ [.(0 | 1 | … | 9)+]

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Scanner {

    private String source; //source file content
    private int pos = 0;
    private int lineNum = 1;


    //step 1 - read file
    public Scanner(String filename) throws IOException {
        // Reads the entire file into a single string
        source = Files.readString(Path.of(filename));
    }
    //create set of reserved words
    private static final ArrayList<String> RESERVED = new ArrayList<>
    (Arrays.asList("program", "begin", "end", "if", "then", "else","input", "output", "int", "double" ,"float" , "while", "loop"));

    private void skipWhitespace() {
        // Skip spaces, tabs, and newlines
        while (pos < source.length()) {
        char c = source.charAt(pos);
        if (c == ' ' || c == '\t' || c == '\n') {
            if (c == '\n') lineNum++;
            pos++;
        } else {break;}
        }
        }


    private char peek() { //peak at next character without consuming it
            return (pos + 1 < source.length()) ? source.charAt(pos + 1) : '\0';
            }

    public Token getNextToken() {
        skipWhitespace();
        //c is the current character
        
        // check   End of file    after skipping whitespace
        if (pos >= source.length()) {
            return new Token(Token.Type.EOF, "EOF", lineNum);
        }

        char c = source.charAt(pos);
        //debugging line to see current character being processed
        // System.out.println("Current char: '" + c + "' at line " + lineNum); 
        // Track newlines
        if (c == '\n') {
            lineNum++;  // Increment line number
            pos++;      // Move to next character
            return getNextToken(); //recurse
        }

        
        // Check if Number first (to avoid confusion with IDs starting with digits)
        //10 digits max checking implemented
        if (c >= '0' && c <= '9') {
            int countDigits = 0; //counter for digits

            // make a char array for the number
            char[] numChars = new char[source.length() - pos];
            int len = 0;
           
            // Read the first digit
            numChars[len++] = c;
            pos++;
            countDigits++;

            //check for floats
            boolean hasDecimal = false;

            while (pos < source.length()) {
                char ch = source.charAt(pos); //ch is char at current pos
                
                if (ch >= '0' && ch <= '9') { 
                    numChars[len++] = ch;
                    pos++;
                    countDigits++;
                }
                else if (ch == '.' && !hasDecimal) { // Allow only one decimal point
                        numChars[len++] = ch;
                        pos++;
                        hasDecimal = true;
                        //also make sure it is followed by at least one digit or else error
                        if (pos < source.length()) {
                            char nextCh = source.charAt(pos);
                            if (nextCh >= '0' && nextCh <= '9') {
                                // Read the digit after decimal
                                numChars[len++] = nextCh;
                                pos++;
                                countDigits++;
                            } else {
                                System.err.println("Lexical error at line " + lineNum + ": Illegal number format, decimal point not followed by digit.");
                                System.exit(1);
                            }
                        } 
                        else { //if decimal is last character then error
                            System.err.println("Lexical error at line " + lineNum + ": Illegal number format, decimal point not followed by digit.");
                            System.exit(1);
                        }
                        
                } //check if a decimal is repeated twice within same number
                else if (ch == '.' && hasDecimal) {
                            System.err.println("Lexical error at line " + lineNum + ": Illegal number format, multiple decimal points.");
                            System.exit(1);
                        } 
                else {break;} // stop when it's not a number or decimal
            }

            // Create the string from the char array
            String num = new String(numChars, 0, len);
            if (countDigits > 10){ //if more than 10 digits then return error
                    System.err.println("Error at line " + lineNum +": '"+ num+"' has " + countDigits + " digits. Max digits allowed: 10.");
                    System.exit(1);
                    return null;
                    //stop the entire program if more than 10 digits
                    }
            return new Token(Token.Type.NUM, num, lineNum);
            
        }
        

        // Check identifiers or reserved words
        if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_') {
            // Preallocate a char array for the id (max length = remaining source)
            char[] idChars = new char[source.length() - pos];
            int len = 0; //tracks length of id

            // Read the first character
            idChars[len++] = c;
            pos++;

            // Read remaining characters that are letters, digits, or underscore
            while (pos < source.length()) {
                char ch = source.charAt(pos); //ch is char at current pos
                if ((ch >= 'A' && ch <= 'Z') ||(ch >= 'a' && ch <= 'z') ||(ch >= '0' && ch <= '9') ||ch == '_') {
                    idChars[len++] = ch; //add char to id array
                    pos++;  //move to next character
                } 
                else {break;}//exit loop if not valid id character
            }

            // Create the string from the char array
            String id = new String(idChars, 0, len);

            if (RESERVED.contains(id))  //after checking full id, see if reserved
                return new Token(Token.Type.RESERVED, id, lineNum);

            return new Token(Token.Type.ID, id, lineNum); //if not reserved, it's an ID
        }



        // Operators and symbols
        switch (c) {
            case ':': //check for := 
                if (peek() == '=') {
                    pos += 2; //go past both characters
                    return new Token(Token.Type.ASSIGN, ":=", lineNum); //return assign token
                } else {
                    pos++; 
                    return new Token(Token.Type.COLON, ":", lineNum); //return colon token
                }
            case '+': case '-': case '*': case '/': 
                pos++;
                return new Token(Token.Type.OP, Character.toString(c), lineNum); //return operator token
            case '<': //check <>
                if (peek() == '>') { pos += 2; return new Token(Token.Type.COMPARE, "<>", lineNum); } //return not equal token
                pos++;
                return new Token(Token.Type.COMPARE, "<", lineNum); //return less than token
            case '>': case '=': //check >=
                pos++;
                return new Token(Token.Type.COMPARE, Character.toString(c), lineNum); //return greater than or equal or greater than & equal token
            case '(':
                pos++;
                return new Token(Token.Type.LPAREN, "(", lineNum);
            case ')':
                pos++;
                return new Token(Token.Type.RPAREN, ")", lineNum);
            case ';':
                pos++;
                return new Token(Token.Type.SEMI, ";", lineNum); 
            case ',':
                pos++;
                return new Token(Token.Type.COMMA, ",", lineNum);
        }
        
        // Illegal character
        System.err.println("Lexical error at line " + lineNum + ": Illegal symbol '" + c + "'");
        System.exit(1);
        return null;
    }


}
