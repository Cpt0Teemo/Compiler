package ast;

public interface Type extends ASTNode {

    public <T> T accept(ASTVisitor<T> v);

    public boolean isEqual(Type t);

    public int getSize();

}
