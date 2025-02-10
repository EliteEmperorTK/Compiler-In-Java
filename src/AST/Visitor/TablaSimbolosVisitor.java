/*
    ESTOS visit SE ASEGURAN DE QUE LAS VARIABLES ESTÉN DECLARADAS CORRECTAMENTE Y EN EL aMBITO QUE TOCA, INCLUYENDO LA GESTIÓN DE SI SE PUEDE USAR LA VARIABLE EN ESE aMBITO
    O SI ESTa DECLARADA
 */
package AST.Visitor;

import AST.*;
import programa.GestorErrores;
import tablaSimbolos.*;

public class TablaSimbolosVisitor implements Visitor {

    private GestorErrores gestorErrores;
    TablaSimbolos ts = new TablaSimbolos();

    public TablaSimbolosVisitor(GestorErrores gestorErr) { 
        this.gestorErrores = gestorErr;
    }
    
    public TablaSimbolos getSymtab() {
        return ts;
    }

    public void print() {
        ts.print(0);
    }

    public void imprimirTablaSimbolos(String nombreArchivo) {
        ts.imprimirTablaSimbolos(nombreArchivo, 0);
    }

    public String getTipoString(Type t) {
        if ( t == null )
			return "";
		else if ( t instanceof TupleType ) {
			return t.toString();
		}
		else if ( t instanceof BooleanType ) {
			return "boolean";
		}
		else if ( t instanceof IntegerType ) {
			return "int";
		}

		return "";
	}

    public void report_error(int linea, String mensaje) {
        gestorErrores.agregarError(linea, mensaje);
    }

	@Override
    public void visitBooleanType(BooleanType n) {
        n.accept(this);
    }

	@Override
    public void visitIntegerType(IntegerType n) {
        n.accept(this);
    }

	@Override
    public void visitTupleType(TupleType n){
        n.accept(this);
    }

    // MainClass main;
    @Override
    public void visitProgram(Program n) {
        // Ingresar al ambito del programa principal
        if (n.main != null) n.main.accept(this);
    }

    // StatementList stateList;
    @Override
    public void visitMainClass(MainClass n) {
        // Registrar la clase main en la tabla de símbolos
        ts = ts.enterScope("main", n);

        // Visitar todas las declaraciones dentro del bloque main
        for (int i = 0; i < n.stateList.size(); i++) {
            Statement stat = n.stateList.get(i);
            if (stat != null) stat.accept(this);
        }

        // Salir del ambito main
        ts = ts.exitScope();
    }

    // busca si una varible esta declarada en el ambito actual, si no esta la declara
    // osea que se pueden declarar variables con el mismo nombre en ambitos diferentes
    // en un for entonces se puede declarar una variable con el mismo nombre que en el main
    @Override
    public void visitVarDecl(VarDecl n) {
        // Verificar si la variable ya esta declarada en el ambito actual
        Simbolo s = ts.lookupSymbol(n.ident.nombre);
        if( s!= null && s instanceof SimboloVar){
            report_error(n.getLineNo(), "Variable \"" + n.ident.nombre + "\" ya esta declarada en este ambito.");
            return;
        }
        // Registrar la variable en la tabla de símbolos
        SimboloVar var = new SimboloVar(n.ident.nombre, getTipoString(n.ty));
        ts.addSimbolo(var);
    }

    @Override
    public void visitConst(Const n) {
        // Verificar si la constante ya esta declarada
        if (ts.lookupSymbol(n.ident.nombre, "SimboloVar") != null) {
            report_error(n.getLineNo(), "Constante \"" + n.ident.nombre + "\" ya esta declarada.");
            return;
        }

        // Registrar la constante en la tabla de símbolos
        SimboloVar constante = new SimboloVar(n.ident.nombre, getTipoString(n.ty),true);
        ts.addSimbolo(constante);
    }

