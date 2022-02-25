package sem;

import ast.FunDecl;

public class FunSymbol extends Symbol{

    public FunDecl fd;

    public FunSymbol(FunDecl fd) {
        this.name = fd.name;
        this.fd = fd;
        this.isFun = true;
    }
}
