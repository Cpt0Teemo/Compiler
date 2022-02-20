package parser;

import ast.*;
import lexer.Token;
import lexer.Token.TokenClass;
import lexer.Tokeniser;
import util.Pair;

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

    public Program parse() {
        // get the first token
        nextToken();

        return parseProgram();
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
        nextToken();
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


    private Program parseProgram() {
        parseIncludes();
        List<StructTypeDecl> stds = parseStructDecls(new ArrayList<>());
        List<VarDecl> vds = parseVarDecls(false, new ArrayList<>());
        List<FunDecl> fds = parseFunDecls(new ArrayList<>());
        expect(TokenClass.EOF);
        return new Program(stds, vds, fds);
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        if (accept(TokenClass.INCLUDE)) {
            nextToken();
            expect(TokenClass.STRING_LITERAL);
            parseIncludes();
        }
    }

    private List<StructTypeDecl> parseStructDecls(List<StructTypeDecl> structTypeDecls) {
       if (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass == TokenClass.LBRA) {
           nextToken();
           String structName = token.data;
           expect(TokenClass.IDENTIFIER);
           expect(TokenClass.LBRA);
           List<VarDecl> varDecls = parseVarDecls(true, new ArrayList<>());
           StructTypeDecl struct = new StructTypeDecl(structName, varDecls);
           expect(TokenClass.RBRA);
           expect(TokenClass.SC);

           structTypeDecls.add(struct);
           return parseStructDecls(structTypeDecls);
       }
       return structTypeDecls;
    }

    private List<VarDecl> parseVarDecls(boolean atLeastOne, List<VarDecl> varDecls) {
        int horizon = 2;
        if(token.tokenClass == TokenClass.STRUCT)
            horizon = 3;

        if (accept(typesArray) && lookAhead(horizon).tokenClass != TokenClass.LPAR) {
            Type type = parseType();
            String identifier = token.data;
            expect(TokenClass.IDENTIFIER);

            if(accept(TokenClass.SC)) {
                nextToken();
                varDecls.add(new VarDecl(type, identifier));
                parseVarDecls(false, varDecls);
            }
            else if(accept(TokenClass.LSBR)) { //Array declaration
                nextToken();
                int size = Integer.valueOf(token.data);
                expect(TokenClass.INT_LITERAL);
                expect(TokenClass.RSBR);
                expect(TokenClass.SC);
                type = ArrayType.fromType(type, size);
                varDecls.add(new VarDecl(type, identifier));
                parseVarDecls(false, varDecls);
            }
            else {
                error(TokenClass.SC, TokenClass.LSBR);
            }
        } else if (atLeastOne) {
            error(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT);
        }
        return varDecls;
    }

    private List<FunDecl> parseFunDecls(List<FunDecl> funDecls) {
        if (lookAhead(2).tokenClass == TokenClass.LPAR) {
            Type type = parseType();
            String name = token.data;
            expect(TokenClass.IDENTIFIER);
            expect(TokenClass.LPAR);
            List<VarDecl> params = parseParams();
            expect(TokenClass.RPAR);
            Block block = parseBlock();
            funDecls.add(new FunDecl(type, name, params, block));
            parseFunDecls(funDecls);
        }
        return funDecls; //TODO
    }

    private Type parseType() {
        Type type = null;
        if(types.contains(token.tokenClass)) {
            if(accept(TokenClass.STRUCT)) {
                nextToken();
                type = new StructType(token.data);
                expect(TokenClass.IDENTIFIER);
            } else {
                type = BaseType.fromTokenClass(token.tokenClass);
                nextToken();
            }
            if(accept(TokenClass.ASTERIX)) {
                nextToken();
                type = new PointerType(type);
            }
            return type;
        }
        error(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT);
        //TODO REMOVE NULL RETURNS
        return null;
    }

    private List<VarDecl> parseParams() {
        List<VarDecl> params = new ArrayList<>();
        if(types.contains(token.tokenClass)) {
            Type type = parseType();
            String name = token.data;
            expect(TokenClass.IDENTIFIER);
            params.add(new VarDecl(type, name));
            while(accept(TokenClass.COMMA)) {
                nextToken();
                type = parseType();
                name = token.data;
                expect(TokenClass.IDENTIFIER);
                params.add(new VarDecl(type, name));
            }
        }
        return params;
    }

    private Block parseBlock() {
        expect(TokenClass.LBRA);
        List<VarDecl> varDecls = parseVarDecls(false, new ArrayList<>());
        List<Stmt> stmts = parseStmts();
        expect(TokenClass.RBRA);
        return new Block(varDecls, stmts);
    }

    private List<Stmt> parseStmts() {
        List<Stmt> stmts = new ArrayList<>();
        while(accept(TokenClass.LBRA, TokenClass.WHILE, TokenClass.IF, TokenClass.RETURN) || isExp()) {
            Stmt stmt = parseStmt();
            if(stmt == null)
                break;
            stmts.add(stmt);
        }
        return stmts;
    }

    private Stmt parseStmt() {
        if(accept(TokenClass.LBRA)) {
            return parseBlock();
        }
        else if(accept(TokenClass.WHILE)) {
            nextToken();
            expect(TokenClass.LPAR);
            Expr condition = parseExp();
            expect(TokenClass.RPAR);
            Stmt insideLoop = parseStmt();
            return new While(condition, insideLoop);
        }
        else if (accept(TokenClass.IF)) {
            nextToken();
            expect(TokenClass.LPAR);
            Expr condition = parseExp();
            expect(TokenClass.RPAR);
            Stmt ifStmt = parseStmt();
            Stmt elseStmt = null;
            if(accept(TokenClass.ELSE)) {
                nextToken();
                elseStmt = parseStmt();
            }
            return new If(condition, ifStmt, elseStmt);
        }
        else if(accept(TokenClass.RETURN)) {
            nextToken();
            Expr expr = null;
            if(isExp()) {
                expr = parseExp();
            }
            expect(TokenClass.SC);
            return new Return(expr);
        } else if(isExp()) {
            Expr lExpr = parseExp();
            Expr rExpr = parseStmtPrime();
            if(rExpr == null)
                return new ExprStmt(lExpr);
            else
                return new Assign(lExpr, rExpr);
        }
        return null;
    }

    private Expr parseStmtPrime() {
        if(accept(TokenClass.ASSIGN)) {
            nextToken();
            Expr expr = parseExp();
            expect(TokenClass.SC);
            return expr;
        } else if (accept(TokenClass.SC)){
            nextToken();
        } else {
            error(TokenClass.EQ, TokenClass.SC);
        }
        return null;
    }

    private boolean isExp() {
        return expStart.contains(token.tokenClass);
    }

    private Expr parseExp() {
        TokenClass tokenClass = token.tokenClass;
        nextToken();
        switch(tokenClass) {
            case LPAR:
                if(accept(typesArray)) { //Typecast
                    parseType();
                    expect(TokenClass.RPAR);
                    parseExp();
                    return null;
                } else {
                    parseExp();
                    expect(TokenClass.RPAR);
                    parseExpPrime();
                    return null;
                }
            case IDENTIFIER:
                if(accept(TokenClass.LPAR)) { //Function call
                    nextToken();
                    if(!accept(TokenClass.RPAR)) {
                        parseExp();
                        while(accept(TokenClass.COMMA)) {
                            nextToken();
                            parseExp();
                        }
                    }
                    expect(TokenClass.RPAR);
                    parseExpPrime();
                    return null;
                } else {
                    parseExpPrime();
                    return null;
                }
            case INT_LITERAL:
                parseExpPrime();
                return null;
            case PLUS: case MINUS:
                parseExp();
                parseExpPrime();
                return null;
            case CHAR_LITERAL:
                parseExpPrime();
                return null;
            case STRING_LITERAL:
                parseExpPrime();
                return null;
            case ASTERIX:
                parseExp();
                parseExpPrime();
                return null;
            case AND:
                parseExp();
                parseExpPrime();
                return null;
            case SIZEOF:
                expect(TokenClass.SIZEOF);
                expect(TokenClass.LPAR);
                parseType();
                expect(TokenClass.RPAR);
                return null;
            default:
                error(expStartArray);
                return null;
        }
    }

    private void parseExpPrime() {
        if(accept(TokenClass.LSBR)) {
            nextToken();
            parseExp();
            expect(TokenClass.RSBR);
            parseExpPrime();
            return;
        } else if (accept(TokenClass.DOT)) {
            nextToken();
            expect(TokenClass.IDENTIFIER);
            parseExpPrime();
            return;
        } else if (binOperators.contains(token.tokenClass)) {
            nextToken();
            parseExp();
            parseExpPrime();
            return;
        }
        //Empty set
    }
}
