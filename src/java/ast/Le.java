package ast;

public class Le extends BinOp {

    public Le(Expr left, Expr right) {
        super(left, right, 5, true);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitLe(this);
    }
}
