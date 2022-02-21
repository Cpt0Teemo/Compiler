package ast;

public class Add extends BinOp {

    public Add(Expr left, Expr right) {
        super(left, right, 4, true);
    }

    public Add(Expr right) {
        super(new IntLiteral(0), right, 2, false);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitAdd(this);
    }
}
