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
}
