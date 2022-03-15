package gen;

import ast.*;
import gen.asm.*;

import java.util.Locale;

/**
 * A visitor that produces code for a function declaration
 */
public class FunGen implements ASTVisitor<Void> {

    private AssemblyProgram asmProg;
    private ProgramGen programGen;
    private ExprGen exprGen;

    public FunGen(AssemblyProgram asmProg, ProgramGen programGen) {
        this.asmProg = asmProg;
        this.programGen = programGen;
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
        //Remove local variables
        asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, offset-4);
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
        //Setup function arguments
        int offset = 0;
        for(VarDecl varDecl: p.params) {
            varDecl.isParam = true;
            varDecl.fd = p;
            varDecl.offset = offset;
            offset += 4; //TODO fix this for structs
        }
        int finalOffset = offset;
        p.params.forEach(x -> x.paramsOffset = finalOffset);

        // Each function should be produced in its own section.
        // This is necessary for the register allocator.
        asmProg.newSection(AssemblyProgram.Section.Type.TEXT);

        if(p.name.toLowerCase().equals("main"))
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
        //Pop registers from stack
        asmProg.getCurrentSection().emit(OpCode.POP_REGISTERS);
        //Get previous frame pointer
        asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, 4);
        asmProg.getCurrentSection().emit(OpCode.LW, Register.Arch.fp, Register.Arch.fp, 0);
        //Return to previous function
        if(!p.name.toLowerCase().equals("main"))
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
        if(vd.type != BaseType.CHAR)
            asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -vd.type.getSize());
        else
            asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -4);
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
            Register paramReg = expr.accept(exprGen);
            asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -4); //TODO structs?
            asmProg.getCurrentSection().emit(OpCode.SW, paramReg, Register.Arch.sp, 0);
            offset += 4; //This needed? Depends on structs :p
        }
        //Reserve space for return value
        int returnSize = fc.funDecl.type.getSize();
        if(fc.funDecl.type == BaseType.CHAR) //This needed? Depends on structs :p
            returnSize = 4;
        asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -returnSize);
        //Push return address on stack
        asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -4);
        asmProg.getCurrentSection().emit(OpCode.SW, Register.Arch.ra, Register.Arch.sp, 0);
        //Call function
        asmProg.getCurrentSection().emit(OpCode.JAL, fc.funDecl.label);
        //Restore return address
        asmProg.getCurrentSection().emit(OpCode.LW, Register.Arch.ra, Register.Arch.sp, 0);
        //Get return value
        Register returnReg = Register.Virtual.create(); //TODO FIX THIS FOR STRUCTS
        asmProg.getCurrentSection().emit(OpCode.LW, returnReg, Register.Arch.sp, 4);
        //Reset stack pointer
        asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -(8 + offset));
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
