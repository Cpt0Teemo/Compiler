package ast;

public class If extends Stmt {

    public final Expr expr;
    public final Stmt ifStmt;
    public final Stmt elseStmt;

    public If(Expr expr, Stmt ifStmt, Stmt elseStmt) {
        this.expr = expr;
        this.ifStmt = ifStmt;
        this.elseStmt = elseStmt;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitIf(this);
    }
}
