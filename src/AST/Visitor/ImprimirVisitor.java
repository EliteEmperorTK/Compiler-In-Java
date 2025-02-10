/**
 * Visitor that prints the source code representation of the AST.
 */
package AST.Visitor;

import AST.*;

public class ImprimirVisitor implements Visitor {
    private int tabs = 0;

    /**
     * Incrementa el nivel de indentación.
     */
    public void incTab() {
        tabs++;
    }

    /**
     * Decrementa el nivel de indentación.
     */
    public void decTab() {
        tabs--;
        if (tabs < 0) tabs = 0;
    }

    /**
     * Imprime espacios según el nivel de indentación actual.
     */
    public void printTab() {
        for (int i = 0; i < tabs * 4; i++) {
            System.out.print(" ");
        }
    }

    // Métodos Visit

    /**
     * Visits the program node and prints its structure.
     * @param n The program node.
     */
    @Override
    public void visitProgram(Program n) {
        System.out.println("Program:");
        incTab();
        n.main.accept(this);
        decTab();
    }

    /**
     * Visits the main class node and prints its statements.
     * @param n The main class node.
     */
    @Override
    public void visitMainClass(MainClass n) {
        printTab();
        System.out.println("main() {");
        incTab();

		for(Statement stat : n.stateList.listaStatements){
			stat.accept(this);
		}

        decTab();
        printTab();
        System.out.println("}");
    }

    /**
     * Visits a variable declaration node and prints it.
     * @param n The variable declaration node.
     */
    @Override
    public void visitVarDecl(VarDecl n) {
        printTab();
        n.ty.accept(this);
        System.out.print(" " + n.ident.toString());
        if (n.expr != null) {
            System.out.print(" = ");
            String temp = n.expr.accept(this);
            System.out.print(temp);
        }
        System.out.println(";");
    }

    /**
     * Visits an if-else statement node and prints its structure.
     * @param n The if statement node.
     */
    @Override
    public void visitIf(If n) {
        printTab();
        System.out.print("if (");
        String temp = n.cond.accept(this);
        System.out.print(temp);

        System.out.println(") {");
        incTab();

		for(Statement stat : n.stateList1.listaStatements){
			stat.accept(this);
		}
		
        decTab();
        if (n.stateList2 != null) {
            printTab();
            System.out.println("} else {");
            incTab();

			for(Statement stat : n.stateList2.listaStatements){
				stat.accept(this);
			}
            decTab();
        }
        printTab();
        System.out.println("}");
    }

    /**
     * Visits an identifier expression node.
     * @param n The identifier expression node.
     * @return The identifier as a string.
     */
	public String visitIdentifierExp(IdentifierExp n) {
		return n.nombre;
	}

    /**
     * Visits a while statement node and prints its structure.
     * @param n The while statement node.
     */
    @Override
    public void visitWhile(While n) {
        printTab();
        System.out.print("while (");

        String temp = n.cond.accept(this);
        System.out.print(temp);

        System.out.println(") {");
        incTab();

		for(Statement stat : n.stateList.listaStatements){
			stat.accept(this);
		}

        decTab();
        printTab();
        System.out.println("}");
    }

    /**
     * Visits an assignment statement node and prints it.
     * @param n The assignment node.
     */
    @Override
    public void visitAssign(Assign n) {
        printTab();
        System.out.print(n.ident.toString() + " = ");
        String temp = n.expr.accept(this);
        System.out.print(temp);
        System.out.println(";");
    }

