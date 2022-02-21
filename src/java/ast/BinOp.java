package ast;

import java.lang.reflect.InvocationTargetException;

public abstract class BinOp extends Expr{

    public final Expr left;
    public final Expr right;
    public final int precedence;
    public final boolean isLeftAssociative;

    public BinOp(Expr left, Expr right, int precedence, boolean isLeftAssociative)
    {
        this.left = left;
        this.right = right;
        this.precedence = precedence;
        this.isLeftAssociative = isLeftAssociative;
    }

    private BinOp applyPrecedence() {
        if(this.right instanceof BinOp)
        {
            BinOp right = (BinOp) this.right;
            if(right.precedence > this.precedence)
            {
                Expr newRightExpr = this.callConstructor(right.left, this.right);
                BinOp result = right.callConstructor(this.left, newRightExpr);
                return result;
            }
        }
    }

    abstract BinOp callConstructor(Expr left, Expr right);

    abstract BinOp switchOperators(Expr rightValue);
}
