/*****************************************************************************

    Hawk Programming Language
    Main.java

    Steps of Compilation:
    1. Main gets file and passes to Scanner
    2. Scanner break it into tokens and passes to Parser
    3. Parser applies grammar rules to tokens

    EXTRA: implement a gui that takes a file input and shows output that is required


******************************************************************************/
import java.util.Scanner;
public class Main 
{ 
    public static void main(String[] args) {
        
    //testing scanner
    // try {
            // Scanner scanner = new Scanner("/workspaces/Hawk-Programming-Language/Hawk/src/testfile.txt"); // your source file
            // Token token;
            // do {
            //     token = scanner.getNextToken();
            //     // System.out.println(token); // prints using Token.toString()
            // } while (token.type != Token.Type.EOF);
            // // Token t;
            // // while ((t = scanner.getNextToken()).type != Token.Type.EOF) {
            // //     System.out.println(t);}
    //testing parser
        //     Parser parser = new Parser("/workspaces/Hawk-Programming-Language/Hawk/src/testfile.txt");
        //     parser.PROGRAM();
        //     // System.out.println("Parsing completed successfully.");
        // } catch (IOException e) {
        //         e.printStackTrace();
        //     }


    // Implement text based GUI
        
       Scanner input = new Scanner(System.in);

        System.out.println("=== Hawk Programming Language Interface ===");
        System.out.println("Type a file path to run, or type 'quit' to exit.");
        System.out.println("--------------------------------------------");

        while (true) {
            System.out.print("\nEnter the path of the source file: ");
            String path = input.nextLine().trim();

            if (path.equalsIgnoreCase("quit") || path.equalsIgnoreCase("q")) {
                System.out.println("Exiting Hawk Interface. Goodbye!");
                break;
            }

            try {
                Parser parser = new Parser(path);
                parser.PROGRAM();
                // System.out.println("✅ Parsing completed successfully.");
            } catch (Exception e) {
                System.out.println("File not found or an error occurred during parsing:");
                System.out.println(e.getMessage());
            }
        }

        input.close();

    }   
}

