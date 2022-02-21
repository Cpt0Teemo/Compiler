package ast;

public class Sub extends BinOp {

    public Sub(Expr left, Expr right) {
        super(left, right);
    }

    public Sub(Expr right) {
        super(new IntLiteral(0), right);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitSub(this);
    }
}
