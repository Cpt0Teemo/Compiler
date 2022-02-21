package ast;

public class Lt extends BinOp {

    public Lt(Expr left, Expr right) {
        super(left, right, 5, true);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitLt(this);
    }
}
