package AST.Visitor;

import AST.*;

public interface Visitor {
        public void visitProgram(Program n);
        public void visitMainClass(MainClass n);
        public void visitVarDecl(VarDecl n);
        public void visitIf(If n);
        public void visitWhile(While n);
        public void visitAssign(Assign n);               
        public void visitFunctionDef(FunctionDef n);  
        public void visitParam(Param n);
        public void visitFor(For n); 
        public void visitRead(Read n); 
        public void visitWrite(Write n); 
        public void visitConst(Const n);           
        public void visitCrearTupla(CrearTupla n);
        public void visitBooleanType(BooleanType n);
        public void visitIntegerType(IntegerType n);
        public void visitTupleType(TupleType n);
        
        
        //Nodos que devuelven un String con la variable temporal asignada a su
        //resultado. Ej: t6 o t32 
        public String visitIntLiteral(IntLiteral n); 
        public String visitAdd(Add n);
        public String visitSub(Sub n);
        public String visitMul(Mul n);
        public String visitAnd(And n);
        public String visitOr(Or n);
        public String visitNot(Not n); 
        public String visitTrue(True n);
        public String visitFalse(False n);
        public String visitIdentifier(Identifier n);        
        public String visitEqual(Equal n); 
        public String visitNotEqual(NotEqual n); 
        public String visitGreaterThan(GreaterThan n);
        public String visitLessThan(LessThan n);
        public String visitGetTupla(GetTupla n); 
        public String visitLlamadaFuncion(LlamadaFuncion n);
        public String visitIdentifierExp(IdentifierExp n);
}
