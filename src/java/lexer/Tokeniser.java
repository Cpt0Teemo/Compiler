package lexer;

import lexer.Token.TokenClass;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author cdubach
 */
public class Tokeniser {

    private Scanner scanner;

    private List<char[]> escapableCharacters = Arrays.asList("tbnrf'\"\\".toCharArray());

    private int error = 0;
    public int getErrorCount() {
	return this.error;
    }

    public Tokeniser(Scanner scanner) {
        this.scanner = scanner;
    }

    private void error(char c, int line, int col) {
        System.out.println("Lexing error: unrecognised character ("+c+") at "+line+":"+col);
	error++;
    }


    public Token nextToken() {
        Token result;
        try {
             result = next();
        } catch (EOFException eof) {
            // end of file, nothing to worry about, just return EOF token
            return new Token(TokenClass.EOF, scanner.getLine(), scanner.getColumn());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // something went horribly wrong, abort
            System.exit(-1);
            return null;
        }
        return result;
    }

    /*
     * To be completed
     */
    private Token next() throws IOException {

        int line = scanner.getLine();
        int column = scanner.getColumn();

        // get the next character
        char c = scanner.next();

        // skip white spaces
        if (Character.isWhitespace(c))
            return next();

        // Simple characters
        if (c == '+')
            return new Token(TokenClass.PLUS, line, column);
        if (c == '-')
            return new Token(TokenClass.MINUS, line, column);
        if (c == '*')
            return new Token(TokenClass.ASTERIX, line, column);
        if (c == '%')
            return new Token(TokenClass.REM, line, column);
        if (c == '&')
            return new Token(TokenClass.AND, line, column);
        if (c == '{')
            return new Token(TokenClass.LBRA, line, column);
        if (c == '}')
            return new Token(TokenClass.RBRA, line, column);
        if (c == '(')
            return new Token(TokenClass.LPAR, line, column);
        if (c == ')')
            return new Token(TokenClass.RPAR, line, column);
        if (c == '[')
            return new Token(TokenClass.LSBR, line, column);
        if (c == ']')
            return new Token(TokenClass.RSBR, line, column);
        if (c == ';')
            return new Token(TokenClass.SC, line, column);
        if (c == ',')
            return new Token(TokenClass.COMMA, line, column);
        if (c == '.')
            return new Token(TokenClass.DOT, line, column);

        // Simple double characters
        if (c == '!' && scanner.peek() == '=') {
            scanner.next();
            return new Token(TokenClass.NE, line, column);
        }
        if (c == '&' && scanner.peek() == '&') {
            scanner.next();
            return new Token(TokenClass.LOGAND, line, column);
        }
        if (c == '|' && scanner.peek() == '|') {
            scanner.next();
            return new Token(TokenClass.LOGOR, line, column);
        }

        // Multi possibility characters
        if (c == '/') {
            if( scanner.peek() == '/') {
                while( scanner.next() != '\n');
                next();
            }
            if( scanner.peek() == '*') {
                scanner.next();
                while( scanner.next() != '*' && scanner.peek() == '/');
                scanner.next();
                next();
            }
            return new Token(TokenClass.DIV, line, column);
        }
        if (c == '=') {
            if (scanner.peek() == '=') {
                scanner.next();
                return new Token(TokenClass.EQ, line, column);
            }
            return new Token(TokenClass.ASSIGN, line, column);
        }
        if (c == '<') {
            if (scanner.peek() == '=') {
                scanner.next();
                return new Token(TokenClass.LE, line, column);
            }
            return new Token(TokenClass.LT, line, column);
        }
        if (c == '>') {
            if (scanner.peek() == '=') {
                scanner.next();
                return new Token(TokenClass.GE, line, column);
            }
            return new Token(TokenClass.GT, line, column);
        }

        // Gets weiiiirdd here
        if (c == '#' && isFollowedByInclude()) {
            return new Token(TokenClass.INCLUDE, line, column);
        }
        if (c == '\'') {
            scanner.next();
            if(scanner.next() == '\'')
                return new Token(TokenClass.CHAR_LITERAL, line, column);
        }
        if (c == '"' && isStringCorrect()) {
            return new Token(TokenClass.STRING_LITERAL, line, column);
        }
        if (Character.isDigit(c)) {
            traverseInt();
            return new Token(TokenClass.INT_LITERAL, line, column);
        }
        if (Character.isLetter(c) || c == '_')
            return handleWordsAndIdentifiers(c);



        // if we reach this point, it means we did not recognise a valid token
        error(c, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }

    private void traverseInt() throws IOException {
        while (Character.isDigit(scanner.peek())) {
            scanner.next();
        }
    }

    private void traverseIdentifiers() throws IOException {
        char nextChar = scanner.peek();
        while (nextChar != '\n' &&
                (Character.isDigit(nextChar) || Character.isLetter(nextChar) || nextChar == '_')) {
            scanner.next();
        }
    }

    private boolean isStringCorrect() throws IOException {
        while (scanner.peek() != '"') {
            if (scanner.peek() == '\n') {
                scanner.next();
                return false;
            }

            if (scanner.peek() == '\\') {
                scanner.next();
                if(!escapableCharacters.contains(scanner.peek()))
                    error(scanner.peek(), scanner.getLine(), scanner.getColumn());

            }
            scanner.next();
        }
        scanner.next();
        return true;
    }

    private boolean isFollowedByInclude() throws IOException {
        char nextChar;
        for(char c: "include".toCharArray()) {
            if(c == scanner.peek())
                scanner.next();
            else {
                goToNextSpaceOrNewLine();
                return false;
            }
        }
        return true;
    }

    private Token handleWordsAndIdentifiers(char c) throws IOException {
        String str = String.valueOf(c);
        while(str.length() > 6) {
            str += String.valueOf(scanner.peek());
            if(mapStringToToken(str) != null)
                return new Token(mapStringToToken(str), scanner.getLine(), scanner.getColumn());
        }
        traverseIdentifiers();
        return new Token(TokenClass.IDENTIFIER, scanner.getLine(), scanner.getColumn());
    }

    private TokenClass mapStringToToken(String str) {
        switch (str) {
            case "int": return TokenClass.INT;
            case "void": return TokenClass.VOID;
            case "char": return TokenClass.CHAR;

            case "if": return TokenClass.IF;
            case "else": return TokenClass.ELSE;
            case "while": return TokenClass.WHILE;
            case "return": return TokenClass.RETURN;
            case "struct": return TokenClass.STRUCT;
            case "sizeof": return TokenClass.SIZEOF;
            default: return null;
        }
    }

    private void goToNextSpaceOrNewLine() throws IOException {
        char nextChar = scanner.peek();
        while (!Character.isWhitespace(nextChar) || nextChar != '\n') {
            scanner.next();
        }
        scanner.next();
    }

}
