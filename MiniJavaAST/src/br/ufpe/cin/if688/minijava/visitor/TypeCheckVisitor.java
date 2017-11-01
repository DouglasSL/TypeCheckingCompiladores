package br.ufpe.cin.if688.minijava.visitor;

import br.ufpe.cin.if688.minijava.ast.And;
import br.ufpe.cin.if688.minijava.ast.ArrayAssign;
import br.ufpe.cin.if688.minijava.ast.ArrayLength;
import br.ufpe.cin.if688.minijava.ast.ArrayLookup;
import br.ufpe.cin.if688.minijava.ast.Assign;
import br.ufpe.cin.if688.minijava.ast.Block;
import br.ufpe.cin.if688.minijava.ast.BooleanType;
import br.ufpe.cin.if688.minijava.ast.Call;
import br.ufpe.cin.if688.minijava.ast.ClassDeclExtends;
import br.ufpe.cin.if688.minijava.ast.ClassDeclSimple;
import br.ufpe.cin.if688.minijava.ast.False;
import br.ufpe.cin.if688.minijava.ast.Formal;
import br.ufpe.cin.if688.minijava.ast.Identifier;
import br.ufpe.cin.if688.minijava.ast.IdentifierExp;
import br.ufpe.cin.if688.minijava.ast.IdentifierType;
import br.ufpe.cin.if688.minijava.ast.If;
import br.ufpe.cin.if688.minijava.ast.IntArrayType;
import br.ufpe.cin.if688.minijava.ast.IntegerLiteral;
import br.ufpe.cin.if688.minijava.ast.IntegerType;
import br.ufpe.cin.if688.minijava.ast.LessThan;
import br.ufpe.cin.if688.minijava.ast.MainClass;
import br.ufpe.cin.if688.minijava.ast.MethodDecl;
import br.ufpe.cin.if688.minijava.ast.Minus;
import br.ufpe.cin.if688.minijava.ast.NewArray;
import br.ufpe.cin.if688.minijava.ast.NewObject;
import br.ufpe.cin.if688.minijava.ast.Not;
import br.ufpe.cin.if688.minijava.ast.Plus;
import br.ufpe.cin.if688.minijava.ast.Print;
import br.ufpe.cin.if688.minijava.ast.Program;
import br.ufpe.cin.if688.minijava.ast.This;
import br.ufpe.cin.if688.minijava.ast.Times;
import br.ufpe.cin.if688.minijava.ast.True;
import br.ufpe.cin.if688.minijava.ast.Type;
import br.ufpe.cin.if688.minijava.ast.VarDecl;
import br.ufpe.cin.if688.minijava.ast.While;
import br.ufpe.cin.if688.minijava.symboltable.Method;
import br.ufpe.cin.if688.minijava.symboltable.Class;
import br.ufpe.cin.if688.minijava.symboltable.SymbolTable;

public class TypeCheckVisitor implements IVisitor<Type> {

	private SymbolTable symbolTable;
	private Class currClass;
	private Method currMethod;
	private boolean flag;

	TypeCheckVisitor(SymbolTable st) {
		symbolTable = st;
		currClass = null;
		currMethod = null;
		flag = false;
	}

	// MainClass m;
	// ClassDeclList cl;
	public Type visit(Program n) {
		n.m.accept(this);
		for (int i = 0; i < n.cl.size(); i++) {
			n.cl.elementAt(i).accept(this);
		}
		return null;
	}

