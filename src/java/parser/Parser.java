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
           StructType structType = new StructType(structName);
           StructTypeDecl struct = new StructTypeDecl(structType, varDecls);
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
            Expr condition = parseExpr1();
            expect(TokenClass.RPAR);
            Stmt insideLoop = parseStmt();
            return new While(condition, insideLoop);
        }
        else if (accept(TokenClass.IF)) {
            nextToken();
            expect(TokenClass.LPAR);
            Expr condition = parseExpr1();
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
            if(!accept(TokenClass.SC)) {
                expr = parseExpr1();
            }
            expect(TokenClass.SC);
            return new Return(expr);
        } else if(isExp()) {
            Expr lExpr = parseExpr1();
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
            Expr expr = parseExpr1();
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

    private Expr parseExpr1() {
        Expr lExpr = parseExpr2();

        if(accept(TokenClass.LOGOR)) {
            nextToken();
            Expr rExpr = parseExpr1();
            return new Or(lExpr, rExpr);
        } else {
            return lExpr;
        }
    }

    private Expr parseExpr2() {
        Expr lExpr = parseExpr3();

        if(accept(TokenClass.LOGAND)) {
            nextToken();
            Expr rExpr = parseExpr2();
            return new And(lExpr, rExpr);
        } else {
            return lExpr;
        }
    }

    private Expr parseExpr3() {
        Expr lExpr = parseExpr4();

        if(accept(TokenClass.EQ)) {
            nextToken();
            Expr rExpr = parseExpr3();
            return new Eq(lExpr, rExpr);
        } else if(accept(TokenClass.NE)) {
            nextToken();
            Expr rExpr = parseExpr3();
            return new Ne(lExpr, rExpr);
        } else {
            return lExpr;
        }
    }

    private Expr parseExpr4() {
        Expr lExpr = parseExpr5();

        if(accept(TokenClass.LT)) {
            nextToken();
            Expr rExpr = parseExpr4();
            return new Lt(lExpr, rExpr);
        } else if(accept(TokenClass.GT)) {
            nextToken();
            Expr rExpr = parseExpr4();
            return new Gt(lExpr, rExpr);
        } else if(accept(TokenClass.LE)) {
            nextToken();
            Expr rExpr = parseExpr4();
            return new Le(lExpr, rExpr);
        } else if(accept(TokenClass.GE)) {
            nextToken();
            Expr rExpr = parseExpr4();
            return new Ge(lExpr, rExpr);
        } else {
            return lExpr;
        }
    }

    private Expr parseExpr5() {
        Expr lExpr = parseExpr6();

        if(accept(TokenClass.PLUS)) {
            nextToken();
            Expr rExpr = parseExpr5();
            return new Add(lExpr, rExpr);
        } else if(accept(TokenClass.MINUS)) {
            nextToken();
            Expr rExpr = parseExpr5();
            return new Sub(lExpr, rExpr);
        } else {
            return lExpr;
        }
    }

    private Expr parseExpr6() {
        Expr lExpr = parseExpr7();

        if(accept(TokenClass.ASTERIX)) {
            nextToken();
            Expr rExpr = parseExpr6();
            return new Mul(lExpr, rExpr);
        } else if(accept(TokenClass.DIV)) {
            nextToken();
            Expr rExpr = parseExpr6();
            return new Div(lExpr, rExpr);
        } else if(accept(TokenClass.REM)) {
            nextToken();
            Expr rExpr = parseExpr6();
            return new Mod(lExpr, rExpr);
        } else {
            return lExpr;
        }
    }

    private Expr parseExpr7() {
        if(accept(TokenClass.PLUS)) {
            nextToken();
            Expr expr = parseExpr7();
            return new Add(expr);
        } else if(accept(TokenClass.MINUS)) {
            nextToken();
            Expr expr = parseExpr7();
            return new Sub(expr);
        } else if (accept(TokenClass.LPAR) && types.contains(lookAhead(1).tokenClass)) {
            nextToken();
            Type type = parseType();
            expect(TokenClass.RPAR);
            Expr rightExpr = parseExpr7();
            return new TypeCastExpr(type, rightExpr);
        } else if (accept(TokenClass.ASTERIX)) {
            nextToken();
            Expr rightExpr = parseExpr7();
            return new ValueAtExpr(rightExpr);
        } else if (accept(TokenClass.AND)) {
            nextToken();
            Expr rightExpr = parseExpr7();
            return new AddressOfExpr(rightExpr);
        } else {
            return parseExpr8();
        }
    }

    private Expr parseExpr8() {
        Expr expr = parseTerminal();

        if(expr instanceof VarExpr) {
            if(accept(TokenClass.LSBR)) { //Array acess
                nextToken();
                Expr arrayIndex = parseExpr1();
                expect(TokenClass.RSBR);
                return new ArrayAccessExpr(expr, arrayIndex);
            } else if(accept(TokenClass.DOT)) { //Field acess
                nextToken();
                String identifier = token.data;
                expect(TokenClass.IDENTIFIER);
                return new FieldAccessExpr(expr, identifier);
            } else if(accept(TokenClass.LPAR)) { //Function call
                nextToken();
                List<Expr> params = new ArrayList<>();
                if(!accept(TokenClass.RPAR)) {
                    while(true) {
                        params.add(parseExpr1());
                        if(!accept(TokenClass.COMMA))
                            break;
                        else
                            nextToken();
                    }
                }
                expect(TokenClass.RPAR);
                return new FunCallExpr(((VarExpr) expr).name, params);
            }
        }
        return expr;
    }

    private Expr parseTerminal() {
        TokenClass tokenClass = token.tokenClass;
        switch (tokenClass) {
            case LPAR:
                nextToken();
                Expr expr = parseExpr1();
                expect(TokenClass.RPAR);
                return expr;
            case IDENTIFIER:
                String varName = token.data;
                nextToken();
                return new VarExpr(varName);
            case INT_LITERAL:
                int value = Integer.parseInt(token.data);
                nextToken();
                return new IntLiteral(value);
            case CHAR_LITERAL:
                char character = token.data.charAt(0);
                nextToken();
                return new ChrLiteral(character);
            case STRING_LITERAL:
                String str = token.data;
                nextToken();
                return new StrLiteral(str);
            case SIZEOF:
                nextToken();
                expect(TokenClass.LPAR);
                Type type = parseType();
                expect(TokenClass.RPAR);
                return new SizeOfExpr(type);
            default:
                error(TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.SIZEOF);
                return null; //TODO
        }
    }
}
