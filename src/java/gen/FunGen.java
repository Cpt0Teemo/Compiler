package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.Comment;
import gen.asm.OpCode;
import gen.asm.Register;

/**
 * A visitor that produces code for a function declaration
 */
public class FunGen implements ASTVisitor<Void> {

    private AssemblyProgram asmProg;
    private ExprGen exprGen;

    public FunGen(AssemblyProgram asmProg) {
        this.asmProg = asmProg;
        this.exprGen = new ExprGen(asmProg, this);
    }

    @Override
    public Void visitBaseType(BaseType bt) {
        throw new ShouldNotReach();
    }

    @Override
    public Void visitStructType(StructType st) {
        return null;
    }

    @Override
    public Void visitPointerType(PointerType pt) {
        return null;
    }

    @Override
    public Void visitArrayType(ArrayType at) {
        return null;
    }

    @Override
    public Void visitStructTypeDecl(StructTypeDecl st) {
        throw new ShouldNotReach();
    }

    @Override
    public Void visitBlock(Block b) {
        // TODO: to complete
        int offset = 4;
        for(VarDecl varDecl: b.varDecls) {
            varDecl.offset = offset;
            varDecl.accept(this);
            if(varDecl.type != BaseType.CHAR)
                offset += varDecl.type.getSize();
            else
                offset += 4;
        }
        for(Stmt stmt: b.stmts) {
            stmt.accept(exprGen);
        }
        return null;
    }

    @Override
    public Void visitWhile(While w) {
        return null;
    }

    @Override
    public Void visitIf(If i) {
        return null;
    }

    @Override
    public Void visitReturn(Return r) {
        return null;
    }

    @Override
    public Void visitAssign(Assign a) {
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt es) {
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl p) {
        // Each function should be produced in its own section.
        // This is necessary for the register allocator.
        asmProg.newSection(AssemblyProgram.Section.Type.TEXT);
        p.block.accept(this);

        // TODO: to complete:
        // 1) emit the prolog
        // 2) emit the body of the function
        // 3) emit the epilog

        return null;
    }

    @Override
    public Void visitProgram(Program p) {
        throw new ShouldNotReach();
    }

    @Override
    public Void visitVarDecl(VarDecl vd) {
        // TODO: should allocate local variables on the stack and remember the offset from the frame pointer where they are stored (e.g. in the VarDecl AST node)
        if(vd.type != BaseType.CHAR)
            asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, vd.type.getSize());
        else
            asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, 4);
        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr v) {
        // expression should be visited with the ExprGen when they appear in a statement (e.g. If, While, Assign ...)
        throw new ShouldNotReach();
    }

    @Override
    public Void visitSizeOfExpr(SizeOfExpr so) {
        return null;
    }

    @Override
    public Void visitArrayAccessExpr(ArrayAccessExpr so) {
        return null;
    }

    @Override
    public Void visitFieldAccessExpr(FieldAccessExpr fa) {
        return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr fc) {

        return null;
    }

    @Override
    public Void visitTypeCastExpr(TypeCastExpr tc) {
        return null;
    }

    @Override
    public Void visitValueAtExpr(ValueAtExpr va) {
        return null;
    }

    @Override
    public Void visitAddressOfExpr(AddressOfExpr ao) {
        return null;
    }

    @Override
    public Void visitIntLiteral(IntLiteral i) {
        return null;
    }

    @Override
    public Void visitChrLiteral(ChrLiteral c) {
        return null;
    }

    @Override
    public Void visitStrLiteral(StrLiteral str) {
        return null;
    }

    @Override
    public Void visitAdd(Add a) {
        return null;
    }

    @Override
    public Void visitSub(Sub s) {
        return null;
    }

    @Override
    public Void visitMul(Mul m) {
        return null;
    }

    @Override
    public Void visitDiv(Div d) {
        return null;
    }

    @Override
    public Void visitMod(Mod m) {
        return null;
    }

    @Override
    public Void visitGt(Gt g) {
        return null;
    }

    @Override
    public Void visitLt(Lt l) {
        return null;
    }

    @Override
    public Void visitGe(Ge g) {
        return null;
    }

    @Override
    public Void visitLe(Le l) {
        return null;
    }

    @Override
    public Void visitNe(Ne n) {
        return null;
    }

    @Override
    public Void visitEq(Eq e) {
        return null;
    }

    @Override
    public Void visitOr(Or o) {
        return null;
    }

    @Override
    public Void visitAnd(And a) {
        return null;
    }

    // TODO: to complete (should only deal with statements, expressions should be handled by the ExprGen or AddrGen)
}
