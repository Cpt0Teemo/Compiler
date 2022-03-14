package ast;

import gen.asm.Label;

public class StrLiteral extends Expr{

    public final String str;
    public Label label;

    public StrLiteral(String str) {
        this.str = str;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStrLiteral(this);
    }
}
