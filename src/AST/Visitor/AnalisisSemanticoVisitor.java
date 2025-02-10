package AST.Visitor;

import AST.*;
import programa.GestorErrores;
import tablaSimbolos.*;

public class AnalisisSemanticoVisitor implements ObjectVisitor {

    private GestorErrores gestorErrores;
    public TablaSimbolos ts =null;
    
    public AnalisisSemanticoVisitor(GestorErrores gestorErr) {
        this.ts = new TablaSimbolos();
        this.gestorErrores = gestorErr;
    }

    public void setTablaSimbolos(TablaSimbolos t){
        ts = t;
    }
    
    public TablaSimbolos getTablaSimbolos(){
        return ts;
    }
    
    // Método para agregar un error
    private void report_error(int linea, String mensaje) {
        gestorErrores.agregarError(linea, mensaje);
    }

    public String checkIntType(Object o)
	{
		if ( o != null && o instanceof String && ((String)o).equals("int") )
		{
			return "int";
		}
		return null;
	}

    //busca en la tabla de simbolos si existe el simbolo
    public Simbolo getSimbolo(ASTNode node){
        String name = null;

        if( node == null ) {
            return null;
        }
        else if ( node instanceof IdentifierExp ) {
			IdentifierExp i = (IdentifierExp)node;
			name = i.nombre;
		}
        else if ( node instanceof Identifier ) {
            Identifier i = (Identifier)node;
            name = i.nombre;
        }

        //se encontro simbolo
        Simbolo s = ts.lookupSymbol(name);
        if( s!= null){
            return s;
        }
		
	return null;
    }
    
    @Override
	public Object visitBooleanType(BooleanType node) {
		return "boolean";
	}

    @Override
	public Object visitIntegerType(IntegerType node) {
		return "int";
	}

    //MainClass:main
    @Override
    public Object visitProgram(Program node) {
        if ( node.main != null ) node.main.accept(this);
        return null;
    }

    //StatementList
    @Override
    public Object visitMainClass(MainClass node) {

        ts = ts.findScope("main");
        for (int i = 0; i < node.stateList.size(); i++) {
			node.stateList.get(i).accept(this);
		}
        ts = ts.exitScope();
        return null;
    }

    //Type ty;
    //Identifier ident;
    //Expression expr;
    @Override
    public Object visitVarDecl(VarDecl node) {
        Object o1 = ( node.ty != null ) ? node.ty.accept(this) : null;
        Object o2 = ( node.expr != null ) ? node.expr.accept(this) : null;
        
        if( o1 == null || o2 == null || !o1.equals(o2) ) 
        {
            report_error(node.getLineNo(), "Error en la declaracion de la variable: '" + node.ident.nombre + "'");
        }

        return o1;
    }

    // Identifier ident;
    // Expression expr;
    @Override
    public Object visitAssign(Assign node) {
        // Verificar si el identificador está declarado
        Simbolo simbolo = ts.lookupSymbol(node.ident.nombre, "SimboloVar");
        if (simbolo == null) {
            report_error(node.getLineNo(), "Variable \"" + node.ident.nombre + "\" no esta declarada.");
            return null;
        }

        // Verificar si el identificador es una constante
        if (simbolo instanceof SimboloVar && ((SimboloVar) simbolo).isConst()) {
            report_error(node.getLineNo(), "No se puede modificar la constante \"" + node.ident.nombre + "\".");
            return null;
        }

        Object o1 = ( node.ident != null ) ? node.ident.accept(this) : null;
		Object o2 = ( node.expr != null ) ? node.expr.accept(this) : null;

		if ( o1 == null || o2 == null || !o1.equals(o2) ) 
		{
			report_error(node.getLineNo(), "Tipos incompatibles en la asignación a \"" + node.ident.nombre + "\".");
		}
		
		return o1;
    }

    // Condition cond;
    // StatementList stateList1,stateList2;
    @Override
    public Object visitIf(If node) {
        // Verificar la condición del if
        Object o = (node.cond != null) ? node.cond.accept(this) : null;
        if (o == null || !(o instanceof String) || !((String) o).equals("boolean")) {
            report_error(node.getLineNo(), "La condicion del if debe ser boolean.");
        }

        // Procesar cada statement en stateList1
        if (node.stateList1 != null) {
            for (int i = 0; i < node.stateList1.size(); i++) {
                Statement statement = node.stateList1.get(i);
                if (statement != null) {
                    statement.accept(this);
                }
            }
        }

        // Procesar cada statement en stateList2 (else)
        if (node.stateList2 != null) {
            for (int i = 0; i < node.stateList2.size(); i++) {
                Statement statement = node.stateList2.get(i);
                if (statement != null) {
                    statement.accept(this);
                }
            }
        }

        return null;
    }

