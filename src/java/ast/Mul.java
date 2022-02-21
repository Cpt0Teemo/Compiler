package ast;

public class Mul extends BinOp {

    public Mul(Expr left, Expr right) {
        super(left, right, 3, true);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitMul(this);
    }
}
