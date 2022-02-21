package ast;

public class Eq extends BinOp {

    public Eq(Expr left, Expr right) {
        super(left, right);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitEq(this);
    }
}