    // String s;
    public Object visitIdentifierExp(IdentifierExp n) {
		Simbolo s = getSimbolo(n);
		if ( s == null ) {
			report_error(n.getLineNo(), "Simbolo \""+n.nombre+"\" no fue declarado.");
			return null;
		}
		
		return s.getTipo();
	}

    //Condition cond;
    //StatementList stateList;
    @Override
    public Object visitWhile(While node) {
        Object o1 = ( node.cond != null ) ? node.cond.accept(this) : null;        
	if ( o1 == null || !(o1 instanceof String) || !((String)o1).equals("boolean") )
	{
            report_error(node.getLineNo(), "La condicion del while debe ser booleana.");
	}
        
        // Procesar cada statement en el stateList
        if (node.stateList != null) {
            for (int i = 0; i < node.stateList.size(); i++) {
                Statement statement = node.stateList.get(i);
                if (statement != null) {
                    statement.accept(this);
                }
            }
        }
		
	return null;
    }

    // AssignStatement assState;
    // Identifier ident;
    // Expression expr;
    // Condition cond;
    // StatementList stateList;
    @Override
    public Object visitFor(For node) {
        // Entrar al ambito del 'for'
        ts = ts.findScope("for");
        
        // Validar la declaración inicial
        Object oAssState = (node.assState != null) ? node.assState.accept(this) : null;
        if (oAssState == null || !(oAssState instanceof String) || !((String) oAssState).equals("int")) {
            report_error(node.getLineNo(), "El identificador en el 'for' debe ser un int.");
        }

        // Validar la condición
        Object oCond = (node.cond != null) ? node.cond.accept(this) : null;
        if (oCond == null || !(oCond instanceof String) || !((String) oCond).equals("boolean")) {
            report_error(node.getLineNo(), "La condicion en el 'for' debe ser tipo boolean.");
        }

        // Validar la actualización
        Object oIdent = (node.ident != null) ? node.ident.accept(this) : null;
        Object oExpr = (node.expr != null) ? node.expr.accept(this) : null;
        if (oIdent == null || !(oIdent instanceof String) || !((String) oIdent).equals("int")) {
            report_error(node.getLineNo(), "El segundo identificador en el 'for' debe ser un int.");
        }
        if (oExpr == null || !(oExpr instanceof String) || !((String) oExpr).equals("int")) {
            report_error(node.getLineNo(), "La expresion de incremento en el for debe ser tipo int.");
        }

        // Validar el cuerpo del for
        if (node.stateList != null) {
            for (int i = 0; i < node.stateList.size(); i++) {
                if (node.stateList.get(i) != null) node.stateList.get(i).accept(this);
            }
        }
        
        ts = ts.exitScope();

    return null; // El 'for' no devuelve un valor
}

    // Type ty;
    // Identifier ident;
    // ParamList paramList;
    // StatementList stateList;
    // Expression expr;
    @Override
    public Object visitFunctionDef(FunctionDef node) {
        // Abrir un nuevo ambito para la función
        ts = ts.findScope(node.ident.toString());

        // Validar y procesar el tipo de la función (si existe)
        Object returnType = (node.ty != null) ? node.ty.accept(this) : null;

        // Procesar parametros
        if ( node.paramList != null ) {
			for (int i = 0; i < node.paramList.size(); i++) {
				if ( node.paramList.get(i) != null ) node.paramList.get(i).accept(this);
			}
		}
        
        if ( node.stateList != null ) {
			for (int i = 0; i < node.stateList.size(); i++) {
				if ( node.stateList.get(i) != null ) node.stateList.get(i).accept(this);
			}
		}
        
        // Validar el retorno de la función
        if (node.ty != null) {
            // La función tiene un tipo, validar que expr coincide con el tipo
            Object returnValueType = (node.expr != null) ? node.expr.accept(this) : null;

            if (returnValueType == null || !returnValueType.equals(returnType)) {
                report_error(node.getLineNo(), "Tipo de retorno incorrecto en la funcion:  \"" + node.ident.toString() + "\".");
            }
        } else {
            // La función no tiene tipo, asegurar que no hay expresión de retorno
            if (node.expr != null) {
                report_error(node.getLineNo(), "Funcion \"" + node.ident.toString() + "\" no puede retornar un valor porque no tiene tipo.");
            }
        }

        // Cerrar el ambito de la función
        ts = ts.exitScope();

        return returnType; // Devolver el tipo de la función como resultado
    }

