package ast;

public class ValueAtExpr extends Expr{

    public final Expr expr;

    public ValueAtExpr(Expr expr) {
        this.expr = expr;
    }


    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitValueAtExpr(this);
    }
}
