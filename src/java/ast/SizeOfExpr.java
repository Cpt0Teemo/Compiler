package ast;

public class SizeOfExpr extends Expr {
    public Type insideType;

    public SizeOfExpr(Type type) {
        this.insideType = type;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitSizeOfExpr(this);
    }

}
