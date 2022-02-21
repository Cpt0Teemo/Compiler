package ast;

public class Div extends BinOp {

    public Div(Expr left, Expr right) {
        super(left, right, 3, true);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitDiv(this);
    }
}