	// Identifier i1,i2;
	// Statement s;
	public Type visit(MainClass n) {
		currClass = symbolTable.getClass(n.i1.toString());
		n.i1.accept(this);
		n.i2.accept(this);
		n.s.accept(this);
		return null;
	}

	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclSimple n) {
		currClass = symbolTable.getClass(n.i.toString());
		n.i.accept(this);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.elementAt(i).accept(this);
		}
		return null;
	}

	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclExtends n) {
		currClass = symbolTable.getClass(n.i.toString());
		n.i.accept(this);
		n.j.accept(this);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.elementAt(i).accept(this);
		}
		return null;
	}

	// Type t;
	// Identifier i;
	public Type visit(VarDecl n) {
		flag = true;
		Type t = n.t.accept(this);
		n.i.accept(this);
		flag = false;
		return t;
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public Type visit(MethodDecl n) {
		currMethod = symbolTable.getMethod(n.i.toString(), currClass.getId());
		Type t = n.t.accept(this);
		n.i.accept(this);
		for (int i = 0; i < n.fl.size(); i++) {
			n.fl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}
		Type e = n.e.accept(this);
		
		if(!(symbolTable.compareTypes(t, e))) {
			System.err.println("Tipo da Express�o � diferente do tipo do m�todo (MethodDecl)");
		}
		
		currMethod = null;
		
		return t;
	}

	// Type t;
	// Identifier i;
	public Type visit(Formal n) {
		flag = true;
		Type t = n.t.accept(this);
		n.i.accept(this);
		flag = false;
		return t;
	}

	public Type visit(IntArrayType n) {
		return n;
	}

	public Type visit(BooleanType n) {
		return n;
	}

	public Type visit(IntegerType n) {
		return n;
	}

	// String s;
	public Type visit(IdentifierType n) {
		if(!symbolTable.containsClass(n.s)) {
			System.err.println("Tipo da classe n�o encontrada (IdentifierType)");
		}
		return n;
	}

	// StatementList sl;
	public Type visit(Block n) {
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}
		return null;
	}

	// Exp e;
	// Statement s1,s2;
	public Type visit(If n) {
		Type e = n.e.accept(this);
		
		if(!(e instanceof BooleanType)) {
			System.err.println("A express�o n�o � do tipo Boolean (If)");
		}
		
		n.s1.accept(this);
		n.s2.accept(this);
		
		return null;
	}

	// Exp e;
	// Statement s;
	public Type visit(While n) {
		Type e = n.e.accept(this);
		
		if(!(e instanceof BooleanType)) {
			System.err.println("A express�o n�o � do tipo Boolean (While)");
		}
		
		n.s.accept(this);
		
		return null;
	}

	// Exp e;
	public Type visit(Print n) {
		n.e.accept(this);
		return null;
	}

	// Identifier i;
	// Exp e;
	public Type visit(Assign n) {
		flag = true;
		Type i = symbolTable.getVarType(currMethod, currClass, n.i.toString());
		flag = false;
		n.i.accept(this);
		Type e = n.e.accept(this);
		
		if(!symbolTable.compareTypes(i, e)) {
			System.err.println("Os tipos de identifier e express�o s�o diferentes");
		}
		
		return null;
	}

	// Identifier i;
	// Exp e1,e2;
	public Type visit(ArrayAssign n) {
		Type i = n.i.accept(this);
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if(!(i instanceof IntArrayType)) {
			System.err.println("O identifier n�o � do tipo IntArray (ArrayAssign)");
		}
		if(!(e1 instanceof IntegerType)) {
			System.err.println("A express�o 1 (Index) n�o � do tipo Int (ArrayAssign)");
		}
		if(!(e2 instanceof IntegerType)) {
			System.err.println("A express�o 2 n�o � do tipo Int (ArrayAssign)");
		}
		
		return null;
	}

	// Exp e1,e2;
	public Type visit(And n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if(!(e1 instanceof IntegerType)) {
			System.err.println("A express�o 1 n�o � do tipo Int (Times)");
		}
		
		if (!(e2 instanceof IntegerType)) {
			System.err.println("A express�o 2 n�o � do tipo Int (Times)");
		}
		
		return new BooleanType();
	}

	// Exp e1,e2;
	public Type visit(LessThan n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if(!(e1 instanceof IntegerType)) {
			System.err.println("A express�o 1 n�o � do tipo Int (Times)");
		}
		
		if (!(e2 instanceof IntegerType)) {
			System.err.println("A express�o 2 n�o � do tipo Int (Times)");
		}
		
		return new BooleanType();
	}

	// Exp e1,e2;
	public Type visit(Plus n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if(!(e1 instanceof IntegerType)) {
			System.err.println("A express�o 1 n�o � do tipo Int (Plus)");
		}
		
		if (!(e2 instanceof IntegerType)) {
			System.err.println("A express�o 2 n�o � do tipo Int (Plus)");
		}
		
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Minus n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if(!(e1 instanceof IntegerType)) {
			System.err.println("A express�o 1 n�o � do tipo Int (Minus)");
		}
		
		if (!(e2 instanceof IntegerType)) {
			System.err.println("A express�o 2 n�o � do tipo Int (Minus)");
		}
		
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Times n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if(!(e1 instanceof IntegerType)) {
			System.err.println("A express�o 1 n�o � do tipo Int (Times)");
		}
		
		if (!(e2 instanceof IntegerType)) {
			System.err.println("A express�o 2 n�o � do tipo Int (Times)");
		}
		
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(ArrayLookup n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if (!(e1 instanceof IntArrayType)) {
			System.err.println("A express�o 1 n�o � um IntArray (ArrayLookup)");
		}
		if (!(e2 instanceof IntegerType)) {
			System.err.println("A express�o 2 n�o � um Int (ArrayLookup)");
		}
		
		return new IntegerType();
	}

	// Exp e;
	public Type visit(ArrayLength n) {
		Type e = n.e.accept(this);
		
		if(!(e instanceof IntArrayType)) {
			System.err.println("A express�o n�o do tipo IntArray (ArrayLength)");
		}
		
		return new IntegerType();
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public Type visit(Call n) {
		
		Type ct = n.e.accept(this);
		Type t = null;
		
		if(ct instanceof IdentifierType) {
			
			Class c = symbolTable.getClass(((IdentifierType) ct).s);
			Method m = symbolTable.getMethod(n.i.toString(), c.getId());
			
			if(m == null) {
				System.out.println("M�todo n�o encontrado (Call)");
				
			} else {

				t = n.i.accept(this);
				
				if (n.el.size() == countParams(m)) {
					for (int i = 0; i < n.el.size(); i++) {
						Type el = n.el.elementAt(i).accept(this);
						if(!this.symbolTable.compareTypes(el, m.getParamAt(i).type())) {
							System.err.println("Tipos n�o compat�veis nos par�metros do m�todo (Call)");
						}
					}
				}else {
					System.err.println("Quantidade dos par�metros � diferente da explist (Call)");
				}
			}
			
		} else {
			System.out.println("Classe n�o encontrada (Call)");
		}
		
		return t;
	}

	// int i;
	public Type visit(IntegerLiteral n) {
		return new IntegerType();
	}

	public Type visit(True n) {
		return new BooleanType();
	}

	public Type visit(False n) {
		return new BooleanType();
	}

	// String s;
	public Type visit(IdentifierExp n) {
		Type i = symbolTable.getVarType(currMethod, currClass, n.s);
		if(i == null) {
			System.err.println("identifier n�o encontrado (IdentifierExp)");
		}
		
		return i;
	}

	public Type visit(This n) {
		return currClass.type();
	}

	// Exp e;
	public Type visit(NewArray n) {
		Type e = n.e.accept(this);
		
		if(!(e instanceof IntegerType)) {
			System.err.println("A express�o n�o � do tipo Int (NewArray)");
		}
		
		return new IntArrayType();
	}

	// Identifier i;
	public Type visit(NewObject n) {
		n.i.accept(this);
		return symbolTable.getClass(n.toString()).type();
	}

	// Exp e;
	public Type visit(Not n) {
		Type e = n.e.accept(this);
		
		if(!(e instanceof BooleanType)) {
			System.err.println("A express�o n�o � do tipo Boolean (Not)");
		}
		
		return new BooleanType();
	}

	// String s;
	public Type visit(Identifier n) {
		if (!flag) {
			if (!symbolTable.containsClass(n.s)) {
				System.err.println("Classe n�o encontrada (Identifier)");
			} else {
				return symbolTable.getClass(n.s).type();
			}
			
		} else {
			Type i = symbolTable.getVarType(currMethod, currClass, n.s);
			if (i == null) {
				System.err.println("Vari�vel n�o encontrada (Identifier)");
			}
			
			return i;
		}
		
		return new IdentifierType(n.s);
	}
	
	public int countParams(Method m) {
		int i = 0;
		int count = 1;
		while (true) {
			if(m.getParamAt(i) == null) {
				break;
			}
			count ++;
		}
		return count;
	}
	
}