    @Override
    public void visitFunctionDef(FunctionDef n) {
        // Registrar la función
        if (ts.lookupSymbol(n.ident.nombre, "SimboloFuncion") != null) {
            report_error(n.getLineNo(), "Funcion \"" + n.ident.nombre + "\" ya esta declarada.");
            return;
        }

        SimboloFuncion funcion = new SimboloFuncion(n.ident.nombre, getTipoString(n.ty));
        if (n.paramList != null) {
            for (Param p : n.paramList.listaParametros) {
                funcion.addParametro(new SimboloVar(p.ident.nombre, getTipoString(p.ty)));
            }
        }
        ts.addSimbolo(funcion);

        // Entrar al ambito de la función
        ts = ts.enterScope(n.ident.nombre, n);
        ts.setFunctionScope(true); // Marcar que este es un ambito de función

        // Registrar los parametros como variables locales
        if (n.paramList != null) {
            for (Param p : n.paramList.listaParametros) {
                if (p != null) p.accept(this);
            }
        }

        // Procesar el cuerpo de la función
        if (n.stateList != null) {
            for (Statement stat : n.stateList.listaStatements) {
                if (stat != null) stat.accept(this);
            }
        }

        ts.setFunctionScope(false); // Salir del ambito de función
        ts = ts.exitScope();
    }

    @Override
    public void visitParam(Param n) {
        // Registrar el parametro como variable local en la tabla de símbolos
        SimboloVar parametro = new SimboloVar(n.ident.nombre, getTipoString(n.ty));
        ts.addSimbolo(parametro);
    }

    @Override
    public void visitFor(For n) {
        // Crear un nuevo ambito para el bucle
        ts = ts.enterScope("for", n);

        // Procesar la declaración inicial
        if (n.assState != null) {
            n.assState.accept(this);
        }

        // Verificar la condición
        if (n.cond != null) {
            n.cond.accept(this);
        }

        // Procesar el identificador y la expresión del paso
        if (n.ident != null && n.expr != null) {
            Simbolo simbolo = ts.lookupSymbol(n.ident.nombre);
            if (simbolo == null) {
                report_error(n.getLineNo(), "Variable de control \"" + n.ident.nombre + "\" no esta declarada.");
            } else if (!simbolo.getTipo().equals("int")) {
                report_error(n.getLineNo(), "La variable de control \"" + n.ident.nombre + "\" debe ser de tipo int.");
            }

            n.expr.accept(this);
        }

        // Procesar el cuerpo del bucle
        if (n.stateList != null) {
            for (Statement stat : n.stateList.listaStatements) {
                if (stat != null) stat.accept(this);
            }
        }

        // Salir del ambito del bucle
        ts = ts.exitScope();
    }

    @Override
    public void visitCrearTupla(CrearTupla n) {
        // Verificar si la tupla ya esta declarada
        if (ts.lookupSymbol(n.ident.nombre, "SimboloTupla") != null) {
            report_error(n.getLineNo(), "Tupla \"" + n.ident.nombre + "\" ya esta declarada.");
            return;
        }

    // Determinar los tipos de las expresiones
    String firstType = n.expr1.accept(this);
    String secondType = n.expr2.accept(this);

    if (firstType == null || secondType == null) {
        report_error(n.getLineNo(), "Error al determinar los tipos de la tupla.");
        return;
    }

    SimboloTupla tupla = new SimboloTupla(n.ident.nombre, firstType, secondType);
    ts.addSimbolo(tupla);
    }

    @Override
    public String visitIntLiteral(IntLiteral n) {
        return "int";
    }

    @Override
    public String visitTrue(True n) {
        return "boolean";
    }

    @Override
    public String visitFalse(False n) {
        return "boolean";
    }

    @Override
    public String visitIdentifier(Identifier n) {
        // Verificar si el identificador esta declarado
        Simbolo simbolo = ts.lookupSymbol(n.nombre);
        if (simbolo == null) {
            report_error(n.getLineNo(), "Identificador \"" + n.nombre + "\" no declarado.");
            return null;
        }
        return simbolo.getTipo();
    }

	// String nombre;
	@Override
    public String visitIdentifierExp(IdentifierExp n){
		// Verificar si el identificador esta declarado
        Simbolo simbolo = ts.lookupSymbol(n.nombre);
        if (simbolo == null) {
            report_error(n.getLineNo(), "Identificador \"" + n.nombre + "\" no declarado.");
            return null;
        }
        return simbolo.getTipo();
	}

