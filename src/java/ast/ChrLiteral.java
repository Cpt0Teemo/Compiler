package ast;

public class ChrLiteral extends Expr{

    public final char value;

    public ChrLiteral(char value) {
        this.value = value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitChrLiteral(this);
    }
}
