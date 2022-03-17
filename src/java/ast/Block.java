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

    public int nextBlocksOffset(){
        return this.nextBlock != null ? this.nextBlock.memSize + this.nextBlock.nextBlocksOffset() : 0;
    }

    public int prevBlocksOffset(){
        return this.memSize + (this.prevBlock != null ? this.prevBlock.prevBlocksOffset(): 0);
    }
}
