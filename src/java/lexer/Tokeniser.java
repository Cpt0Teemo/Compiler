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

    private List<char[]> escapableCharacters = Arrays.asList("tbnrf0'\"\\".toCharArray());
    private boolean parsing = false;

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
            if(parsing) {
                int line = scanner.getLine();
                int column = scanner.getColumn();
                error('\0', line, column);
                return new Token(TokenClass.INVALID, line, column);
            }
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
        parsing = false;
        char c = scanner.next();

        // skip white spaces
        if (Character.isWhitespace(c))
            return next();

        parsing = true;
        // Simple single characters
        Token result = checkSimpleSingleCharacters(c);
        if(result != null)
            return result;

        // Simple double characters
        if (c == '!' && scanner.peek() == '=') {
            scanner.next();
            return new Token(TokenClass.NE, line, column);
        }
        if (c == '|' && scanner.peek() == '|') {
            scanner.next();
            return new Token(TokenClass.LOGOR, line, column);
        }

        // Multi possibility characters
        result = checkComplexMultiCharacters(c, line, column);
        if(result != null)
            return result;

        // Dynamic lengths
        if (c == '#')                           return handleInclude(line, column);
        if (c == '\'')                          return handleCharLiteral(scanner.next(), line, column);
        if (c == '"')                           return handleStringLiteral();
        if (Character.isDigit(c))               return handleIntLiteral(c, line, column);
        if (Character.isLetter(c) || c == '_')  return handleWordsAndIdentifiers(c, line, column);


        // if we reach this point, it means we did not recognise a valid token
        error(c, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }

    private Token checkSimpleSingleCharacters(char c) {
        int line = scanner.getLine();
        int column = scanner.getColumn();
        switch (c) {
            case '+': return new Token(TokenClass.PLUS, line, column);
            case '-': return new Token(TokenClass.MINUS, line, column);
            case '*': return new Token(TokenClass.ASTERIX, line, column);
            case '%': return new Token(TokenClass.REM, line, column);
            case '{': return new Token(TokenClass.LBRA, line, column);
            case '}': return new Token(TokenClass.RBRA, line, column);
            case '(': return new Token(TokenClass.LPAR, line, column);
            case ')': return new Token(TokenClass.RPAR, line, column);
            case '[': return new Token(TokenClass.LSBR, line, column);
            case ']': return new Token(TokenClass.RSBR, line, column);
            case ';': return new Token(TokenClass.SC, line, column);
            case ',': return new Token(TokenClass.COMMA, line, column);
            case '.': return new Token(TokenClass.DOT, line, column);
            default: return null;
        }
    }

    private Token checkComplexMultiCharacters(char c, int line, int column) throws IOException {
        switch (c) {
            case '/':
                if (scanner.peek() == '/') {
                    while (scanner.next() != '\n') ;
                    return next();
                }
                if (scanner.peek() == '*') {
                    scanner.next();
                    while (scanner.next() != '*' || scanner.peek() != '/') ;
                    scanner.next();
                    return next();
                }
                return new Token(TokenClass.DIV, line, column);
            case '=':
                if (scanner.peek() == '=') {
                    scanner.next();
                    return new Token(TokenClass.EQ, line, column);
                }
                return new Token(TokenClass.ASSIGN, line, column);
            case '<':
                if (scanner.peek() == '=') {
                    scanner.next();
                    return new Token(TokenClass.LE, line, column);
                }
                return new Token(TokenClass.LT, line, column);
            case '>':
                if (scanner.peek() == '=') {
                    scanner.next();
                    return new Token(TokenClass.GE, line, column);
                }
                return new Token(TokenClass.GT, line, column);
            case '&':
                if (scanner.peek() == '&') {
                    scanner.next();
                    return new Token(TokenClass.LOGAND, line, column);
                }
                return new Token(TokenClass.AND, line, column);
            default: return null;
        }
    }

    private Token handleIntLiteral(char c, int line, int column) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        while (Character.isDigit(scanner.peek())) {
            sb.append(scanner.next());
        }
        return new Token(TokenClass.INT_LITERAL, sb.toString(), line, column);
    }

    private Token handleStringLiteral() throws IOException {
        StringBuilder sb = new StringBuilder();
        Token failure = null;
        while (scanner.peek() != '"') {
            if (scanner.peek() == '\n') {
                error(scanner.peek(), scanner.getLine(), scanner.getColumn());
                return new Token(TokenClass.INVALID, "\\n", scanner.getLine(), scanner.getColumn());
            }

            if (scanner.peek() == '\\') {
                scanner.next();
                char nextChar = scanner.next();
                char escapeChar = mapCharToEscapeChar(nextChar);
                if(escapeChar == '!') {
                    error(nextChar, scanner.getLine(), scanner.getColumn());
                    failure = new Token(TokenClass.INVALID, "\\" + nextChar, scanner.getLine(), scanner.getColumn());
                }
                sb.append(escapeChar);
                continue;
            }
            sb.append(scanner.next());
        }
        scanner.next();
        if(failure != null)
            return failure;
        return new Token(TokenClass.STRING_LITERAL, sb.toString(), scanner.getLine(), scanner.getColumn());
    }

    private Token handleCharLiteral(char c, int line, int column) throws IOException {
        if(c == '\\') { //If has an escape character
            char nextChar = scanner.next();
            char escapeChar = mapCharToEscapeChar(nextChar);
            if(escapeChar == '!') {
                if(scanner.peek() == '\'')
                    scanner.next();
                error(nextChar, line, column);
                return new Token(TokenClass.INVALID, "\\" + nextChar, scanner.getLine(), scanner.getColumn());
            }
            if(scanner.next() == '\'')
                return new Token(TokenClass.CHAR_LITERAL, Character.toString(escapeChar), line, column);
        }
        else if(c == '\'') { // Doesn't accept ''
            scanner.next();
            error(c, line, column);
            return new Token(TokenClass.INVALID, Character.toString(c), line, column);
        }
        else if(scanner.next() == '\'')
            return new Token(TokenClass.CHAR_LITERAL, Character.toString(c), line, column);

        error(c, line, column);
        return new Token(TokenClass.INVALID, "'", line, column);
    }

    private Token handleInclude(int line, int column) throws IOException {
        for(char c: "include".toCharArray()) {
            if(c == scanner.peek())
                scanner.next();
            else {
                char error = scanner.peek();
                goToNextSpaceOrNewLine();
                error(error, line, column);
                return new Token(TokenClass.INVALID, "#", line, column);
            }
        }
        return new Token(TokenClass.INCLUDE, line, column);
    }

    private char mapCharToEscapeChar(char c) {
        switch (c) {
            case 't': return '\t';
            case 'b': return '\b';
            case 'n': return '\n';
            case 'r': return '\r';
            case 'f': return '\f';
            case '\'': return '\'';
            case '"': return '"';
            case '\\': return '\\';
            case '0': return '\0';
            default: return '!'; //Placeholder for wrong
        }
    }

    private Token handleWordsAndIdentifiers(char c, int line, int column) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        while(sb.length() < 6 && Character.isLetter(scanner.peek())) {
            sb.append(scanner.next());
            if(mapStringToToken(sb.toString()) != null) {
                char nextChar = scanner.peek();
                if(Character.isLetter(nextChar) || Character.isDigit(nextChar) || nextChar == '_')
                    break;
                return new Token(mapStringToToken(sb.toString()), line, column);
            }
        }
        sb = traverseIdentifiers(sb);
        return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
    }

    private StringBuilder traverseIdentifiers(StringBuilder sb) throws IOException {
        char nextChar = scanner.peek();
        while (Character.isDigit(nextChar) || Character.isLetter(nextChar) || nextChar == '_') {
            sb.append(scanner.next());
            nextChar = scanner.peek();
        }
        return sb;
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
        try {
            char nextChar = scanner.peek();
            while (!Character.isWhitespace(nextChar)) {
                scanner.next();
                nextChar = scanner.peek();
            }
            scanner.next();
        } catch (EOFException e) {
            return;
        }
    }

}
