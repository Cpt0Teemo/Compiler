package ast;

public class StructType implements Type {

    public final String name;
    public StructTypeDecl structTypeDecl;

    public StructType(String name) {
        this.name = name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructType(this);
    }

    @Override
    public boolean isEqual(Type t) {
        if(t instanceof StructType)
            return name.compareToIgnoreCase(((StructType) t).name) == 0;
        else
            return false;
    }
}
