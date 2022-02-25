package sem;

import ast.StructTypeDecl;

public class StructSymbol extends Symbol {

    StructTypeDecl structTypeDecl;

    public StructSymbol(StructTypeDecl structTypeDecl) {
        this.structTypeDecl = structTypeDecl;
        this.isStruct = true;
    }
}