    // Identifier ident;
    // ExpressionList exprList;
    @Override
    public Object visitLlamadaFuncion(LlamadaFuncion node) {
            // Buscar la función en la tabla de símbolos
        Simbolo funcion = ts.lookupSymbol(node.ident.toString(), "SimboloFuncion");

        if (funcion == null) {
            // La función no esta declarada
            report_error(node.getLineNo(), "Función " + node.ident.toString() + " no declarada.");
            return null;
        }

        // Verificar que el símbolo encontrado es una función
        if (!(funcion instanceof SimboloFuncion)) {
            report_error(node.getLineNo(), node.ident.toString() + " no es una función.");
            return null;
        }

        SimboloFuncion simboloFuncion = (SimboloFuncion) funcion;

        // Verificar número de argumentos
        if (simboloFuncion.getparametros().size() != node.exprList.size()) {
            report_error(node.getLineNo(), "Número incorrecto de argumentos para la función " + node.ident.toString() +
                ". Esperados: " + simboloFuncion.getparametros().size() + ", proporcionados: " + node.exprList.size() + ".");
            return null;
        }

        // Verificar tipos de argumentos
        for (int i = 0; i < node.exprList.size(); i++) {
            // Obtener el tipo del argumento actual como String
            String argType = (String) node.exprList.get(i).accept(this);

            // Obtener el tipo del parametro esperado como String
            Simbolo parametro = simboloFuncion.getparametros().get(i);
            String paramType = parametro.getTipo();

            // Comparar tipos directamente
            if (!argType.equals(paramType)) {
                report_error(node.getLineNo(), "Tipo de argumento incompatible para el parametro " + parametro.getNombre() +
                    " de la función " + node.ident.toString() + ". Esperado: " + paramType + ", encontrado: " + argType + ".");
            }
        }

        // Retornar el tipo de retorno de la función
        return simboloFuncion.getTipo();
    }

    // Identifier ident;
    // Expression expr1, expr2;
    @Override
    public Object visitCrearTupla(CrearTupla node) {
        // Obtener los tipos de las expresiones
        String firstType = (String)node.expr1.accept(this);
        String secondType = (String)node.expr2.accept(this);
        
        // Validar los tipos (pueden ser int o boolean)
        if (!firstType.equals("int") && !firstType.equals("boolean")) {
            report_error(node.getLineNo(), "El primer elemento de la tupla debe ser int o boolean.");
            return null;
        }
        if (!secondType.equals("int") && !secondType.equals("boolean")) {
            report_error(node.getLineNo(), "El segundo elemento de la tupla debe ser int o boolean.");
            return null;
        }
        
        // Verificar si ya existe un símbolo con el mismo nombre
        Simbolo existente = ts.lookupSymbol(node.ident.toString(), "SimboloVar");
        if (existente != null) {
            report_error(node.getLineNo(), "La variable " + node.ident.toString() + " ya esta declarada.");
            return null;
        }

        // Crear y registrar el nuevo símbolo de la tupla
        SimboloTupla simbolo = new SimboloTupla(node.ident.toString(), firstType, secondType);
        //ts.addSimbolo(simbolo);
        
    // Usar el `Location` del nodo `CrearTupla` para instanciar el `TupleType`
    return new TupleType(firstType, secondType, node.getLocation());
    }

    //Identifier ident;
    //Expression expr;
    @Override
    public Object visitGetTupla(GetTupla node) {
        // Verificar que el identificador es una tupla
        Simbolo simbolo = ts.lookupSymbol(node.ident.toString(), "SimboloTupla");
        if (simbolo == null) {
            report_error(node.getLineNo(), "La variable " + node.ident.toString() + " no esta declarada.");
            return null;
        }

        if (!(simbolo instanceof SimboloTupla)) {
            report_error(node.getLineNo(), "La variable " + node.ident.toString() + " no es de tipo tupla.");
            return null;
        }

        SimboloTupla tupla = (SimboloTupla) simbolo;

        // Validar el índice
        Object indexType = node.expr.accept(this);
        if (!"int".equals(indexType)) {
            report_error(node.getLineNo(), "El índice debe ser de tipo int.");
            return null;
        }

        if (node.expr instanceof IntLiteral) {
            int index = ((IntLiteral) node.expr).valor;
            if (index < 0 || index > 1) {
                report_error(node.getLineNo(), "El índice de la tupla debe ser 0 o 1.");
                return null;
            }

            // Retornar el tipo correspondiente
            return (index == 0) ? tupla.getprimerTipo() : tupla.getsegundoTipo();
        }

        return "elemento de tupla"; // Caso genérico
    }

