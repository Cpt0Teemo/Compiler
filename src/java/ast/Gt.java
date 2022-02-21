package ast;

public class Gt extends BinOp {

    public Gt(Expr left, Expr right) {
        super(left, right, 5, true);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitGt(this);
    }
}
