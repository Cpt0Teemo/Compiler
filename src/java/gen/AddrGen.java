package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.Register;

/**
 * Generates code to calculate the address of an expression and return the result in a register.
 */
public class AddrGen implements ASTVisitor<Register> {


    private AssemblyProgram asmProg;

    public AddrGen(AssemblyProgram asmProg) {
        this.asmProg = asmProg;
    }

    @Override
    public Register visitBaseType(BaseType bt) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitStructType(StructType bt) { throw new ShouldNotReach(); }

    @Override
    public Register visitPointerType(PointerType bt) { throw new ShouldNotReach(); }

    @Override
    public Register visitArrayType(ArrayType bt) { throw new ShouldNotReach(); }

    @Override
    public Register visitStructTypeDecl(StructTypeDecl st) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitBlock(Block b) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitWhile(While w) { throw new ShouldNotReach(); }

    @Override
    public Register visitIf(If i) { throw new ShouldNotReach(); }

    @Override
    public Register visitReturn(Return r) { throw new ShouldNotReach(); }

    @Override
    public Register visitAssign(Assign a) { throw new ShouldNotReach(); }

    @Override
    public Register visitExprStmt(ExprStmt es) { throw new ShouldNotReach(); }

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
        // TODO: to complete
        return null;
    }

    @Override
    public Register visitSizeOfExpr(SizeOfExpr so) {
        return null;
    }

    @Override
    public Register visitArrayAccessExpr(ArrayAccessExpr so) {
        return null;
    }

    @Override
    public Register visitFieldAccessExpr(FieldAccessExpr fa) {
        return null;
    }

    @Override
    public Register visitFunCallExpr(FunCallExpr fc) {
        return null;
    }

    @Override
    public Register visitTypeCastExpr(TypeCastExpr tc) {
        return null;
    }

    @Override
    public Register visitValueAtExpr(ValueAtExpr va) {
        return null;
    }

    @Override
    public Register visitAddressOfExpr(AddressOfExpr ao) {
        return null;
    }

    @Override
    public Register visitIntLiteral(IntLiteral i) {
        return null;
    }

    @Override
    public Register visitChrLiteral(ChrLiteral c) {
        return null;
    }

    @Override
    public Register visitStrLiteral(StrLiteral str) {
        return null;
    }

    @Override
    public Register visitAdd(Add a) {
        return null;
    }

    @Override
    public Register visitSub(Sub s) {
        return null;
    }

    @Override
    public Register visitMul(Mul m) {
        return null;
    }

    @Override
    public Register visitDiv(Div d) {
        return null;
    }

    @Override
    public Register visitMod(Mod m) {
        return null;
    }

    @Override
    public Register visitGt(Gt g) {
        return null;
    }

    @Override
    public Register visitLt(Lt l) {
        return null;
    }

    @Override
    public Register visitGe(Ge g) {
        return null;
    }

    @Override
    public Register visitLe(Le l) {
        return null;
    }

    @Override
    public Register visitNe(Ne n) {
        return null;
    }

    @Override
    public Register visitEq(Eq e) {
        return null;
    }

    @Override
    public Register visitOr(Or o) {
        return null;
    }

    @Override
    public Register visitAnd(And a) {
        return null;
    }

    // TODO: to complete (only deal with Expression nodes, anything else should throw ShouldNotReach)

}
