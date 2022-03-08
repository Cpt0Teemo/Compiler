package sem;

import ast.StructTypeDecl;

public class StructSymbol extends Symbol {

    StructTypeDecl structTypeDecl;

    public StructSymbol(StructTypeDecl structTypeDecl) {
        this.name = structTypeDecl.structType.name;
        this.structTypeDecl = structTypeDecl;
        this.isStruct = true;
    }
}
