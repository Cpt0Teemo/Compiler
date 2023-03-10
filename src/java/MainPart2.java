import ast.ASTPrinter;
import ast.Program;
import gen.ProgramGen;
import lexer.Scanner;
import lexer.Token;
import lexer.Tokeniser;
import parser.Parser;
import regalloc.NaiveRegAlloc;
import sem.SemanticAnalyzer;
import gen.asm.AssemblyProgram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * The Main file implies an interface for the subsequent components, e.g.
 *   * The Tokeniser must have a constructor which accepts a Scanner,
 *     moreover Tokeniser must provide a public method getErrorCount
 *     which returns the total number of lexing errors.
 */
public class MainPart2 {
	private static final int FILE_NOT_FOUND = 2;
    private static final int MODE_FAIL      = 254;
    private static final int LEXER_FAIL     = 250;
    private static final int PARSER_FAIL    = 245;
    private static final int SEM_FAIL       = 240;
    private static final int PASS           = 0;
    
    private enum Mode {
        LEXER, PARSER, AST, SEMANTICANALYSIS, GEN
    }

    private static void usage() {
        System.out.println("Usage: java "+Main.class.getSimpleName()+" pass inputfile outputfile");
        System.out.println("where pass is either: -lexer, -parser, -ast, -sem or -gen");
        System.exit(-1);
    }

    public static void main(String[] args) throws FileNotFoundException {

        if (args.length != 3)
            usage();

        Mode mode = null;
        switch (args[0]) {
            case "-lexer":  mode = Mode.LEXER; break;
            case "-parser": mode = Mode.PARSER; break;
            case "-ast":    mode = Mode.AST; break;
            case "-sem":    mode = Mode.SEMANTICANALYSIS; break;
            case "-gen":    mode = Mode.GEN; break;
            default:
                usage();
                break;
        }

        File inputFile = new File(args[1]);

        Scanner scanner;
        try {
            scanner = new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            System.out.println("File "+inputFile.toString()+" does not exist.");
            System.exit(FILE_NOT_FOUND);
            return;
        }

        Tokeniser tokeniser = new Tokeniser(scanner);
        if (mode == Mode.LEXER) {
            for (Token t = tokeniser.nextToken(); t.tokenClass != Token.TokenClass.EOF; t = tokeniser.nextToken()) 
            	System.out.println(t);
            if (tokeniser.getErrorCount() == 0)
        		System.out.println("Lexing: pass");
    	    else
        		System.out.println("Lexing: failed ("+tokeniser.getErrorCount()+" errors)");
            System.exit(tokeniser.getErrorCount() == 0 ? PASS : LEXER_FAIL);
        } else if (mode == Mode.PARSER) {
		    Parser parser = new Parser(tokeniser);
		    parser.parse();
		    if (parser.getErrorCount() == 0)
		    	System.out.println("Parsing: pass");
		    else
		    	System.out.println("Parsing: failed ("+parser.getErrorCount()+" errors)");
		    System.exit(parser.getErrorCount() == 0 ? PASS : PARSER_FAIL);
        }  else if (mode == Mode.AST) {
            Parser parser = new Parser(tokeniser);
            Program programAst = parser.parse();
            if (parser.getErrorCount() == 0) {                
                PrintWriter writer;
                StringWriter sw = new StringWriter();
                try {
                    writer = new PrintWriter(sw);
                    programAst.accept(new ASTPrinter(writer));
                    writer.flush();
                    System.out.print(sw.toString());
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                System.out.println("Parsing: failed ("+parser.getErrorCount()+" errors)");
            System.exit(parser.getErrorCount() == 0 ? PASS : PARSER_FAIL);
        } else if (mode == Mode.SEMANTICANALYSIS) {
            Parser parser = new Parser(tokeniser);
            Program programAst = parser.parse();
            if (parser.getErrorCount() == 0) {
                SemanticAnalyzer sem = new SemanticAnalyzer();
                int errors = sem.analyze(programAst);
                if (errors == 0)
                    System.out.println("Semantic analysis: Pass");
                else
                    System.out.println("Semantic analysis: Failed (" + errors + ")");
                System.exit(errors == 0 ? PASS : SEM_FAIL);
            } else
                System.exit(PARSER_FAIL);
        } else if (mode == Mode.GEN) {
            Parser parser = new Parser(tokeniser);
            Program programAst = parser.parse();
            SemanticAnalyzer sem = new SemanticAnalyzer();
            int errors = sem.analyze(programAst);
            AssemblyProgram prog = new AssemblyProgram();
            ProgramGen programGen = new ProgramGen(prog);
            programAst.accept(programGen);
            PrintWriter pw = new PrintWriter("temp.asm");
            prog.print(pw);
            AssemblyProgram newProg = NaiveRegAlloc.INSTANCE.apply(prog);
            PrintWriter writer = new PrintWriter("temp2.asm");
            newProg.print(writer);
            System.exit(MODE_FAIL);
        } else {
        	System.exit(MODE_FAIL);
        }
    }
}
