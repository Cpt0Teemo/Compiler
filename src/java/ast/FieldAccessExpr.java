package ast;

public class FieldAccessExpr extends Expr{

    public final Expr expr;
    public final String field;

    public FieldAccessExpr(Expr expr, String field) {
        this.expr = expr;
        this.field = field;
    }


    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitFieldAccessExpr(this);
    }
}
