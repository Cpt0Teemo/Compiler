package sem;

import ast.*;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {

	@Override
	public Type visitBaseType(BaseType bt) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitStructType(StructType bt) {
		return null;
	}

	@Override
	public Type visitPointerType(PointerType bt) {
		return null;
	}

	@Override
	public Type visitArrayType(ArrayType bt) {
		return null;
	}

	@Override
	public Type visitStructTypeDecl(StructTypeDecl st) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitBlock(Block b) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitWhile(While w) {
		return null;
	}

	@Override
	public Type visitIf(If i) {
		return null;
	}

	@Override
	public Type visitReturn(Return r) {
		return null;
	}

	@Override
	public Type visitAssign(Assign a) {
		return null;
	}

	@Override
	public Type visitExprStmt(ExprStmt es) {
		return null;
	}

	@Override
	public Type visitFunDecl(FunDecl p) {
		// To be completed...
		return null;
	}


	@Override
	public Type visitProgram(Program p) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitVarDecl(VarDecl vd) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitVarExpr(VarExpr v) {
		// To be completed...
		return null;
	}

	// To be completed...


}
