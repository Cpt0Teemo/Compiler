package gen;

import ast.*;
import gen.asm.*;

import java.util.Collections;

/**
 * Generates code to calculate the address of an expression and return the result in a register.
 */
public class AddrGen implements ASTVisitor<Register> {


    private AssemblyProgram asmProg;
    private ExprGen exprGen;
    private FunGen funGen;

    public AddrGen(AssemblyProgram asmProg, ExprGen exprGen, FunGen funGen) {
        this.asmProg = asmProg;
        this.exprGen = exprGen;
        this.funGen = funGen;
    }

    @Override
    public Register visitBaseType(BaseType bt) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitStructType(StructType st) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitPointerType(PointerType pt) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitArrayType(ArrayType at) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitStructTypeDecl(StructTypeDecl st) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitBlock(Block b) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitWhile(While w) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitIf(If i) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitReturn(Return r) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitAssign(Assign a) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitExprStmt(ExprStmt es) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitFunDecl(FunDecl p) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitProgram(Program p) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitVarDecl(VarDecl vd) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitVarExpr(VarExpr v) {
        Register register = Register.Virtual.create();
        if(v.vd.isStaticData()) {
            asmProg.getCurrentSection().emit(OpCode.LA, register, v.vd.label);
        } else if(!v.vd.isParam){
            int offset = v.vd.offset + funGen.funCallSPOffset + calculateBlockOffset(v.vd.b);
            asmProg.getCurrentSection().emit(OpCode.ADDI, register, Register.Arch.sp, v.vd.offset + funGen.funCallSPOffset); //TODO structs
        } else if(v.vd.isParam){
            int returnSize = v.vd.fd.type == BaseType.CHAR ? 4 : v.vd.fd.type.getSize(); //TODO fix structs
            int offset = 4 + returnSize + 4 + v.vd.offset; //RA + Return + 4 (read the last word for reserved param)
            asmProg.getCurrentSection().emit(new Comment("Retrieving Parameter: " + v.vd.varName));
            asmProg.getCurrentSection().emit(OpCode.ADDI, register, Register.Arch.fp, offset);
        }
        return register;
    }

    private int calculateBlockOffset(Block b){
        return b != null && b.nextBlock != null ? b.nextBlock.memSize + calculateBlockOffset(b.nextBlock) : 0;
    }

    @Override
    public Register visitSizeOfExpr(SizeOfExpr so) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitArrayAccessExpr(ArrayAccessExpr so) {
        int offsetSize = so.type.getSize();
        Register tempReg = Register.Virtual.create();
        Register offsetReg = Register.Virtual.create();
        Register register = Register.Virtual.create();
        Register addrReg = so.array.accept(this);
        Register indexReg = so.index.accept(exprGen);
        asmProg.getCurrentSection().emit(OpCode.LI, tempReg, offsetSize);
        asmProg.getCurrentSection().emit(OpCode.MULT, indexReg, tempReg);
        asmProg.getCurrentSection().emit(OpCode.MFLO, offsetReg);
        asmProg.getCurrentSection().emit(OpCode.ADD, register, addrReg, offsetReg);

        return register;
    }

    @Override
    public Register visitFieldAccessExpr(FieldAccessExpr fa) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitFunCallExpr(FunCallExpr fc) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitTypeCastExpr(TypeCastExpr tc) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitValueAtExpr(ValueAtExpr va) {
        Register register = Register.Virtual.create();
        Register exprReg = va.expr.accept(this);
        if(va.type == BaseType.CHAR)
            asmProg.getCurrentSection().emit(OpCode.LB, register, exprReg, 0);
        else
            asmProg.getCurrentSection().emit(OpCode.LW, register, exprReg, 0);
        return register;
    }

    @Override
    public Register visitAddressOfExpr(AddressOfExpr ao) {
        return ao.expr.accept(this);
    }

    @Override
    public Register visitIntLiteral(IntLiteral i) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitChrLiteral(ChrLiteral c) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitStrLiteral(StrLiteral str) {
        str.label =  Label.create();
        Register register = Register.Virtual.create();
        asmProg.sections.get(0).emit(str.label);
        int padding = 4 - ((str.str.length()+1)%4);
        asmProg.sections.get(0).emit(new Directive("asciiz \"" + decodeString(str.str) +  "\""));
        asmProg.sections.get(0).emit(new Directive("space " + padding));
        asmProg.getCurrentSection().emit(OpCode.LA, register, str.label);
        return register;
    }

    private String decodeString(String s) {
        String result = "";
        for(char c: s.toCharArray()) {
            switch (c) {
                case '\t': result += "\\t"; continue;
                case '\b': result += "\\b"; continue;
                case '\n': result += "\\n"; continue;
                case '\r': result += "\\r"; continue;
                case '\f': result += "\\f"; continue;
                case '\'': result += "'"; continue;
                case '"': result += "\""; continue;
                case '\\': result += "\\\\"; continue;
                case '\0': result += "\\0"; continue;
                default: result += c;
            }
        }
        return result;
    }

    @Override
    public Register visitAdd(Add a) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitSub(Sub s) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitMul(Mul m) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitDiv(Div d) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitMod(Mod m) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitGt(Gt g) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitLt(Lt l) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitGe(Ge g) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitLe(Le l) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitNe(Ne n) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitEq(Eq e) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitOr(Or o) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitAnd(And a) {
        throw new ShouldNotReach();
    }

    // TODO: to complete (only deal with Expression nodes, anything else should throw ShouldNotReach)

}