    // Operadores binarios

    // Expression expr1, expr2;
    @Override
    public Object visitAdd(Add node) { 
        String s1 = checkIntType(( node.expr1 != null ) ? node.expr1.accept(this) : null);
		String s2 = checkIntType(( node.expr2 != null ) ? node.expr2.accept(this) : null);
		if ( s1 != s2 || s1 != "int") {
			report_error(node.getLineNo(), "Operandos deben ser del tipo int en la operación de suma.");
			return null;
		}
		
		return s1;
    }
    
    // Expression expr1, expr2;
    @Override
    public Object visitSub(Sub node) { 
        String s1 = checkIntType(( node.expr1 != null ) ? node.expr1.accept(this) : null);
		String s2 = checkIntType(( node.expr2 != null ) ? node.expr2.accept(this) : null);
		if ( s1 != s2 || s1 != "int") {
			report_error(node.getLineNo(), "Operandos deben ser del tipo int en la operación de resta.");
			return null;
		}
		
		return s1;
    }
    
    @Override
    public Object visitMul(Mul node) { 
        String s1 = checkIntType(( node.expr1 != null ) ? node.expr1.accept(this) : null);
		String s2 = checkIntType(( node.expr2 != null ) ? node.expr2.accept(this) : null);
		if ( s1 != s2 || s1 != "int") {
			report_error(node.getLineNo(), "Operandos deben ser del tipo int en la operación de multiplicacion.");
			return null;
		}
		
		return s1; 
    }

    // Operadores de comparación
    // Expression expr1,expr2;
    @Override
    public Object visitLessThan(LessThan node) { 
        String s1 = checkIntType(( node.expr1 != null ) ? node.expr1.accept(this) : null);
		String s2 = checkIntType(( node.expr2 != null ) ? node.expr2.accept(this) : null);
		if ( s1 != s2 || s1 != "int") {
			report_error(node.getLineNo(), "Operandos deben ser del tipo int en la operación de menor a.");
			return null;
		}
		
		return "boolean"; 
    }
    
    // Expression expr1,expr2;
    @Override
    public Object visitGreaterThan(GreaterThan node) { 
        String s1 = checkIntType(( node.expr1 != null ) ? node.expr1.accept(this) : null);
		String s2 = checkIntType(( node.expr2 != null ) ? node.expr2.accept(this) : null);
		if ( s1 != s2 || s1 != "int") {
			report_error(node.getLineNo(), "Operandos deben ser del tipo int en la operación de mayor a.");
			return null;
		}
		
		return "boolean"; 
    }
    
    // Expression expr1,expr2;
    @Override
    public Object visitEqual(Equal node) { 
        String s1 = (node.expr1 != null) ? node.expr1.accept(this).toString() : null;
        String s2 = (node.expr2 != null) ? node.expr2.accept(this).toString() : null;

        // Verificar si ambos operandos son del mismo tipo y que sean validos (int o boolean)
        if ((s1 == null || s2 == null) || (!s1.equals(s2)) || (!s1.equals("int") && !s1.equals("boolean"))) {
            report_error(node.getLineNo(), "Operandos deben ser del mismo tipo (int o boolean) en la operación de igualdad.");
            return null;
        }

        // Retornar el tipo de resultado de la comparación
        return "boolean";
    }
    
    @Override
    public Object visitNotEqual(NotEqual node) { 
        String s1 = (node.expr1 != null) ? node.expr1.accept(this).toString() : null;
        String s2 = (node.expr2 != null) ? node.expr2.accept(this).toString() : null;

        // Verificar si ambos operandos son del mismo tipo y que sean validos (int o boolean)
        if ((s1 == null || s2 == null) || (!s1.equals(s2)) || (!s1.equals("int") && !s1.equals("boolean"))) {
            report_error(node.getLineNo(), "Operandos deben ser del mismo tipo (int o boolean) en la operación de igualdad.");
            return null;
        }

        // Retornar el tipo de resultado de la comparación
        return "boolean";
     }

