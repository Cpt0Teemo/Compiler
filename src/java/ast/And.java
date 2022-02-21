package ast;

public class And extends BinOp {

    public And(Expr left, Expr right) {
        super(left, right, 7, true);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitAnd(this);
    }
}
