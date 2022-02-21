package ast;

public class StrLiteral extends Expr{

    public final String str;

    public StrLiteral(String str) {
        this.str = str;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStrLiteral(this);
    }
}
