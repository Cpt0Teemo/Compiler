package ast;

public class Ge extends BinOp {

    public Ge(Expr left, Expr right) {
        super(left, right, 5, true);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitGe(this);
    }
}