    /**
     * Visits a function definition node and prints it.
     * @param n The function definition node.
     */
    @Override
    public void visitFunctionDef(FunctionDef n) {
        printTab();
        System.out.print("def " + n.ty.toString() + " " + n.ident.toString() + "(");

        if(n.paramList != null){
            for (int i = 0; i < n.paramList.size(); i++) {
            n.paramList.get(i).accept(this);
            if (i < n.paramList.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println(") {");
        incTab();
        }
        

        for(Statement stat : n.stateList.listaStatements){
			stat.accept(this);
		}
		
        if (n.expr != null) {
            printTab();
            System.out.print("return ");
            String temp = n.expr.accept(this);
            System.out.print(temp);
            System.out.println(";");
        }
        decTab();
        printTab();
        System.out.println("}");
    }

    /**
     * Visits a function parameter node.
     * @param n The parameter node.
     */
    @Override
    public void visitParam(Param n) {
        n.ty.accept(this);
        System.out.print(" " + n.ident.toString());
    }

    /**
     * Visits a for-loop node and prints it.
     * @param n The for-loop node.
     */
    @Override
    public void visitFor(For n) {
        printTab();
        System.out.print("for (");

        n.assState.accept(this);

        System.out.print("; ");

        String temp = n.cond.accept(this);
        System.out.print(temp);

        System.out.print("; ");

        temp = n.expr.accept(this);
        System.out.print(temp);
        System.out.println(") {");
        incTab();
		for(Statement stat : n.stateList.listaStatements){
			stat.accept(this);
		}
        decTab();
        printTab();
        System.out.println("}");
    }

    /**
     * Visits a read statement node.
     * @param n The read statement node.
     */
    @Override
    public void visitRead(Read n) {
        printTab();
        System.out.println("read(" + n.ident.toString() + ");");
    }

    /**
     * Visits a write statement node.
     * @param n The write statement node.
     */
    @Override
    public void visitWrite(Write n) {
        printTab();
        System.out.print("write(");
        String temp = n.expr.accept(this);
        System.out.print(temp);
        System.out.println(");");
    }

    /**
     * Visits a constant declaration node.
     * @param n The constant declaration node.
     */
    @Override
    public void visitConst(Const n) {
        printTab();
        System.out.print("const " + n.ty.toString()+ " " + n.ident.toString() + " = ");
        String temp = n.expr.accept(this);
        System.out.print(temp);
        System.out.println(";");
    }

    /**
     * Visits a tuple creation node.
     * @param n The tuple creation node.
     */
    @Override
    public void visitCrearTupla(CrearTupla n) {
        printTab();
        System.out.print("tupla " + n.ident.toString() + " = (");

        String temp = n.expr1.accept(this);
        System.out.print(temp);

        System.out.print(", ");
        
        temp = n.expr2.accept(this);
        System.out.print(temp);
        
        System.out.println(");");
    }

    /**
     * Visits a boolean type node.
     * @param n The boolean type node.
     */
    @Override
    public void visitBooleanType(BooleanType n) {
        System.out.print("boolean");
    }

    /**
     * Visits an integer type node.
     * @param n The integer type node.
     */
    @Override
    public void visitIntegerType(IntegerType n) {
        System.out.print("int");
    }

    /**
     * Visits a tuple type node.
     * @param n The tuple type node.
     */
    @Override
    public void visitTupleType(TupleType n) {
        System.out.print("tuple");
    }

    // Métodos Visit con String como retorno

    @Override
    public String visitIntLiteral(IntLiteral n) {
        return Integer.toString(n.valor);
    }

    @Override
    public String visitAdd(Add n) {
        return n.expr1.accept(this) + " + " + n.expr2.accept(this);
    }

    @Override
    public String visitSub(Sub n) {
        return n.expr1.accept(this) + " - " + n.expr2.accept(this) ;
    }

    @Override
    public String visitMul(Mul n) {
        return n.expr1.accept(this) + " * " + n.expr2.accept(this);
    }

    @Override
    public String visitAnd(And n) {
        return n.expr1.accept(this) + " && " + n.expr2.accept(this);
    }

    @Override
    public String visitOr(Or n) {
        return n.expr1.accept(this) + " || " + n.expr2.accept(this);
    }

    @Override
    public String visitNot(Not n) {
        return "!" + n.expr.accept(this);
    }

    @Override
    public String visitTrue(True n) {
        return "true";
    }

    @Override
    public String visitFalse(False n) {
        return "false";
    }

    @Override
    public String visitIdentifier(Identifier n) {
        return n.nombre.toString();
    }

    @Override
    public String visitEqual(Equal n) {
        return n.expr1.accept(this) + " == " + n.expr2.accept(this);
    }

    @Override
    public String visitNotEqual(NotEqual n) {
        return n.expr1.accept(this) + " != " + n.expr2.accept(this);
    }

    @Override
    public String visitGreaterThan(GreaterThan n) {
        return n.expr1.accept(this) + " > " + n.expr2.accept(this);
    }

    @Override
    public String visitLessThan(LessThan n) {
        return n.expr1.accept(this) + " < " + n.expr2.accept(this);
    }

    @Override
    public String visitGetTupla(GetTupla n) {
        return n.ident.toString() + "(" + n.expr.accept(this) + ")";
    }

    @Override
    public String visitLlamadaFuncion(LlamadaFuncion n) {
        StringBuilder sb = new StringBuilder();
        sb.append(n.ident.toString()).append("(");
        for (int i = 0; i < n.exprList.size(); i++) {
            sb.append(n.exprList.get(i).accept(this));
            if (i < n.exprList.size() - 1) sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    }
}
