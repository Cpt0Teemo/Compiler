package ast;

public class PointerType implements Type {

    public final Type type;

    public PointerType(Type type) {
        this.type = type;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitPointerType(this);
    }

    @Override
    public boolean isEqual(Type t) {
        if(t instanceof PointerType)
            return type.isEqual(((PointerType) t).type);
        else
            return false;
    }

    @Override
    public int getSize() {
        return 4;
    }
}
