package ast;

public class ArrayType implements Type {

    public final Type type;
    public final int size;

    public ArrayType(Type type, int size) {
        this.type = type;
        this.size = size;
    }

    public static Type fromType(Type type, int size) {
        if(!(type instanceof PointerType))
            return new ArrayType(type, size);

        //Since pointers have higher precedence
        type = ((PointerType) type).type;
        type = new ArrayType(type, size);
        return new PointerType(type);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitArrayType(this);
    }
}
