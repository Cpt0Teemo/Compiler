package gen;

import ast.*;
import gen.asm.*;
import gen.asm.Label;

import java.awt.*;


/**
 * Generates code to evaluate an expression and return the result in a register.
 */
public class ExprGen implements ASTVisitor<Register> {

    private AssemblyProgram asmProg;
    private FunGen funGen;
    private AddrGen addrGen;

    public ExprGen(AssemblyProgram asmProg, FunGen funGen) {
        this.asmProg = asmProg;
        this.funGen = funGen;
        this.addrGen = new AddrGen(asmProg, this);
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
        b.varDecls.forEach(x -> x.accept(this));
        b.stmts.forEach(x -> x.accept(this));
        return null;
    }

    @Override
    public Register visitWhile(While w) {
        Label bodyLabel = Label.create("BODY_WHILE");
        Label endLabel = Label.create("END_WHILE");
        asmProg.getCurrentSection().emit(bodyLabel);
        Register condReg = w.expr.accept(this);
        asmProg.getCurrentSection().emit(OpCode.BEQ, Register.Arch.zero, condReg, endLabel);
        w.stmt.accept(this);
        asmProg.getCurrentSection().emit(OpCode.J, bodyLabel);
        asmProg.getCurrentSection().emit(endLabel);
        return null;
    }

    @Override
    public Register visitIf(If i) {
        Register ifReg = i.expr.accept(this);
        Label elseLabel = Label.create("ELSE_IF");
        Label endLabel = Label.create("END_IF");
        if(i.elseStmt == null) {
            asmProg.getCurrentSection().emit(OpCode.BEQ, Register.Arch.zero, ifReg, endLabel);
            i.ifStmt.accept(this);
            asmProg.getCurrentSection().emit(endLabel);
        } else {
            asmProg.getCurrentSection().emit(OpCode.BEQ, Register.Arch.zero, ifReg, elseLabel);
            i.ifStmt.accept(this);
            asmProg.getCurrentSection().emit(OpCode.J, endLabel);
            asmProg.getCurrentSection().emit(elseLabel);
            i.elseStmt.accept(this);
            asmProg.getCurrentSection().emit(endLabel);
        }
        return null;
    }

