package ast;

import gen.asm.Register;

import java.util.List;

public class FunCallExpr extends Expr{

    public FunDecl funDecl;
    public final String fnName;
    public final List<Expr> params;
    public Register returnRegister;

    public FunCallExpr(String fnName, List<Expr> params) {
        this.fnName = fnName;
        this.params = params;
    }


    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitFunCallExpr(this);
    }
}
