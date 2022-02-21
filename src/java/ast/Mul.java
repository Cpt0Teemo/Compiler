package ast;

public class Mul extends BinOp {

    public Mul(Expr left, Expr right) {
        super(left, right);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitMul(this);
    }
}
