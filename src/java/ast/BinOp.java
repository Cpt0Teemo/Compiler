package ast;

public abstract class BinOp extends Expr{

    public final Expr left;
    public final Expr right;

    public BinOp(Expr left, Expr right)
    {
        this.left = left;
        this.right = right;
    }
}