	// Condition cond;
  	// StatementList stateList1,stateList2;
	@Override
    public void visitIf(If n){
        if (n.stateList1 != null) {
                ts = ts.enterScope("if", n);
                for (int i = 0; i < n.stateList1.size(); i++) {
            Statement stat = n.stateList1.get(i);
            if (stat != null) stat.accept(this);
                }
                ts = ts.exitScope();
            }

        if (n.stateList2 != null) {
                ts = ts.enterScope("else", n);
                for (int i = 0; i < n.stateList2.size(); i++) {
                    Statement stat = n.stateList2.get(i);
                    if (stat != null) stat.accept(this);
                }
                ts = ts.exitScope();
        }
    }

	// Condition cond;
  	// StatementList stateList;
	@Override
    public void visitWhile(While n){
	ts = ts.enterScope("while", n);
        for (Statement stat : n.stateList.listaStatements) {
            if (stat != null) stat.accept(this);
        }
        ts = ts.exitScope();
	}

	// Identifier ident;
  	// Expression expr;
	@Override
    public void visitAssign(Assign n){
        if ( n.ident != null ) n.ident.accept(this);
		if ( n.expr != null ) n.expr.accept(this);
	}                                

	// Identifier ident;
	@Override
    public void visitRead(Read n){
		Simbolo simbolo = ts.lookupSymbol(n.ident.nombre, "SimboloVar");
        if (simbolo == null) {
            report_error(n.getLineNo(), "Variable \"" + n.ident.nombre + "\" no esta declarada.");
        }
	} 

	// Expression expr;
    @Override
    public void visitWrite(Write n) {
        if (n.expr != null) {
            String tipo = (String) n.expr.accept(this);

            if (tipo == null) {
                report_error(n.getLineNo(), "La expresión del write no es valida.");
            }
        }
    }

	// Expression expr1, expr2;
	@Override
    public String visitAdd(Add n) {
        return "int";
    }

	// Expression expr1, expr2;
	@Override
    public String visitSub(Sub n) {
        return "int";
    }

	// Expression expr1, expr2;
	@Override
    public String visitMul(Mul n) {
        return "int";
    }

	// Expression expr1, expr2;
	@Override
    public String visitAnd(And n) {
        return "boolean";
    }

	// Expression expr1, expr2;
	@Override
    public String visitOr(Or n) {
        return "boolean";
    }

	// Expression expr;///////////////////////////////
	@Override
    public String visitNot(Not n){
		return "boolean";
	}

	// Expression expr1,expr2;
	@Override
    public String visitEqual(Equal n) {
        return "boolean";
    } 

	// Expression expr1, expr2;
	@Override
    public String visitNotEqual(NotEqual n) {
        return "boolean";
    }

	// Expression expr1, expr2;
	@Override
    public String visitGreaterThan(GreaterThan n) {
        return "boolean";
    }

	// Expression expr1,expr2;
	@Override
    public String visitLessThan(LessThan n) {
        return "boolean";
    }

	// Identifier ident;
  	// Expression expr;
	@Override
    public String visitGetTupla(GetTupla n) {
        SimboloTupla tupla = (SimboloTupla) ts.lookupSymbol(n.ident.nombre, "SimboloTupla");
        if (tupla == null) {
            report_error(n.getLineNo(), "Tupla \"" + n.ident.nombre + "\" no esta declarada.");
            return null;
        }
        return "elemento";
    }

	// Identifier ident;
  	// ExpressionList exprList;
	@Override
    public String visitLlamadaFuncion(LlamadaFuncion n) {
        SimboloFuncion funcion = (SimboloFuncion) ts.lookupSymbol(n.ident.nombre, "SimboloFuncion");
        if (funcion == null) {
            report_error(n.getLineNo(), "Función \"" + n.ident.nombre + "\" no esta declarada.");
            return null;
        }
        return funcion.getTipo();
    }
}