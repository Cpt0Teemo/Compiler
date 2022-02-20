package ast;

public class Assign extends Stmt {

    public final Expr leftExpr;
    public final Expr rightExpr;

    public Assign(Expr leftExpr, Expr rightExpr) {
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitAssign(this);
    }
}
