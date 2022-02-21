package ast;

public class TypeCastExpr extends Expr{

    public final Type castType;
    public final Expr expr;

    public TypeCastExpr(Type castType, Expr expr) {
        this.castType = castType;
        this.expr = expr;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitTypeCastExpr(this);
    }
}
