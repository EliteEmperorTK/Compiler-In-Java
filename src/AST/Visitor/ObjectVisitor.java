package AST.Visitor;

import AST.*;

public interface ObjectVisitor {
        public Object visitProgram(Program n);
        public Object visitMainClass(MainClass n);
        public Object visitVarDecl(VarDecl n);
        public Object visitBooleanType(BooleanType n);
        public Object visitIntegerType(IntegerType n);
        public Object visitTupleType(TupleType n);
        public Object visitIf(If n);
        public Object visitWhile(While n);
        public Object visitAssign(Assign n);
        public Object visitAnd(And n);
        public Object visitLessThan(LessThan n);
        public Object visitTrue(True n);
        public Object visitFalse(False n);
        public Object visitNot(Not n);
        public Object visitIdentifier(Identifier n);
        public Object visitFunctionDef(FunctionDef n);
        public Object visitAdd(Add n); 
        public Object visitSub(Sub n); 
        public Object visitMul(Mul n);
        public Object visitParam(Param n);
        public Object visitEqual(Equal n); 
        public Object visitNotEqual(NotEqual n); 
        public Object visitGreaterThan(GreaterThan n); 
        public Object visitOr(Or n);
        public Object visitFor(For n); 
        public Object visitRead(Read n); 
        public Object visitWrite(Write n);
        public Object visitConst(Const n); 
        public Object visitIntLiteral(IntLiteral n); 
        public Object visitLlamadaFuncion(LlamadaFuncion n);  
        public Object visitCrearTupla(CrearTupla n);
        public Object visitGetTupla(GetTupla n);
        public Object visitIdentifierExp(IdentifierExp n);
}