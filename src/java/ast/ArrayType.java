package ast;

public class ArrayType implements Type {

    public final Type type;
    public final int size;

    public ArrayType(Type type, int size) {
        this.type = type;
        this.size = size;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitArrayType(this);
    }

    @Override
    public boolean isEqual(Type t) {
        if(t instanceof ArrayType)
            return type.isEqual(((ArrayType) t).type);
        else
            return false;
    }

    @Override
    public int getSize() {
        int x = this.type.getSize() * size;
        return x + (4 - (x % 4));
    }
}
