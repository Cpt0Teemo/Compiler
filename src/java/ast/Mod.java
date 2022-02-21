package ast;

public class Mod extends BinOp {

    public Mod(Expr left, Expr right) {
        super(left, right);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitMod(this);
    }
}
