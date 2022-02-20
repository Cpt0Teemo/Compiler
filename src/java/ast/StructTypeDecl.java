package ast;

import java.util.List;

public class StructTypeDecl implements ASTNode {

    public final String name;
    public final List<VarDecl> varDecls;

    public StructTypeDecl(String name, List<VarDecl> varDecls) {
        this.name = name;
        this.varDecls = varDecls;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructTypeDecl(this);
    }

}
