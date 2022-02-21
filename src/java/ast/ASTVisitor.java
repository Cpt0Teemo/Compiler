package ast;

public interface ASTVisitor<T> {

    class ShouldNotReach extends Error {
        public ShouldNotReach() {
            super("Current visitor should never reach this node");
        }
    }

    public T visitBaseType(BaseType bt);
    public T visitStructType(StructType st);
    public T visitPointerType(PointerType pt);
    public T visitArrayType(ArrayType at);

    public T visitStructTypeDecl(StructTypeDecl st);
    public T visitFunDecl(FunDecl f);

    public T visitBlock(Block b);
    public T visitWhile(While w);
    public T visitIf(If i);
    public T visitReturn(Return r);
    public T visitAssign(Assign a);
    public T visitExprStmt(ExprStmt es);

    public T visitProgram(Program p);
    public T visitVarDecl(VarDecl vd);
    public T visitVarExpr(VarExpr v);
    public T visitSizeOfExpr(SizeOfExpr so);

    //Special Expressions
    public T visitArrayAccessExpr(ArrayAccessExpr so);
    public T visitFieldAccessExpr(FieldAccessExpr fa);
    public T visitFunCallExpr(FunCallExpr fc);
    public T visitTypeCastExpr(TypeCastExpr tc);
    public T visitValueAtExpr(ValueAtExpr va);
    public T visitAddressOfExpr(AddressOfExpr ao);

    //Types
    public T visitIntLiteral(IntLiteral i);
    public T visitChrLiteral(ChrLiteral c);
    public T visitStrLiteral(StrLiteral str);

    //Binary operators
    public T visitAdd(Add a);
    public T visitSub(Sub s);
    public T visitMul(Mul m);
    public T visitDiv(Div d);
    public T visitMod(Mod m);
    public T visitGt(Gt g);
    public T visitLt(Lt l);
    public T visitGe(Ge g);
    public T visitLe(Le l);
    public T visitNe(Ne n);
    public T visitEq(Eq e);
    public T visitOr(Or o);
    public T visitAnd(And a);

}
