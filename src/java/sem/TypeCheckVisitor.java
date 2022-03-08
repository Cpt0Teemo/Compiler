package sem;

import ast.*;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {

	private Type currentReturnType;
	private boolean returnedAtlestOnce;

	@Override
	public Type visitBaseType(BaseType bt) {
		return bt;
	}

	@Override
	public Type visitStructType(StructType bt) {
		return bt;
	}

	@Override
	public Type visitPointerType(PointerType bt) {
		return bt;
	}

	@Override
	public Type visitArrayType(ArrayType bt) {
		return bt;
	}

	@Override
	public Type visitStructTypeDecl(StructTypeDecl st) {
		return st.structType;
	}

	@Override
	public Type visitBlock(Block b) {
		for(VarDecl varDecl : b.varDecls) {
			varDecl.accept(this);
		}
		for(Stmt stmt : b.stmts) {
			stmt.accept(this);
		}
		return null;
	}

	@Override
	public Type visitWhile(While w) {
		Type conditionT = w.expr.accept(this);
		if(!conditionT.isEqual(BaseType.INT))
			error("The condition for the while loop should be an INT");
		w.stmt.accept(this);
		return null;
	}

	@Override
	public Type visitIf(If i) {
		Type conditionT = i.expr.accept(this);
		if(!conditionT.isEqual(BaseType.INT))
			error("The condition for the if should be an INT");
		i.ifStmt.accept(this);
		if(i.elseStmt != null)
			i.elseStmt.accept(this);
		return null;
	}

	@Override
	public Type visitReturn(Return r) {
		if(r.expr != null) {
			Type returnT = r.expr.accept(this);
			if(!currentReturnType.isEqual(returnT))
				error("Error type must match function return type");
		} else {
			if(!currentReturnType.isEqual(BaseType.VOID))
				error("Error type must match function return type");
		}
		returnedAtlestOnce = true;
		return null;
	}

	@Override
	public Type visitAssign(Assign a) {
		Type leftT = a.leftExpr.accept(this);
		Type rightT = a.rightExpr.accept(this);
		if(leftT.isEqual(BaseType.VOID) || leftT instanceof ArrayType)
			error("Cant assign to void or array");
		return leftT;
	}

	@Override
	public Type visitExprStmt(ExprStmt es) {
		es.expr.accept(this);
		return null;
	}

	@Override
	public Type visitFunDecl(FunDecl p) {
		currentReturnType = p.type.accept(this);
		returnedAtlestOnce = false;
		for(VarDecl param : p.params) {
			param.accept(this);
		}
		p.block.accept(this);
		if(!p.type.isEqual(BaseType.VOID) && !returnedAtlestOnce)
			error("Function has no return when expected one");

		return null;
	}


	@Override
	public Type visitProgram(Program p) {
		for(StructTypeDecl structTypeDecl : p.structTypeDecls) {
			structTypeDecl.accept(this);
		}
		for(VarDecl varDecl : p.varDecls) {
			varDecl.accept(this);
		}
		for(FunDecl funDecl : p.funDecls) {
			funDecl.accept(this);
		}
		return null;
	}

	@Override
	public Type visitVarDecl(VarDecl vd) {
		if(vd.type.isEqual(BaseType.VOID))
			error("Can't declare void variable");
		return null;
	}

	@Override
	public Type visitVarExpr(VarExpr v) {
		v.type = v.vd.type;
		return v.type;
	}

	@Override
	public Type visitSizeOfExpr(SizeOfExpr so) {
		so.type = BaseType.INT;
		return so.type;
	}

	@Override
	public Type visitArrayAccessExpr(ArrayAccessExpr so) {
		Type arrayType = so.array.accept(this);
		Type indexType = so.index.accept(this);

		if(!(arrayType instanceof ArrayType) && !(arrayType instanceof PointerType))
			error("Array access can only be used on pointers and arrays");
		if(!indexType.isEqual(BaseType.INT))
			error("Array access index needs to be an int");
		if(arrayType instanceof  ArrayType)
			so.type = ((ArrayType) arrayType).type.accept(this);
		else
			so.type = ((PointerType) arrayType).type.accept(this);
		return so.type;
	}

	@Override
	public Type visitFieldAccessExpr(FieldAccessExpr fa) {
		Type exprType = fa.expr.accept(this);

		if(!(exprType instanceof StructType)) {
			error("Field access can only be used on structs");
			return null;
		}
		fa.type = ((StructType) exprType).structTypeDecl.structType;
		return fa.type;
	}

	@Override
	public Type visitFunCallExpr(FunCallExpr fc) {
		if(fc.funDecl.params.size() != fc.params.size())
			error("Function parameter amount is incorrect");
		for (int i=0; i<fc.params.size(); i++) {
			Type fnCallParamType = fc.params.get(i).accept(this);
			Type fnDeclParamType = fc.funDecl.params.get(i).type.accept(this);

			if(!fnCallParamType.isEqual(fnDeclParamType))
				error("Type mismatch in function call " + fc.fnName + "'s parameter");
		}
		fc.type = fc.funDecl.type;
		return fc.type;
	}

	@Override
	public Type visitTypeCastExpr(TypeCastExpr tc) {
		Type originalT = tc.expr.accept(this);
		Type castT = tc.castType.accept(this);
		Type newCast;

		if(originalT instanceof PointerType) {
			if(castT instanceof PointerType)
				tc.type = originalT;
			else if (castT instanceof ArrayType)
				tc.type = castT;
		}

		tc.type = castT;
		return tc.type;
	}

	@Override
	public Type visitValueAtExpr(ValueAtExpr va) {
		Type exprT = va.expr.accept(this);
		if(!(exprT instanceof PointerType))
			error("Value at only works on pointer types");
		va.type = ((PointerType) exprT).type.accept(this);
		return va.type;
	}

	@Override
	public Type visitAddressOfExpr(AddressOfExpr ao) {
		Type T = ao.expr.accept(this);
		ao.type = T;
		return new PointerType(T);
	}

	@Override
	public Type visitIntLiteral(IntLiteral i) {
		i.type = BaseType.INT;
		return i.type;
	}

	@Override
	public Type visitChrLiteral(ChrLiteral c) {
		c.type = BaseType.CHAR;
		return c.type;
	}

	@Override
	public Type visitStrLiteral(StrLiteral str) {
		str.type = new ArrayType(BaseType.CHAR, str.str.length() + 1);
		return str.type;
	}

	@Override
	public Type visitAdd(Add a) {
		Type lhdT = a.left.accept(this);
		Type rhdT = a.right.accept(this);
		if(!lhdT.isEqual(BaseType.INT) || !rhdT.isEqual(BaseType.INT))
			error("'+' operator requires two ints");

		a.type = BaseType.INT;
		return a.type;
	}

	@Override
	public Type visitSub(Sub s) {
		Type lhdT = s.left.accept(this);
		Type rhdT = s.right.accept(this);
		if(lhdT != BaseType.INT || rhdT != BaseType.INT)
			error("'-' operator requires two ints");

		s.type = BaseType.INT;
		return s.type;
	}

	@Override
	public Type visitMul(Mul m) {
		Type lhdT = m.left.accept(this);
		Type rhdT = m.right.accept(this);
		if(lhdT != BaseType.INT || rhdT != BaseType.INT)
			error("*' operator requires two ints");

		m.type = BaseType.INT;
		return m.type;
	}

	@Override
	public Type visitDiv(Div d) {
		Type lhdT = d.left.accept(this);
		Type rhdT = d.right.accept(this);
		if(lhdT != BaseType.INT || rhdT != BaseType.INT)
			error("'/' operator requires two ints");

		d.type = BaseType.INT;
		return d.type;
	}

	@Override
	public Type visitMod(Mod m) {
		Type lhdT = m.left.accept(this);
		Type rhdT = m.right.accept(this);
		if(lhdT != BaseType.INT || rhdT != BaseType.INT)
			error("'%' operator requires two ints");

		m.type = BaseType.INT;
		return m.type;
	}

	@Override
	public Type visitGt(Gt g) {
		Type lhdT = g.left.accept(this);
		Type rhdT = g.right.accept(this);
		if(lhdT != BaseType.INT || rhdT != BaseType.INT)
			error(">' operator requires two ints");

		g.type = BaseType.INT;
		return g.type;
	}

	@Override
	public Type visitLt(Lt l) {
		Type lhdT = l.left.accept(this);
		Type rhdT = l.right.accept(this);
		if(lhdT != BaseType.INT || rhdT != BaseType.INT)
			error("'<' operator requires two ints");

		l.type = BaseType.INT;
		return l.type;
	}

	@Override
	public Type visitGe(Ge g) {
		Type lhdT = g.left.accept(this);
		Type rhdT = g.right.accept(this);
		if(lhdT != BaseType.INT || rhdT != BaseType.INT)
			error(">=' operator requires two ints");

		g.type = BaseType.INT;
		return g.type;
	}

	@Override
	public Type visitLe(Le l) {
		Type lhdT = l.left.accept(this);
		Type rhdT = l.right.accept(this);
		if(lhdT != BaseType.INT || rhdT != BaseType.INT)
			error("'<=' operator requires two ints");

		l.type = BaseType.INT;
		return l.type;
	}

	@Override
	public Type visitNe(Ne n) {
		Type lhdT = n.left.accept(this);
		Type rhdT = n.right.accept(this);
		if(lhdT instanceof StructType || lhdT instanceof ArrayType || lhdT.isEqual(BaseType.VOID))
			error("Can't use '!=' with a " + lhdT.getClass().getSimpleName());
		if(!lhdT.isEqual(rhdT))
			error("'!=' operator requires two same types");

		n.type = BaseType.INT;
		return n.type;
	}

	@Override
	public Type visitEq(Eq e) {
		Type lhdT = e.left.accept(this);
		Type rhdT = e.right.accept(this);
		if(lhdT instanceof StructType || lhdT instanceof ArrayType || lhdT.isEqual(BaseType.VOID))
			error("Can't use '==' with a " + lhdT.getClass().getSimpleName());
		if(!lhdT.isEqual(rhdT))
			error("'==' operator requires two same types");

		e.type = BaseType.INT;
		return e.type;
	}

	@Override
	public Type visitOr(Or o) {
		Type lhdT = o.left.accept(this);
		Type rhdT = o.right.accept(this);
		if(!lhdT.isEqual(BaseType.INT) || !rhdT.isEqual(BaseType.INT))
			error("'|' operator requires two ints");

		o.type = BaseType.INT;
		return o.type;
	}

	@Override
	public Type visitAnd(And a) {
		Type lhdT = a.left.accept(this);
		Type rhdT = a.right.accept(this);
		if(!lhdT.isEqual(BaseType.INT) || !rhdT.isEqual(BaseType.INT))
			error("'&' operator requires two ints");

		a.type = BaseType.INT;
		return a.type;
	}
}
