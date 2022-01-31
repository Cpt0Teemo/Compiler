package parser;


import lexer.Token;
import lexer.Token.TokenClass;
import lexer.Tokeniser;

import java.util.*;


/**
 * @author cdubach
 */
public class Parser {

    private Token token;

    // use for backtracking (useful for distinguishing decls from procs when parsing a program for instance)
    private Queue<Token> buffer = new LinkedList<>();

    private final Tokeniser tokeniser;

    private List<TokenClass> types = List.of(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT);
    private TokenClass[] typesArray = types.toArray(TokenClass[]::new);
    private List<TokenClass> expStart = List.of(TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL
                                        , TokenClass.PLUS, TokenClass.MINUS, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL
                                        , TokenClass.ASTERIX, TokenClass.AND, TokenClass.SIZEOF);
    private TokenClass[] expStartArray = expStart.toArray(TokenClass[]::new);
    private List<TokenClass> binOperators = List.of(TokenClass.GT, TokenClass.LT, TokenClass.LE, TokenClass.GE, TokenClass.NE, TokenClass.EQ
                                        , TokenClass.PLUS, TokenClass.MINUS, TokenClass.DIV, TokenClass.ASTERIX, TokenClass.REM
                                        , TokenClass.LOGOR, TokenClass.LOGAND);
    private TokenClass[] binOperatorsArray = binOperators.toArray(TokenClass[]::new);

