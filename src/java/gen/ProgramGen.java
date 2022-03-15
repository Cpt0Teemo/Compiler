package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.Directive;
import gen.asm.Label;
import gen.asm.OpCode;

/**
 * This visitor should produce a program. Its job is simply to handle the global variable declaration by allocating
 * these in the data section. Then it should call the FunGen function generator to process each function declaration.
 * The label corresponding to each global variable can either be stored in the VarDecl AST node (simplest solution)
 * or store in an ad-hoc data structure (i.e. a Map) that can be passed to the other visitors.
 */
public class ProgramGen implements ASTVisitor<Void> {

    private final AssemblyProgram asmProg;
    private final FunGen funGen;
    public final Label main;

    private final AssemblyProgram.Section dataSection ;

    public ProgramGen(AssemblyProgram asmProg) {
        this.asmProg = asmProg;
        this.dataSection = asmProg.newSection(AssemblyProgram.Section.Type.DATA);
        this.funGen = new FunGen(asmProg, this);
        this.main = Label.create("MAIN");
    }

    @Override
    public Void visitFunDecl(FunDecl fd) {
        // call the visitor specialized for handling function declaration
        return funGen.visitFunDecl(fd);
    }

    @Override
    public Void visitProgram(Program p) {
        p.varDecls.forEach(vd -> vd.accept(this));
        AssemblyProgram.Section section = asmProg.newSection(AssemblyProgram.Section.Type.TEXT);
        section.emit(OpCode.J, main);
        p.funDecls.forEach(fd -> fd.accept(this));
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl vd) {
        vd.label = Label.create(vd.varName);
        dataSection.emit(vd.label);
        if(vd.type != BaseType.CHAR)
            dataSection.emit(new Directive("space " + vd.type.getSize()));
        else
            dataSection.emit(new Directive("space " + 4));
        // TODO: to complete: declare the variable globally in the data section and remember its label somewhere (e.g. in the VarDecl AST node directly).
        return null;
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
        return null;
    }

    @Override
    public Void visitBlock(Block b)  {
        throw new ShouldNotReach();
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
    public Void visitVarExpr(VarExpr v) {
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

    // TODO: to complete (all the other visit methods should throw SholdNotReach)


}
