package ast;

public class Eq extends BinOp {

    public Eq(Expr left, Expr right) {
        super(left, right, 6, true);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitEq(this);
    }
}
