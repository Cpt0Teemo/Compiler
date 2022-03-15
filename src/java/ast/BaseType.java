package ast;

import lexer.Token;

public enum BaseType implements Type {
    INT, CHAR, VOID;

    public static BaseType fromTokenClass(Token.TokenClass tokenClass) {
        switch (tokenClass) {
            case INT : return BaseType.INT;
            case CHAR : return BaseType.CHAR;
            case VOID : return BaseType.VOID;
            default : return null;
        }
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitBaseType(this);
    }

    @Override
    public boolean isEqual(Type t) {
        return t == this;
    }

    @Override
    public int getSize() {
        switch (this) {
            case INT: return 4;
            case CHAR: return 1;
            default: return 0;
        }
    }

}
