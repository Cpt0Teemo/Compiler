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

	@Override
	public Type visitSizeOfExpr(SizeOfExpr so) {
		return null;
	}

	@Override
	public Type visitArrayAccessExpr(ArrayAccessExpr so) {
		return null;
	}

	@Override
	public Type visitFieldAccessExpr(FieldAccessExpr fa) {
		return null;
	}

	@Override
	public Type visitFunCallExpr(FunCallExpr fc) {
		return null;
	}

	@Override
	public Type visitTypeCastExpr(TypeCastExpr tc) {
		return null;
	}

	@Override
	public Type visitValueAtExpr(ValueAtExpr va) {
		return null;
	}

	@Override
	public Type visitAddressOfExpr(AddressOfExpr ao) {
		return null;
	}

	@Override
	public Type visitIntLiteral(IntLiteral i) {
		return null;
	}

	@Override
	public Type visitChrLiteral(ChrLiteral c) {
		return null;
	}

	@Override
	public Type visitStrLiteral(StrLiteral str) {
		return null;
	}

	@Override
	public Type visitAdd(Add a) {
		return null;
	}

	@Override
	public Type visitSub(Sub s) {
		return null;
	}

	@Override
	public Type visitMul(Mul m) {
		return null;
	}

	@Override
	public Type visitDiv(Div d) {
		return null;
	}

	@Override
	public Type visitMod(Mod m) {
		return null;
	}

	@Override
	public Type visitGt(Gt g) {
		return null;
	}

	@Override
	public Type visitLt(Lt l) {
		return null;
	}

	@Override
	public Type visitGe(Ge g) {
		return null;
	}

	@Override
	public Type visitLe(Le l) {
		return null;
	}

	@Override
	public Type visitNe(Ne n) {
		return null;
	}

	@Override
	public Type visitEq(Eq e) {
		return null;
	}

	@Override
	public Type visitOr(Or o) {
		return null;
	}

	@Override
	public Type visitAnd(And a) {
		return null;
	}

	// To be completed...


}
