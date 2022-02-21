package ast;

public class Lt extends BinOp {

    public Lt(Expr left, Expr right) {
        super(left, right);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitLt(this);
    }
}
