package gen;

import ast.*;
import gen.asm.*;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * A visitor that produces code for a function declaration
 */
public class FunGen implements ASTVisitor<Void> {

    private AssemblyProgram asmProg;
    private ProgramGen programGen;
    private ExprGen exprGen;
    public FunDecl currentFun;
    public Block currentBlock;
    public int funCallSPOffset;

    public FunGen(AssemblyProgram asmProg, ProgramGen programGen) {
        this.asmProg = asmProg;
        this.programGen = programGen;
        this.exprGen = new ExprGen(asmProg, this);
        this.funCallSPOffset = 0;
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
        currentBlock = b;
        int offset = 0;
        for(int i = b.varDecls.size(); i > 0; i--) {
            VarDecl varDecl = b.varDecls.get(i-1);
            varDecl.offset = offset;
            offset += varDecl.type == BaseType.CHAR ? 4 : varDecl.type.getSize();
        }
        for(VarDecl varDecl: b.varDecls) {
            varDecl.totalOffset = offset;
            varDecl.accept(this);
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
        this.currentFun = p;
        //Setup function arguments
        int offset = 0;
        for(int i = p.params.size(); i > 0; i--) {
            VarDecl varDecl = p.params.get(i-1);
            varDecl.offset =  offset;
            varDecl.isParam = true;
            varDecl.fd = p;
            int typeSize = varDecl.type == BaseType.CHAR ? 4 : varDecl.type.getSize();
            offset += typeSize;
        }
        int finalOffset = offset;
        p.params.forEach(x -> x.paramsOffset = finalOffset); //TODO remove

        // Each function should be produced in its own section.
        // This is necessary for the register allocator.
        asmProg.newSection(AssemblyProgram.Section.Type.TEXT);

        if(p.name.equalsIgnoreCase("main"))
            p.label = this.programGen.main;
        else
            p.label = Label.create(p.name);
        asmProg.getCurrentSection().emit(p.label);

        //Initialise frame pointer
        asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -4);
        asmProg.getCurrentSection().emit(OpCode.SW, Register.Arch.fp, Register.Arch.sp, 0);
        asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.fp, Register.Arch.sp, 0);
        //Push registers to stack
        asmProg.getCurrentSection().emit(OpCode.PUSH_REGISTERS);
        //Run function
        p.block.accept(this);
        //Pop local variables
        int sizeOfLocalVars = p.block.varDecls.size() == 0 ? 0 : p.block.varDecls.get(0).totalOffset;
        asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, sizeOfLocalVars);
        //Pop registers from stack
        asmProg.getCurrentSection().emit(OpCode.POP_REGISTERS);
        //Get previous frame pointer
        asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, 4);
        asmProg.getCurrentSection().emit(OpCode.LW, Register.Arch.fp, Register.Arch.fp, 0);
        //Return to previous function
        if(!p.name.equalsIgnoreCase("main"))
            asmProg.getCurrentSection().emit(OpCode.JR, Register.Arch.ra);
        return null;
    }

    @Override
    public Void visitProgram(Program p) {
        throw new ShouldNotReach();
    }

    @Override
    public Void visitVarDecl(VarDecl vd) {
        // TODO: should allocate local variables on the stack and remember the offset from the frame pointer where they are stored (e.g. in the VarDecl AST node)
        if(vd.type != BaseType.CHAR) {
            for(int i = 0; i < vd.type.getSize(); i += 4 )
                asmProg.getCurrentSection().emit(OpCode.SW, Register.Arch.zero, Register.Arch.sp, -4);
            asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -vd.type.getSize());
        }
        else{
            asmProg.getCurrentSection().emit(OpCode.SW, Register.Arch.zero, Register.Arch.sp, -4);
            asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -4);
        }
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
        //Push arguments on stack
        int offset = 0;
        for(Expr expr: fc.params) {
            int typeSize = expr.type == BaseType.CHAR ? 4 : expr.type.getSize();
            Register paramReg = expr.accept(exprGen);
            asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -typeSize); //TODO structs?
            funCallSPOffset += typeSize;
            asmProg.getCurrentSection().emit(new Comment("Storing Parameter size: " + expr.type.getSize()));
            if(expr.type == BaseType.CHAR)
                asmProg.getCurrentSection().emit(OpCode.SB, paramReg, Register.Arch.sp, 0);
            else
                asmProg.getCurrentSection().emit(OpCode.SW, paramReg, Register.Arch.sp, 0);
            offset += typeSize;
        }
        //Reserve space for return value
        int returnSize = fc.funDecl.type == BaseType.CHAR ? 4 : fc.funDecl.type.getSize(); //TODO fix for structs
        asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -returnSize);
        funCallSPOffset += returnSize;
        //Push return address on stack
        asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -4);
        funCallSPOffset += 4;
        asmProg.getCurrentSection().emit(OpCode.SW, Register.Arch   .ra, Register.Arch.sp, 0);
        //Call function
        asmProg.getCurrentSection().emit(OpCode.JAL, fc.funDecl.label);
        //Restore return address
        asmProg.getCurrentSection().emit(OpCode.LW, Register.Arch.ra, Register.Arch.sp, 0);
        //Get return value
        fc.returnRegister = Register.Virtual.create();
        if(fc.type == BaseType.CHAR)
            asmProg.getCurrentSection().emit(OpCode.LB, fc.returnRegister, Register.Arch.sp, returnSize); //TODO fix for structs
        else
            asmProg.getCurrentSection().emit(OpCode.LW, fc.returnRegister, Register.Arch.sp, returnSize); //TODO fix for structs
        //Reset stack pointer
        int totalOffset = 4 + returnSize + offset;
        asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, totalOffset);
        funCallSPOffset = 0;
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
