package sem;

import ast.*;

import java.util.ArrayList;
import java.util.List;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {

	private Scope scope;

	public NameAnalysisVisitor() {
		this.scope = new Scope();

		FunDecl print_s = new FunDecl(BaseType.VOID
				, "print_s"
				, List.of(new VarDecl(new PointerType(BaseType.CHAR), "s"))
				, new Block(new ArrayList<>(), new ArrayList<>()));
		FunDecl print_i = new FunDecl(BaseType.VOID
				, "print_i"
				, List.of(new VarDecl(BaseType.INT, "i"))
				, new Block(new ArrayList<>(), new ArrayList<>()));
		FunDecl print_c = new FunDecl(BaseType.VOID
				, "print_c"
				, List.of(new VarDecl(BaseType.CHAR, "c"))
				, new Block(new ArrayList<>(), new ArrayList<>()));
		FunDecl read_c = new FunDecl(BaseType.CHAR
				, "read_c"
				, new ArrayList<>()
				, new Block(new ArrayList<>(), new ArrayList<>()));
		FunDecl read_i = new FunDecl(BaseType.INT
				, "read_i"
				, new ArrayList<>()
				, new Block(new ArrayList<>(), new ArrayList<>()));
		FunDecl mcmalloc = new FunDecl(new PointerType(BaseType.VOID)
				, "mcmalloc"
				, List.of(new VarDecl(BaseType.INT, "size"))
				, new Block(new ArrayList<>(), new ArrayList<>()));

		scope.put(new FunSymbol(print_s));
		scope.put(new FunSymbol(print_c));
		scope.put(new FunSymbol(print_i));
		scope.put(new FunSymbol(read_c));
		scope.put(new FunSymbol(read_i));
		scope.put(new FunSymbol(mcmalloc));
	}

	@Override
	public Void visitBaseType(BaseType bt) {
		return null;
	}

	@Override
	public Void visitStructType(StructType bt) {
		Symbol s = this.scope.lookup(bt.name, true);
		if(s == null || !s.isStruct) {
			error("Struct type " + bt.name + " has never been declared");
			return null;
		}
		bt.structTypeDecl = ((StructSymbol) s).structTypeDecl;
		return null;
	}

	@Override
	public Void visitPointerType(PointerType bt) {
		return null;
	}

	@Override
	public Void visitArrayType(ArrayType bt) {
		return null;
	}

	@Override
	public Void visitStructTypeDecl(StructTypeDecl sts) {
		Symbol s = this.scope.lookupCurrent("struct." + sts.structType.name, true);
		if( s != null) {
			error("Struct " + sts.structType.name + " is already declared.");
			return null;
		}
		this.scope.put(new StructSymbol(sts));
		Scope oldScope = this.scope;
		this.scope = new Scope(oldScope);
		for(VarDecl vd : sts.varDecls)
			vd.accept(this);
		this.scope = oldScope;
		return null;
	}

	@Override
	public Void visitBlock(Block b) {
		Scope oldScope = this.scope;
		this.scope = new Scope(oldScope);
		for(VarDecl vd : b.varDecls)
			vd.accept(this);
		for(Stmt stmt : b.stmts)
			stmt.accept(this);
		this.scope = oldScope;
		return null;
	}

	@Override
	public Void visitWhile(While w) {
		w.expr.accept(this);
		w.stmt.accept(this);
		return null;
	}

	@Override
	public Void visitIf(If i) {
		i.expr.accept(this);
		i.ifStmt.accept(this);
		if( i.elseStmt != null)
			i.elseStmt.accept(this);
		return null;
	}

	@Override
	public Void visitReturn(Return r) {
		if( r.expr != null )
			r.expr.accept(this);
		return null;
	}

	@Override
	public Void visitAssign(Assign a) {
		a.leftExpr.accept(this);
		a.rightExpr.accept(this);
		return null;
	}

	@Override
	public Void visitExprStmt(ExprStmt es) {
		es.expr.accept(this);
		return null;
	}

	@Override
	public Void visitFunDecl(FunDecl p) {
		Symbol s = this.scope.lookupCurrent(p.name);
		if( s != null) {
			error("Function " + p.name + " is already declared.");
			return null;
		}
		this.scope.put(new FunSymbol(p));
		Scope oldScope = this.scope;
		this.scope = new Scope(oldScope);
		for(VarDecl vd : p.params)
			vd.accept(this);
		p.block.accept(this);
		this.scope = oldScope;
		return null;
	}


	@Override
	public Void visitProgram(Program p) {
		for(StructTypeDecl structTypeDecl : p.structTypeDecls)
			structTypeDecl.accept(this);
		for(VarDecl vd : p.varDecls)
			vd.accept(this);
		for(FunDecl fd : p.funDecls) {
			int i = p.funDecls.indexOf(fd);
			fd.accept(this);
		}
		return null;
	}

	@Override
	public Void visitVarDecl(VarDecl vd) {
		Symbol s = this.scope.lookupCurrent(vd.varName);
		if( s != null)
			error("Variable " + vd.varName + " is already declared.");
		else {
			this.scope.put(new VarSymbol(vd));
			vd.type.accept(this);
		}
		return null;
	}

	@Override
	public Void visitVarExpr(VarExpr v) {
		Symbol s = this.scope.lookup(v.name);
		if( s == null || !s.isVar)
			error("Variable " + v.name + " is never declared.");
		else
			v.vd = ((VarSymbol) s).vd;
		return null;
	}

	@Override
	public Void visitSizeOfExpr(SizeOfExpr so) {
		return null;
	}

	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr so) {
		so.array.accept(this);
		so.index.accept(this);
		return null;
	}

	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr fa) {
		fa.expr.accept(this);
		return null;
	}

	@Override
	public Void visitFunCallExpr(FunCallExpr fc) {
		Symbol s = this.scope.lookup(fc.fnName);
		if( s == null || !s.isFun) {
			error("Function " + fc.fnName + " is never declared.");
			return null;
		}
		fc.funDecl = ((FunSymbol) s).fd;
		for(Expr expr : fc.params)
			expr.accept(this);
		return null;
	}

	@Override
	public Void visitTypeCastExpr(TypeCastExpr tc) {
		tc.expr.accept(this);
		return null;
	}

	@Override
	public Void visitValueAtExpr(ValueAtExpr va) {
		va.expr.accept(this);
		return null;
	}

	@Override
	public Void visitAddressOfExpr(AddressOfExpr ao) {
		ao.expr.accept(this);
		return null;
	}

	@Override
	public Void visitIntLiteral(IntLiteral i) {
		return null;
	}

	@Override
	public Void visitChrLiteral(ChrLiteral c) {
		return null;
	}

	@Override
	public Void visitStrLiteral(StrLiteral str) {
		return null;
	}

	@Override
	public Void visitAdd(Add a) {
		a.left.accept(this);
		a.right.accept(this);
		return null;
	}

	@Override
	public Void visitSub(Sub s) {
		s.left.accept(this);
		s.right.accept(this);
		return null;
	}

	@Override
	public Void visitMul(Mul m) {
		m.left.accept(this);
		m.right.accept(this);
		return null;
	}

	@Override
	public Void visitDiv(Div d) {
		d.left.accept(this);
		d.right.accept(this);
		return null;
	}

	@Override
	public Void visitMod(Mod m) {
		m.left.accept(this);
		m.right.accept(this);
		return null;
	}

	@Override
	public Void visitGt(Gt g) {
		g.left.accept(this);
		g.right.accept(this);
		return null;
	}

	@Override
	public Void visitLt(Lt l) {
		l.left.accept(this);
		l.right.accept(this);
		return null;
	}

	@Override
	public Void visitGe(Ge g) {
		g.left.accept(this);
		g.right.accept(this);
		return null;
	}

	@Override
	public Void visitLe(Le l) {
		l.left.accept(this);
		l.right.accept(this);
		return null;
	}

	@Override
	public Void visitNe(Ne n) {
		n.left.accept(this);
		n.right.accept(this);
		return null;
	}

	@Override
	public Void visitEq(Eq e) {
		e.left.accept(this);
		e.right.accept(this);
		return null;
	}

	@Override
	public Void visitOr(Or o) {
		o.left.accept(this);
		o.right.accept(this);
		return null;
	}

	@Override
	public Void visitAnd(And a) {
		a.left.accept(this);
		a.right.accept(this);
		return null;
	}

}