    // Operadores lógicos
    @Override
    public Object visitAnd(And node) { 
        Object o1 = ( node.expr1 != null ) ? node.expr1.accept(this) : null;
		Object o2 = ( node.expr2 != null ) ? node.expr2.accept(this) : null;
		
		String s1 = (o1 != null && o1 instanceof String) ? (String)o1 : "";
		String s2 = (o2 != null && o2 instanceof String) ? (String)o2 : "";
		
		if ( s1 != s2 || s1 != "boolean") {
			report_error(node.getLineNo(), "Operandos deben ser del tipo boolean en la operación And.");
			return null;
		}
		
		return s1; 
    }
    
    @Override
    public Object visitOr(Or node) { 
        Object o1 = ( node.expr1 != null ) ? node.expr1.accept(this) : null;
		Object o2 = ( node.expr2 != null ) ? node.expr2.accept(this) : null;
		
		String s1 = (o1 != null && o1 instanceof String) ? (String)o1 : "";
		String s2 = (o2 != null && o2 instanceof String) ? (String)o2 : "";
		
		if ( s1 != s2 || s1 != "boolean") {
			report_error(node.getLineNo(), "Operandos deben ser del tipo boolean en la operación Or.");
			return null;
		}
		
		return s1;
     }

    // Expression expr
    @Override
    public Object visitNot(Not node) {
        return ( node.expr != null ) ? node.expr.accept(this) : null;
    }

    // Literales y otros
    @Override
    public Object visitIntLiteral(IntLiteral node) {
        return "int";
    }

    @Override
    public Object visitTrue(True node) {
        return "boolean";
    }

    @Override
    public Object visitFalse(False node) {
        return "boolean";
    }

    @Override
    public Object visitIdentifier(Identifier node) {
        Simbolo simbolo = getSimbolo(node);
        if (simbolo == null) {
            getSimbolo(node);
            report_error(node.getLineNo(), "Simbolo \""+node.nombre+"\" no fue declarado.");
            return null;
        }
        return simbolo.getTipo();
    }

    private boolean sonTiposCompatibles(Type t1, Type t2) {
        if (t1 == null || t2 == null) return false;
        if (t1.getClass() == t2.getClass()) return true;
        return false;
    }

    // Expression expr;
    @Override
    public Object visitWrite(Write node) {
		if ( node.expr != null ) node.expr.accept(this);
		return null;
	}

    // Type ty;
    // Identifier ident;
    @Override
    public Object visitParam(Param node) {
		return ( node.ty != null ) ? node.ty.accept(this) : null;
	}

    // Identifier ident;
    @Override
    public Object visitRead(Read node) {
        // Verificar si el identificador esta declarado
        Simbolo simbolo = ts.lookupSymbol(node.ident.toString(), "SimboloVar");
        if (simbolo == null) {
            report_error(node.getLineNo(), "Variable " + node.ident.toString() + " no esta declarada.");
            return null;
        }

        // Verificar que el tipo del identificador sea "int"
        if (!simbolo.getTipo().equals("int")) {
            report_error(node.getLineNo(), "La variable " + node.ident.toString() + " debe ser de tipo int para usarla en read().");
            return null;
        }

        // Si todo es correcto, no hay errores
        return null;
    }

    // Type ty;
    // Identifier ident;
    // Expression expr;
    @Override
    public Object visitConst(Const node) {
        // Verificar si la constante ya esta declarada
        Simbolo existente = ts.lookupSymbol(node.ident.toString(), "SimboloVar");
        if (existente == null) {
            report_error(node.getLineNo(), " La constante " + node.ident.toString() + " no esta declarada.");
            return null;
        }

        // Verificar el tipo de la expresión
        String exprType = (String) node.expr.accept(this);
        if (!node.ty.toString().equals(exprType)) {
            report_error(node.getLineNo(), "Tipo de la expresión no coincide con el tipo de la constante. Esperado: " + node.ty + ", encontrado: " + exprType + ".");
            return null;
        }

        // Registrar la constante en la tabla de símbolos
        SimboloVar constante = new SimboloVar(node.ident.toString(), node.ty.toString());
        //ts.addSimbolo(constante);

        // Retornar el tipo de la constante (si es necesario)
        return node.ty;
	}

    // String firstType;
    // String secondType;
    @Override
    public Object visitTupleType(TupleType node) {
        // Retornar la representación del tipo como una cadena
        return "Tuple(" + node.getFirstType() + ", " + node.getSecondType() + ")";
    }
}