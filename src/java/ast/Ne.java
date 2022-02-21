package ast;

public class Ne extends BinOp {

    public Ne(Expr left, Expr right) {
        super(left, right);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitNe(this);
    }
}