    public Parser(Tokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public void parse() {
        // get the first token
        nextToken();

        parseProgram();
    }

    public int getErrorCount() {
        return error;
    }

    private int error = 0;
    private Token lastErrorToken;

    private void error(TokenClass... expected) {

        if (lastErrorToken == token) {
            // skip this error, same token causing trouble
            return;
        }

        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (TokenClass e : expected) {
            sb.append(sep);
            sb.append(e);
            sep = "|";
        }
        System.out.println("Parsing error: expected ("+sb+") found ("+token+") at "+token.position);

        error++;
        lastErrorToken = token;
    }

    /*
     * Look ahead the i^th element from the stream of token.
     * i should be >= 1
     */
    private Token lookAhead(int i) {
        // ensures the buffer has the element we want to look ahead
        while (buffer.size() < i)
            buffer.add(tokeniser.nextToken());
        assert buffer.size() >= i;

        int cnt=1;
        for (Token t : buffer) {
            if (cnt == i)
                return t;
            cnt++;
        }

        assert false; // should never reach this
        return null;
    }


    /*
     * Consumes the next token from the tokeniser or the buffer if not empty.
     */
    private void nextToken() {
        if (!buffer.isEmpty())
            token = buffer.remove();
        else
            token = tokeniser.nextToken();
    }

    /*
     * If the current token is equals to the expected one, then skip it, otherwise report an error.
     * Returns the expected token or null if an error occurred.
     */
    private Token expect(TokenClass... expected) {
        for (TokenClass e : expected) {
            if (e == token.tokenClass) {
                Token cur = token;
                nextToken();
                return cur;
            }
        }

        error(expected);
        return null;
    }

    /*
    * Returns true if the current token is equals to any of the expected ones.
    */
    private boolean accept(TokenClass... expected) {
        boolean result = false;
        for (TokenClass e : expected)
            result |= (e == token.tokenClass);
        return result;
    }


    private void parseProgram() {
        parseIncludes();
        parseStructDecls();
        parseVarDecls(false);
        parseFunDecls();
        expect(TokenClass.EOF);
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        if (accept(TokenClass.INCLUDE)) {
            nextToken();
            expect(TokenClass.STRING_LITERAL);
            parseIncludes();
        }
    }

    private void parseStructDecls() {
       if (accept(TokenClass.STRUCT)) {
           nextToken();
           expect(TokenClass.IDENTIFIER);
           expect(TokenClass.LBRA);
           parseVarDecls(false); //TODO confirm at least one Var declaration
           expect(TokenClass.RBRA);
           expect(TokenClass.SC);

           parseStructDecls();
       }
    }

    private void parseVarDecls(boolean atLeastOne) {
        int horizon = 2;
        if(token.tokenClass == TokenClass.STRUCT)
            horizon = 3;

        if (accept(typesArray) && lookAhead(horizon).tokenClass != TokenClass.LPAR) {
            parseType();
            expect(TokenClass.IDENTIFIER);

            if(accept(TokenClass.SC)) {
                nextToken();
                parseVarDecls(false);
            }
            else if(accept(TokenClass.LSBR)) {
                nextToken();
                expect(TokenClass.INT_LITERAL);
                expect(TokenClass.RSBR);
                expect(TokenClass.SC);
                parseVarDecls(false);
            }
            else {
                error(TokenClass.SC, TokenClass.LSBR);
            }
        } else if (atLeastOne) {
            error(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT);
        }
    }

    private void parseFunDecls() {
        if (lookAhead(2).tokenClass == TokenClass.LPAR) {
            parseType();
            expect(TokenClass.IDENTIFIER);
            expect(TokenClass.LPAR);
            parseParams();
            expect(TokenClass.RPAR);
            parseBlock();
            parseFunDecls();
        }
    }

    private void parseType() {
        if(types.contains(token.tokenClass)) {
            if(accept(TokenClass.STRUCT)) {
                nextToken();
                expect(TokenClass.IDENTIFIER);
            } else {
                nextToken();
            }
            if(accept(TokenClass.ASTERIX))
                nextToken();
            return;
        }
        error(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT);
    }

    private void parseParams() {
        if(types.contains(token.tokenClass)) {
            parseType();
            expect(TokenClass.IDENTIFIER);
            while(accept(TokenClass.COMMA)) {
                nextToken();
                parseType();
                expect(TokenClass.IDENTIFIER);
            }
        }
    }

    private void parseBlock() {
        expect(TokenClass.LBRA);
        parseVarDecls(false);
        parseStmts();
        expect(TokenClass.RBRA);
    }

    private void parseStmts() {
        if(accept(TokenClass.LBRA)) {
            parseBlock();
            parseStmts();
        }
        else if(accept(TokenClass.WHILE)) {
            nextToken();
            expect(TokenClass.LPAR);
            parseExp();
            expect(TokenClass.RPAR);
            parseStmts();
        }
        else if (accept(TokenClass.IF)) {
            nextToken();
            expect(TokenClass.LPAR);
            parseExp();
            expect(TokenClass.RPAR);
            parseStmts();
            if(accept(TokenClass.ELSE))
                nextToken();
            parseStmts();
        }
        else if(accept(TokenClass.RETURN)) {
            nextToken();
            if(isExp())
                parseExp();
            expect(TokenClass.SC);
            parseStmts();
        } else if(isExp()) {
            parseExp();
            parseStmtPrime();
            parseStmts();
        }
    }

    private void parseStmtPrime() {
        if(accept(TokenClass.ASSIGN)) {
            nextToken();
            parseExp();
            expect(TokenClass.SC);
        } else if (accept(TokenClass.SC)){
            nextToken();
        } else {
            error(TokenClass.EQ, TokenClass.SC);
        }
    }

    private boolean isExp() {
        return expStart.contains(token.tokenClass);
    }

    private void parseExp() {
        TokenClass tokenClass = token.tokenClass;
        nextToken();
        switch(tokenClass) {
            case LPAR:
                if(accept(typesArray)) { //Typecast
                    parseType();
                    expect(TokenClass.RPAR);
                    parseExp();
                    return;
                } else {
                    parseExpPrime();
                    expect(TokenClass.RPAR);
                    return;
                }
            case IDENTIFIER:
                if(accept(TokenClass.LPAR)) {
                    nextToken();
                    if(!accept(TokenClass.RPAR)) {
                        parseExp();
                        while(accept(TokenClass.COMMA)) {
                            nextToken();
                            parseExp();
                        }
                    }
                    expect(TokenClass.RPAR);
                    return;
                } else {
                    parseExpPrime();
                    return;
                }
            case INT_LITERAL:
                parseExpPrime();
                return;
            case PLUS: case MINUS:
                parseExp();
                parseExpPrime();
                return;
            case CHAR_LITERAL:
                parseExpPrime();
                return;
            case STRING_LITERAL:
                parseExpPrime();
                return;
            case ASTERIX:
                parseExp();
                parseExpPrime();
                return;
            case AND:
                parseExp();
                parseExpPrime();
                return;
            case SIZEOF:
                expect(TokenClass.SIZEOF);
                expect(TokenClass.LPAR);
                parseType();
                expect(TokenClass.RPAR);
                return;
            default: error(expStartArray);
        }
    }

    private void parseExpPrime() {
        if(accept(TokenClass.RSBR)) {
            nextToken();
            parseExp();
            expect(TokenClass.RSBR);
            return;
        } else if (accept(TokenClass.DOT)) {
            nextToken();
            expect(TokenClass.IDENTIFIER);
            return;
        } else if (binOperators.contains(token.tokenClass)) {
            nextToken();
            parseExp();
            return;
        }
        //Empty set
    }
}
