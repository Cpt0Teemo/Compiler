package ast;

import java.util.List;

public class Block extends Stmt {

    public final List<VarDecl> varDecls;
    public final List<Stmt> stmts;

    public Block prevBlock;
    public Block nextBlock;
    public int memSize;

    public Block(List<VarDecl> varDecls, List<Stmt> stms) {
        this.varDecls = varDecls;
        this.stmts = stms;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitBlock(this);
    }
}
