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
import br.ufpe.cin.if688.minijava.symboltable.SymbolTable;

public class TypeCheckVisitor implements IVisitor<Type> {

	private SymbolTable symbolTable;

	TypeCheckVisitor(SymbolTable st) {
		symbolTable = st;
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
		n.i1.accept(this);
		n.i2.accept(this);
		n.s.accept(this);
		return null;
	}

	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclSimple n) {
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
		n.t.accept(this);
		n.i.accept(this);
		return null;
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public Type visit(MethodDecl n) {
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
		
		if (!symbolTable.compareTypes(t, e)) {
			System.err.println("Tipo não coincide com o retorno (MethodDecl)");
		}
		
		return null;
	}

	// Type t;
	// Identifier i;
	public Type visit(Formal n) {
		n.t.accept(this);
		n.i.accept(this);
		return null;
	}

	public Type visit(IntArrayType n) {
		return null;
	}

	public Type visit(BooleanType n) {
		return null;
	}

	public Type visit(IntegerType n) {
		return null;
	}

	// String s;
	public Type visit(IdentifierType n) {
		return null;
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
		
		if(e == null) {
			return null;
		}
		
		if(!(e instanceof BooleanType)) {
			System.err.println("A expressão não é boolean (IF)");
		}
		
		n.s1.accept(this);
		n.s2.accept(this);
		return null;
	}

	// Exp e;
	// Statement s;
	public Type visit(While n) {
		Type e = n.e.accept(this);
		
		if(e == null) {
			return null;
		}
		
		if(!(e instanceof BooleanType)) {
			System.err.println("A expressão não é boolean (WHILE)");
		}
		
		n.s.accept(this);
		return null;
	}

	// Exp e;
	public Type visit(Print n) {
		Type e = n.e.accept(this);
		
		if(e == null) {
			return null;
		}
		
		if(!(e instanceof Type)) {
			System.err.println("A expressão não é um type (PRINT)");
		}
		return null;
	}

	// Identifier i;
	// Exp e;
	public Type visit(Assign n) {
		Type i = n.i.accept(this);
		Type e = n.e.accept(this);
		
		if (i == null || e == null) {
			return null;
		}
		
		if (!symbolTable.compareTypes(i, e)) {
			System.err.println("Os tipos de identifier (" + n.i.toString() + ") " + "e expession (" + n.e.toString() + ") são diferentes");
		}
		
		return null;
	}

	// Identifier i;
	// Exp e1,e2;
	public Type visit(ArrayAssign n) {
		Type i = n.i.accept(this);
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if (i == null || e1 == null || e2 == null) {
			return null;
		}
		
		if(!(e1 instanceof IntegerType)) {
			System.err.println(n.e1.toString() + " não é do tipo integer (ARRAY ASSIGN)");
		} 
		return null;
	}

	// Exp e1,e2;
	public Type visit(And n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if (e1 == null || e2 == null) {
			return null;
		}
		
		if (!(e1 instanceof BooleanType)) {
			System.err.println(e1 + " nao eh do tipo Boolean (AND)");
		}
		if (!(e2 instanceof BooleanType)) {
			System.err.println(e2 + " nao eh do tipo Boolean (AND)");
		}
		
		return new BooleanType();
	}

	// Exp e1,e2;
	public Type visit(LessThan n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if (e1 == null || e2 == null) {
			return null;
		}
		
		if (!(e1 instanceof IntegerType)) {
			System.err.println(e1 + " nao eh do tipo Integer (LessThan)");
		}
		if (!(e2 instanceof IntegerType)) {
			System.err.println(e2 + " nao eh do tipo Integer (LessThan)");
		}
		
		return new BooleanType();
	}

	// Exp e1,e2;
	public Type visit(Plus n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if (e1 == null || e2 == null) {
			return null;
		}
		
		if (!(e1 instanceof IntegerType)) {
			System.err.println(e1 + " nao eh do tipo Integer (Plus)");
		}
		if (!(e2 instanceof IntegerType)) {
			System.err.println(e2 + " nao eh do tipo Integer (Plus)");
		}
		
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Minus n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if (e1 == null || e2 == null) {
			return null;
		}
		
		if (!(e1 instanceof IntegerType)) {
			System.err.println(e1 + " nao eh do tipo Integer (Minus)");
		}
		if (!(e2 instanceof IntegerType)) {
			System.err.println(e2 + " nao eh do tipo Integer (Minus)");
		}
		
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Times n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if (e1 == null || e2 == null) {
			return null;
		}
		
		if (!(e1 instanceof IntegerType)) {
			System.err.println(e1 + " nao eh do tipo Integer (Times)");
		}
		if (!(e2 instanceof IntegerType)) {
			System.err.println(e2 + " nao eh do tipo Integer (Times)");
		}
		
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(ArrayLookup n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if (e1 == null || e2 == null) {
			return null;
		}
		
		if (!(e1 instanceof IntArrayType)) {
			System.err.println(e1 + " nao eh do tipo Int Array (ArrayLookup)");
		}
		if (!(e2 instanceof IntegerType)) {
			System.err.println(e2 + " nao eh do tipo Integer (ArrayLookup)");
		}
		
		return new IntegerType();
	}

	// Exp e;
	public Type visit(ArrayLength n) {
		Type e = n.e.accept(this);
		
		if (e == null) {
			return null;
		}
		
		if(!(e instanceof IntArrayType)) {
			System.err.println(e + " não é do tipo IntArray (ArrayLength)");
		}
		
		return null;
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public Type visit(Call n) {
		n.e.accept(this);
		n.i.accept(this);
		for (int i = 0; i < n.el.size(); i++) {
			n.el.elementAt(i).accept(this);
		}
		return null;
	}

	// int i;
	public Type visit(IntegerLiteral n) {
		return null;
	}

	public Type visit(True n) {
		return null;
	}

	public Type visit(False n) {
		return null;
	}

	// String s;
	public Type visit(IdentifierExp n) {
		return null;
	}

	public Type visit(This n) {
		return null;
	}

	// Exp e;
	public Type visit(NewArray n) {
		n.e.accept(this);
		return null;
	}

	// Identifier i;
	public Type visit(NewObject n) {
		return null;
	}

	// Exp e;
	public Type visit(Not n) {
		n.e.accept(this);
		return null;
	}

	// String s;
	public Type visit(Identifier n) {
		return null;
	}
}
