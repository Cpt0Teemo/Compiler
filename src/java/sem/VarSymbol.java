package sem;

import ast.VarDecl;

public class VarSymbol extends Symbol{

    public VarDecl vd;

    public VarSymbol(VarDecl vd) {
        this.name = vd.varName;
        this.vd = vd;
        this.isVar = true;
    }
}
