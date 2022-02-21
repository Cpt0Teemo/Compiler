package ast;

public class Or extends BinOp {

    public Or(Expr left, Expr right) {
        super(left, right);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitOr(this);
    }
}
