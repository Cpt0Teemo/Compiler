package ast;

import java.io.PrintWriter;

public class ASTPrinter implements ASTVisitor<Void> {

    private PrintWriter writer;

    public ASTPrinter(PrintWriter writer) {
            this.writer = writer;
    }

    @Override
    public Void visitBlock(Block b) {
        writer.print("Block(");
        String delimiter = "";
        for (VarDecl vd : b.varDecls) {
            writer.print(delimiter);
            vd.accept(this);
            delimiter = ",";
        }
        for (Stmt stmt : b.stmts) {
            writer.print(delimiter);
            stmt.accept(this);
            delimiter = ",";
        }
        writer.print(")");
        return null;
    }

    @Override
    public Void visitWhile(While w) {
        writer.print("While(");
        w.expr.accept(this);
        writer.print(",");
        w.stmt.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitIf(If i) {
        writer.print("If(");
        i.expr.accept(this);
        writer.print(",");
        i.ifStmt.accept(this);
        if(i.elseStmt != null) {
            writer.print(",");
            i.elseStmt.accept(this);
        }
        writer.print(")");
        return null;
    }

    @Override
    public Void visitReturn(Return r) {
        writer.print("Return(");
        r.expr.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitAssign(Assign a) {
        writer.print("Assign(");
        a.leftExpr.accept(this);
        writer.print(",");
        a.rightExpr.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt es) {
        writer.print("ExprStmt(");
        es.expr.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl fd) {
        writer.print("FunDecl(");
        fd.type.accept(this);
        writer.print(","+fd.name+",");
        for (VarDecl vd : fd.params) {
            vd.accept(this);
            writer.print(",");
        }
        fd.block.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitProgram(Program p) {
        writer.print("Program(");
        String delimiter = "";
        for (StructTypeDecl std : p.structTypeDecls) {
            writer.print(delimiter);
            delimiter = ",";
            std.accept(this);
        }
        for (VarDecl vd : p.varDecls) {
            writer.print(delimiter);
            delimiter = ",";
            vd.accept(this);
        }
        for (FunDecl fd : p.funDecls) {
            writer.print(delimiter);
            delimiter = ",";
            fd.accept(this);
        }
        writer.print(")");
	    writer.flush();
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl vd){
        writer.print("VarDecl(");
        vd.type.accept(this);
        writer.print(","+vd.varName);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr v) {
        writer.print("VarExpr(");
        writer.print(v.name);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitSizeOfExpr(SizeOfExpr so) {
        writer.print("SizeOf(");
        so.type.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitArrayAccessExpr(ArrayAccessExpr ac) {
        writer.print("ArrayAccess(");
        ac.array.accept(this);
        writer.print(",");
        ac.index.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitFieldAccessExpr(FieldAccessExpr fa) {
        writer.print("FieldAccess(");
        fa.expr.accept(this);
        writer.print(",");
        writer.print(fa.field);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr fc) {
        writer.print("FunCallExpr(");
        writer.print(fc.fnName);
        String delimiter = "";
        for (Expr expr: fc.params) {
            writer.print(",");
            expr.accept(this);
        }
        writer.print(")");
        return null;
    }

    @Override
    public Void visitTypeCastExpr(TypeCastExpr tc) {
        writer.print("TypecastExpr(");
        tc.castType.accept(this);
        writer.print(",");
        tc.expr.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitValueAtExpr(ValueAtExpr va) {
        writer.print("ValueAt(");
        va.expr.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitAddressOfExpr(AddressOfExpr ao) {
        writer.print("AddressOf(");
        ao.expr.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitIntLiteral(IntLiteral i) {
        writer.print("IntLiteral(");
        writer.print(i.value);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitChrLiteral(ChrLiteral c) {
        writer.print("CharLiteral(");
        writer.print(c.value);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitStrLiteral(StrLiteral str) {
        writer.print("StrLiteral(");
        writer.print(str.str);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitAdd(Add a) {
        writer.print("BinOp(");
        a.left.accept(this);
        writer.print(",ADD,");
        a.right.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitSub(Sub s) {
        writer.print("BinOp(");
        s.left.accept(this);
        writer.print(",SUB,");
        s.right.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitMul(Mul m) {
        writer.print("BinOp(");
        m.left.accept(this);
        writer.print(",MUL,");
        m.right.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitDiv(Div d) {
        writer.print("BinOp(");
        d.left.accept(this);
        writer.print(",DIV,");
        d.right.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitMod(Mod m) {
        writer.print("BinOp(");
        m.left.accept(this);
        writer.print(",MOD,");
        m.right.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitGt(Gt g) {
        writer.print("BinOp(");
        g.left.accept(this);
        writer.print(",GT,");
        g.right.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitLt(Lt l) {
        writer.print("BinOp(");
        l.left.accept(this);
        writer.print(",LT,");
        l.right.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitGe(Ge g) {
        writer.print("BinOp(");
        g.left.accept(this);
        writer.print(",GE,");
        g.right.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitLe(Le l) {
        writer.print("BinOp(");
        l.left.accept(this);
        writer.print(",LE,");
        l.right.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitNe(Ne n) {
        writer.print("BinOp(");
        n.left.accept(this);
        writer.print(",NE,");
        n.right.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitEq(Eq e) {
        writer.print("BinOp(");
        e.left.accept(this);
        writer.print(",EQ,");
        e.right.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitOr(Or o) {
        writer.print("BinOp(");
        o.left.accept(this);
        writer.print(",OR,");
        o.right.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitAnd(And a) {
        writer.print("BinOp(");
        a.left.accept(this);
        writer.print(",AND,");
        a.right.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitBaseType(BaseType bt) {
       writer.print(bt.name());
       return null;
    }

    @Override
    public Void visitStructType(StructType bt) {
        writer.print("StructType(");
        writer.print(bt.name);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitPointerType(PointerType bt) {
        writer.print("PointerType(");
        bt.type.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitArrayType(ArrayType at) {
        writer.print("ArrayType(");
        at.type.accept(this);
        writer.print(",");
        writer.print(at.size);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitStructTypeDecl(StructTypeDecl st) {
        writer.print("StructTypeDecl(");
        writer.print(st.name);
        for (VarDecl varDecl: st.varDecls) {
            writer.print(",");
            varDecl.accept(this);
        }
        writer.print(")");
        return null;
    }
    
}
