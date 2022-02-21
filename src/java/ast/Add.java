package ast;

public class Add extends BinOp {

    public Add(Expr left, Expr right) {
        super(left, right);
    }

    public Add(Expr right) {
        super(new IntLiteral(0), right);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitAdd(this);
    }
}