    @Override
    public Register visitReturn(Return r) {
        if(r.expr != null) {
            Register returnReg = r.expr.accept(this);
            int returnSize = r.expr.type == BaseType.CHAR ? 4 : r.expr.type.getSize(); //TODO fix for structs
            asmProg.getCurrentSection().emit(OpCode.SW, returnReg, Register.Arch.fp, 4 + returnSize);
        }
        //End function procedure
        //Pop local variables
        int sizeOfLocalVars = funGen.currentFun.block.varDecls.size() == 0 ? 0 : funGen.currentFun.block.varDecls.get(0).totalOffset;
        asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, sizeOfLocalVars);
        //Pop registers from stack
        asmProg.getCurrentSection().emit(OpCode.POP_REGISTERS);
        //Get previous frame pointer
        asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, 4);
        asmProg.getCurrentSection().emit(OpCode.LW, Register.Arch.fp, Register.Arch.fp, 0);
        //Return to previous function
        asmProg.getCurrentSection().emit(OpCode.JR, Register.Arch.ra);
        return null;
    }

    @Override
    public Register visitAssign(Assign a) {
        Register addrReg = a.leftExpr.accept(addrGen);
        Register valReg = a.rightExpr.accept(this);
        if(a.leftExpr.type == BaseType.CHAR)
            asmProg.getCurrentSection().emit(OpCode.SB, valReg, addrReg, 0);
        else
            asmProg.getCurrentSection().emit(OpCode.SW, valReg, addrReg, 0);
        return null;
    }

    @Override
    public Register visitExprStmt(ExprStmt es) {
        return es.expr.accept(this);
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
        Register addrReg = v.accept(addrGen);
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.LW, register, addrReg, 0);
        return register;
    }

    @Override
    public Register visitSizeOfExpr(SizeOfExpr so) {
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.LI, register, so.type.getSize());
        return register;
    }

    @Override
    public Register visitArrayAccessExpr(ArrayAccessExpr so) {

        Register register = Register.Virtual.create();
        Register addrReg = so.accept(addrGen);

        if(so.type == BaseType.CHAR)
            asmProg.getCurrentSection().emit(OpCode.LB, register, addrReg, 0);
        else
            asmProg.getCurrentSection().emit(OpCode.LW, register, addrReg, 0);
        return register;
    }

    @Override
    public Register visitFieldAccessExpr(FieldAccessExpr fa) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitFunCallExpr(FunCallExpr fc) {
        Register register = Register.Virtual.create();
        if(fc.fnName.matches("print_i")) {
            Register valueReg = fc.params.get(0).accept(this);
            asmProg.getCurrentSection().emit(new Comment("Print_i function call"));
            asmProg.getCurrentSection().emit(OpCode.LI, Register.Arch.v0, 1);
            asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.a0, valueReg, 0);
            asmProg.getCurrentSection().emit(OpCode.SYSCALL);
        } else if(fc.fnName.matches("print_c")) {
            Register valueReg = fc.params.get(0).accept(this);
            asmProg.getCurrentSection().emit(new Comment("Print_c function call"));
            asmProg.getCurrentSection().emit(OpCode.LI, Register.Arch.v0, 11);
            asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.a0, valueReg, 0);
            asmProg.getCurrentSection().emit(OpCode.SYSCALL);
        } else if(fc.fnName.matches("print_s")) {
            Register strReg = fc.params.get(0).accept(this);
            asmProg.getCurrentSection().emit(new Comment("Print_s function call"));
            asmProg.getCurrentSection().emit(OpCode.LI, Register.Arch.v0, 4);
            asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.a0, strReg, 0);
            asmProg.getCurrentSection().emit(OpCode.SYSCALL);
        } else if(fc.fnName.matches("read_c")) {
            asmProg.getCurrentSection().emit(new Comment("Read_c function call"));
            asmProg.getCurrentSection().emit(OpCode.LI, Register.Arch.v0, 12);
            asmProg.getCurrentSection().emit(OpCode.SYSCALL);
            asmProg.getCurrentSection().emit(OpCode.ADDI, register, Register.Arch.v0, 0);
        } else if(fc.fnName.matches("read_i")) {
            asmProg.getCurrentSection().emit(new Comment("Read_i function call"));
            asmProg.getCurrentSection().emit(OpCode.LI, Register.Arch.v0, 5);
            asmProg.getCurrentSection().emit(OpCode.SYSCALL);
            asmProg.getCurrentSection().emit(OpCode.ADDI, register, Register.Arch.v0, 0);
        } else if(fc.fnName.matches("mcmalloc")) {
            asmProg.getCurrentSection().emit(new Comment("Mcmalloc function call"));
            Register sizeReg = fc.params.get(0).accept(this);
            asmProg.getCurrentSection().emit(OpCode.LI, Register.Arch.v0, 9);
            asmProg.getCurrentSection().emit(OpCode.ADDI, Register.Arch.a0, sizeReg, 0);
            asmProg.getCurrentSection().emit(OpCode.SYSCALL);
            asmProg.getCurrentSection().emit(OpCode.ADDI, register, Register.Arch.v0, 0);
        } else {
            fc.accept(funGen);
            //TODO implement get returned value
            register = fc.returnRegister;
        }
        return register;
    }

    @Override
    public Register visitTypeCastExpr(TypeCastExpr tc) {
        if(tc.castType instanceof PointerType || tc.castType instanceof ArrayType) {
            return tc.expr.accept(addrGen);
        } else
            return tc.expr.accept(this);
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
        return ao.expr.accept(addrGen);
    }

    @Override
    public Register visitIntLiteral(IntLiteral i) {
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.LI, register, i.value);
        return register;
    }

    @Override
    public Register visitChrLiteral(ChrLiteral c) {
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.LI, register, c.value);
        return register;
    }

    @Override
    public Register visitStrLiteral(StrLiteral str) {
        return str.accept(addrGen);
    }

    @Override
    public Register visitAdd(Add a) {
        Register lReg = a.left.accept(this);
        Register rReg = a.right.accept(this);
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.ADD, register, lReg, rReg);
        return register;
    }

    @Override
    public Register visitSub(Sub s) {
        Register lReg = s.left.accept(this);
        Register rReg = s.right.accept(this);
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.SUB, register, lReg, rReg);
        return register;
    }

    @Override
    public Register visitMul(Mul m) {
        Register lReg = m.left.accept(this);
        Register rReg = m.right.accept(this);
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.MULT, lReg, rReg);
        asmProg.getCurrentSection().emit(OpCode.MFLO, register);
        return register;
    }

    @Override
    public Register visitDiv(Div d) {
        Register lReg = d.left.accept(this);
        Register rReg = d.right.accept(this);
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.DIV, lReg, rReg);
        asmProg.getCurrentSection().emit(OpCode.MFLO, register);
        return register;
    }

    @Override
    public Register visitMod(Mod m) {
        Register lReg = m.left.accept(this);
        Register rReg = m.right.accept(this);
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.DIV, lReg, rReg);
        asmProg.getCurrentSection().emit(OpCode.MFHI, register);
        return register;
    }

    @Override
    public Register visitGt(Gt g) {
        Register lReg = g.left.accept(this);
        Register rReg = g.right.accept(this);
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.SLT, register, rReg, lReg);
        return register;
    }

    @Override
    public Register visitLt(Lt l) {
        Register lReg = l.left.accept(this);
        Register rReg = l.right.accept(this);
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.SLT, register, lReg, rReg);
        return register;
    }

    @Override
    public Register visitGe(Ge g) {
        Register lReg = g.left.accept(this);
        Register rReg = g.right.accept(this);
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.SLT, register, lReg, rReg);
        asmProg.getCurrentSection().emit(OpCode.XORI, register, register, 1);
        return register;
    }

    @Override
    public Register visitLe(Le l) {
        Register lReg = l.left.accept(this);
        Register rReg = l.right.accept(this);
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.SLT, register, rReg, lReg);
        asmProg.getCurrentSection().emit(OpCode.XORI, register, register, 1);
        return register;
    }

    @Override
    public Register visitNe(Ne n) {
        Register lReg = n.left.accept(this);
        Register rReg = n.right.accept(this);
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.XOR, register, lReg, rReg);
        asmProg.getCurrentSection().emit(OpCode.SLTU, register, Register.Arch.zero, register);
        return register;
    }

    @Override
    public Register visitEq(Eq e) {
        Register lReg = e.left.accept(this);
        Register rReg = e.right.accept(this);
        Register register = Register.Virtual.create();
        asmProg.getCurrentSection().emit(OpCode.XOR, register, lReg, rReg);
        asmProg.getCurrentSection().emit(OpCode.SLTIU, register, register, 1);
        return register;
    }

    @Override
    public Register visitOr(Or o) {
        Label trueLabel = Label.create("OR_TRUE");
        Label endLabel = Label.create("OR_END");
        Register lReg = o.left.accept(this);
        Register register = Register.Virtual.create();

        asmProg.getCurrentSection().emit(OpCode.BNE, lReg, Register.Arch.zero, trueLabel);
        Register rReg = o.right.accept(this);
        asmProg.getCurrentSection().emit(OpCode.BNE, rReg, Register.Arch.zero, trueLabel);
        asmProg.getCurrentSection().emit(OpCode.LI, register, 0);
        asmProg.getCurrentSection().emit(OpCode.J, endLabel);

        asmProg.getCurrentSection().emit(trueLabel);
        asmProg.getCurrentSection().emit(OpCode.LI, register, 1);

        asmProg.getCurrentSection().emit(endLabel);
        return register;
    }

    @Override
    public Register visitAnd(And a) {
        Label falseLabel = Label.create("AND_FALSE");
        Label endLabel = Label.create("AND_END");
        Register lReg = a.left.accept(this);
        Register register = Register.Virtual.create();

        asmProg.getCurrentSection().emit(OpCode.BEQ, lReg, Register.Arch.zero, falseLabel);
        Register rReg = a.right.accept(this);
        asmProg.getCurrentSection().emit(OpCode.BEQ, rReg, Register.Arch.zero, falseLabel);
        asmProg.getCurrentSection().emit(OpCode.LI, register, 1);
        asmProg.getCurrentSection().emit(OpCode.J, endLabel);

        asmProg.getCurrentSection().emit(falseLabel);
        asmProg.getCurrentSection().emit(OpCode.LI, register, 0);

        asmProg.getCurrentSection().emit(endLabel);
        return register;
    }

}
