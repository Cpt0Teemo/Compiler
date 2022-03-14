package ast;

import gen.asm.Label;

public class VarDecl implements ASTNode {
    public final Type type;
    public final String varName;
    public Label label;
    public int offset;

    public VarDecl(Type type, String varName) {
	    this.type = type;
	    this.varName = varName;
    }

    public boolean isStaticData() { return label != null; }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitVarDecl(this);
    }
}
