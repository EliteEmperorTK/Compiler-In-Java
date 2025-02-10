package AST.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import AST.*;

public class CodigoTresDireccionesVisitor implements Visitor {

    // Lista que almacenará todas las instrucciones de código intermedio
    private List<String> cod3Dir = new ArrayList<>();

    // Contadores para variables temporales y etiquetas
    private int contadorTemp = 0;    // Para generar nombres como t1, t2, ...
    private int contadorEtiquetas = 0;   // Para etiquetas como e1, e2, ...

    /**
     * Devuelve el nombre de la nueva variable temporal.
     */
    private String newTemp() {
        contadorTemp++;
        return ("t" + contadorTemp);
    }

    /**
     * Devuelve el nombre de la nueva etiqueta.
     */
    private String newLabel() {
        contadorEtiquetas++;
        return ("e" + contadorEtiquetas);
    }

    /**
     * Devuelve la lista completa de instrucciones C3@ generadas.
     */
    public List<String> getCod3Dir() {
        return cod3Dir;
    }

    /**
     * Imprime la lista completa de instrucciones C3@ generadas nombreArchivo =
     * el nombre del archivo sin la extensión en el que se escribirá todo el
     * código de 3 direcciones
     */
    public void imprimircod3Dir(String nombreArchivo) {
        //Formato .txt
        String nombArch = "src/programa/" + nombreArchivo + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombArch))) {
            // Iteramos sobre cada línea de la lista y la escribimos en el archivo
            for (String line : cod3Dir) {
                writer.write(line + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            System.err.println("Error al escribir en el archivo: " + ex.getMessage());
        }
    }

    /**
     * Visita el nodo 'Program' y genera instrucciones C3@
     */
    @Override
    public void visitProgram(Program n) {
        // Procesa el main
        n.main.accept(this);
    }

    /**
     * Visita el nodo 'MainClass' y genera instrucciones C3@
     */
    @Override
    public void visitMainClass(MainClass n) {
        // Etiqueta que marca el inicio del método main
        cod3Dir.add("main:   skip");
        // Genera código para el cuerpo del método 
        //El cuerpo del else
        for (Statement statement : n.stateList.listaStatements) {
            statement.accept(this);
        }

        // Etiqueta que marca el final del método main
        cod3Dir.add("end_main: skip");
    }

    /**
     * Visita el nodo 'Assign' y genera instrucciones C3@
     */
    @Override
    public void visitAssign(Assign n) {
        // Visita la expresión para obtener su C3@ y obtenemos su último temp
        String tempExpr = n.expr.accept(this);

        // Asigna el valor del temporal a la variable objetivo
        cod3Dir.add("	" + n.ident.accept(this) + " = " + tempExpr);
    }

    /**
     * Visita el nodo 'Add' y genera instrucciones C3@
     */
    @Override
    public String visitAdd(Add n) {
        // Procesa el operando izquierdo y guarda el t final
        String expr1Temp = n.expr1.accept(this);

        // Procesa el operando derecho
        String expr2Temp = n.expr2.accept(this);

        // Usa un temporal para almacenar el resultado de la suma
        String temp = newTemp();
        cod3Dir.add("	" + temp + " = " + expr1Temp + " + " + expr2Temp);
        return temp; // Guarda el resultado en el nodo para uso posterior
    }

    /**
     * Visita el nodo 'If' y genera instrucciones C3@
     */
    @Override
    public void visitIf(If n) {
        // Procesa la condición del 'if'
        String condTemp = n.cond.accept(this);

        // Genera etiquetas para las ramas y el final
        String etiqNormal = newLabel();

        String etiqElse = null; // Etiqueta para la rama 'else'
        //Si existe lista de statements 2 --> es porque hay un else
        if (n.stateList2 != null) {
            etiqElse = newLabel(); // Etiqueta para la rama 'else'
        }

        String etiqFinal = newLabel();  // Etiqueta para el final del 'if'

        // Si la condición es verdadera, salta a la primera instrucción
        cod3Dir.add("	if " + condTemp + " goto " + etiqNormal);

        // Si hay else, salta a la etiqueta del else
        if (etiqElse != null) {
            cod3Dir.add("	goto " + etiqElse);
        } else {
            // Si no hay else, salta directamente al final
            cod3Dir.add("	goto " + etiqFinal);
        }

        cod3Dir.add(etiqNormal + ":     skip");

        //El cuerpo del if
        for (Statement statement : n.stateList1.listaStatements) {
            statement.accept(this);
        }

        //Si hay else, viene ahora, sinó, es el final de la función
        if (etiqElse != null) {
            cod3Dir.add("        goto " + etiqFinal); //Para saltar al final en caso de que entre al if

            cod3Dir.add(etiqElse + ":     skip");

            //El cuerpo del else
            for (Statement statement : n.stateList2.listaStatements) {
                statement.accept(this);
            }
        }
        //Etiqueta del final
        cod3Dir.add(etiqFinal + ":     skip");
    }

    /**
     * Visita el nodo 'While' y genera instrucciones C3@
     */
    @Override
    public void visitWhile(While n) {
        // Genera etiquetas para el inicio y el final del bucle
        String etiqInicial = newLabel();
        String etiqBloque = newLabel(); 	//Para el principio del código
        String etiqFinal = newLabel();

        // Procesa la condición del bucle
        String condTemp = n.cond.accept(this);

        // Marca el inicio del bucle
        cod3Dir.add(etiqInicial + ":        skip");

        // Si la condición es falsa, salta al final del bucle
        cod3Dir.add("		 if " + condTemp + " goto " + etiqBloque);
        cod3Dir.add("		 goto " + etiqFinal);

        cod3Dir.add(etiqBloque + ":        skip");

        // Genera el cuerpo del bucle
        for (Statement statement : n.stateList.listaStatements) {
            statement.accept(this);
        }

        // Salta al inicio para reevaluar la condición
        cod3Dir.add("		 goto " + etiqInicial);

        // Marca el final del bucle
        cod3Dir.add(etiqFinal + ":        skip");
    }

    /**
     * Visita el nodo 'FunctionDef' y genera instrucciones C3@
     */
    @Override
    public void visitFunctionDef(FunctionDef n) {
        // Marca el inicio de la función
        cod3Dir.add("e" + n.ident.accept(this) + ": skip");

        //Uso el nombre de la función
        cod3Dir.add("        pmb n" + n.ident.accept(this));

        // Procesamos los parámetros de la función
        if(n.paramList != null){
			for (Param param : n.paramList.listaParametros) {
        		param.accept(this);
    		}
		}
        // Genera el cuerpo de la función
        //Ahora escribimos todos los statements que hay dentro del cuerpo del for
        for (Statement statement : n.stateList.listaStatements) {
            statement.accept(this);
        }

        //Si su expr tiene valor devolvemos -> se devuelve un valor
        if (n.expr != null) {
            String varTempReturn = n.expr.accept(this);
            cod3Dir.add("        rtn n" + n.ident.accept(this) + ", " + varTempReturn);
        } else {
            //En caso de que no haya un return explícito, escribimos un return vacío para que sepa que tiene que volver del método
            cod3Dir.add("        rtn n" + n.ident.accept(this));
        }

        // Marca el final de la función
        //cod3Dir.add("end_" + n.ident.accept(this) + ":       skip");
    }

    /**
     * Visita el nodo 'VarDecl' y genera instrucciones C3@
     */
    @Override
    public void visitVarDecl(VarDecl n) {
        //Procesamos la expresión de inicialización, si existe
        String initTemp = initTemp = n.expr.accept(this); // Procesamos la expresión de inicialización

        // Generamos la instrucción para declarar la variable e inicializarla
        cod3Dir.add("        " + n.ident.accept(this) + " = " + initTemp);
    }

    /**
     * Visita el nodo 'And' y genera instrucciones C3@
     */
    @Override
    public String visitAnd(And n) {
        // Procesa los operandos izquierdo y derecho
        String tempIzq = n.expr1.accept(this);
        String tempDer = n.expr2.accept(this);

        String condicionResultante = (tempIzq + " && " + tempDer);

        // Usa un temporal para almacenar el resultado del `and`
        /*String tempResult = newTemp();
    	cod3Dir.add("        " + tempResult + " = " + tempIzq + " && " + tempDer);
		return tempResult;*/
        return condicionResultante;
    }

    /**
     * Visita el nodo 'LessThan' y genera instrucciones C3@
     */
    @Override
    public String visitLessThan(LessThan n) {
        // Procesa los operandos izquierdo y derecho
        String tempIzq = n.expr1.accept(this);
        String tempDer = n.expr2.accept(this);

        String condicionResultante = (tempIzq + " < " + tempDer);

        // Usa un temporal para almacenar el resultado de la comparación
        /*String tempResult = newTemp();
    	cod3Dir.add("        " + tempResult + " = " + tempIzq + " < " + tempDer);
		return tempResult;*/
        return condicionResultante;
    }

    /**
     * Visita el nodo 'True' y genera instrucciones C3@
     */
    @Override
    public String visitTrue(True n) {
        // Devuelve el valor verdadero en un temporal
        String tempResult = newTemp();
        cod3Dir.add("        " + tempResult + " = true");
        return tempResult;
    }

    /**
     * Visita el nodo 'False' y genera instrucciones C3@
     */
    @Override
    public String visitFalse(False n) {
        // Devuelve el valor falso en un temporal
        String tempResult = newTemp();
        cod3Dir.add("        " + tempResult + " = false");
        return tempResult;
    }

    /**
     * Visita el nodo 'Not' y genera instrucciones C3@
     */
    @Override
    public String visitNot(Not n) {
        // Procesa la expresión dentro del `not`
        String tempExpr = n.expr.accept(this);

        String condicionResultante = ("!" + tempExpr);
        // Usa un temporal para almacenar el resultado del `not`
        /*String tempResult = newTemp();
    	cod3Dir.add("        " + tempResult + " = !" + tempExpr);
		return tempResult;*/

        return condicionResultante;
    }

    /**
     * Este nodo no genera código por sí mismo, pero devuelve su nombre para que
     * pueda ser usado en otros nodos
     */
    @Override
    public String visitIdentifier(Identifier n) {
        return n.toString();
    }

    /**
     * Este nodo no genera código por sí mismo, pero devuelve su nombre para que
     * pueda ser usado en otros nodos
     */
    @Override
    public String visitIdentifierExp(IdentifierExp n) {
        return n.toString();
    }

    /**
     * Visita el nodo 'Sub' y genera instrucciones C3@
     */
    @Override
    public String visitSub(Sub n) {
        // Procesa los operandos izquierdo y derecho
        String tempIzq = n.expr1.accept(this);
        String tempDer = n.expr2.accept(this);

        // Usa un temporal para almacenar el resultado de la resta
        String tempResult = newTemp();
        cod3Dir.add("        " + tempResult + " = " + tempIzq + " - " + tempDer);
        return tempResult;
    }

    /**
     * Visita el nodo 'Mul' y genera instrucciones C3@
     */
    @Override
    public String visitMul(Mul n) {
        // Procesa los operandos izquierdo y derecho
        String tempIzq = n.expr1.accept(this);
        String tempDer = n.expr2.accept(this);

        // Usa un temporal para almacenar el resultado de la multiplicación
        String tempResult = newTemp();
        cod3Dir.add("        " + tempResult + " = " + tempIzq + " * " + tempDer);
        return tempResult;
    }

    /**
     * Visita el nodo 'Param' y genera instrucciones C3@
     */
    @Override
    public void visitParam(Param n) {        
        cod3Dir.add("param_c " + n.ident.accept(this));
    }

    /**
     * Visita el nodo 'Equal' (==) y genera instrucciones C3@
     */
    @Override
    public String visitEqual(Equal n) {
        // Procesa los operandos izquierdo y derecho
        String tempIzq = n.expr1.accept(this);
        String tempDer = n.expr2.accept(this);

        String condicionResultante = (tempIzq + " == " + tempDer);
        // Usa un temporal para almacenar el resultado de la comparación
        //String tempResult = newTemp();
        //cod3Dir.add("        " + tempResult + " = " + tempIzq + " == " + tempDer);
        //return tempResult;
        return condicionResultante;
    }

    /**
     * Visita el nodo 'NotEqual' (!=) y genera instrucciones C3@
     */
    @Override
    public String visitNotEqual(NotEqual n) {
        // Procesa los operandos izquierdo y derecho
        String tempIzq = n.expr1.accept(this);
        String tempDer = n.expr2.accept(this);

        String condicionResultante = (tempIzq + " != " + tempDer);
        // Usa un temporal para almacenar el resultado de la comparación
        //String tempResult = newTemp();
        //cod3Dir.add("        " + tempResult + " = " + tempIzq + " != " + tempDer);
        //return tempResult;
        return condicionResultante;
    }

    /**
     * Visita el nodo 'GreaterThan' (>) y genera instrucciones C3@
     */
    @Override
    public String visitGreaterThan(GreaterThan n) {
        // Procesa los operandos izquierdo y derecho
        String tempIzq = n.expr1.accept(this);
        String tempDer = n.expr2.accept(this);

        String condicionResultante = (tempIzq + " > " + tempDer);

        // Usa un temporal para almacenar el resultado de la comparación
        /*String tempResult = newTemp();
    	cod3Dir.add("        " + tempResult + " = " + tempIzq + " > " + tempDer);
		return tempResult;*/
        return condicionResultante;
    }

    /**
     * Visita el nodo 'Or' (||) y genera instrucciones C3@
     */
    @Override
    public String visitOr(Or n) {
        // Procesa los operandos izquierdo y derecho
        String tempIzq = n.expr1.accept(this);
        String tempDer = n.expr2.accept(this);

        String condicionResultante = (tempIzq + " || " + tempDer);
        // Usa un temporal para almacenar el resultado del `or`
        /*String tempResult = newTemp();
    	cod3Dir.add("        " + tempResult + " = " + tempIzq + " || " + tempDer);
		return tempResult;*/
        return condicionResultante;
    }

    /**
     * Visita el nodo 'For' y genera instrucciones C3@
     */
    @Override
    public void visitFor(For n) {
        // Genera etiquetas para las partes del bucle
        String etiqCondicion = newLabel();
        String etiqCuerpo = newLabel();
        String etiqFinal = newLabel();

        // Genera el código de inicialización
        //String tempAuxInicializacion = n.expr1.accept(this); 						//Se genera la expresión
        //cod3Dir.add("		" + n.ident1.toString() + " = " + tempAuxInicializacion);	//Y se asigna
        n.assState.accept(this);

        // Etiqueta para la condición
        cod3Dir.add(etiqCondicion + ":     skip");
        String condTemp = n.cond.accept(this);

        cod3Dir.add("        if " + condTemp + " goto " + etiqCuerpo);
        cod3Dir.add("        goto " + etiqFinal);

        // Cuerpo del bucle
        cod3Dir.add(etiqCuerpo + ":     skip");

        //Ahora escribimos todos los statements que hay dentro del cuerpo del for
        for (Statement statement : n.stateList.listaStatements) {
            statement.accept(this);
        }

        // Incremento
        String tempAuxIncremento = n.expr.accept(this); 						//Se genera la expresión
        cod3Dir.add("        " + n.ident.toString() + " = " + tempAuxIncremento);	//Y se asigna
        //Salto al inicio
        cod3Dir.add("        goto " + etiqCondicion);

        // Marca el final del bucle
        cod3Dir.add(etiqFinal + ":     skip");
    }

    /**
     * Visita el nodo 'Read' y genera instrucciones C3@
     */
    @Override
    public void visitRead(Read n) {
        // Lee un valor y lo asigna a una variable
        cod3Dir.add("        " + "read(" + n.ident.accept(this) + ")");
    }

    /**
     * Visita el nodo 'For' y genera instrucciones C3@
     */
    @Override
    public void visitWrite(Write n) {
        // Escribe una expresión
        String tempExpr = n.expr.accept(this);
        cod3Dir.add("        write(" + tempExpr + ")");
    }

    /**
     * Visita el nodo 'Const' y genera instrucciones C3@
     */
    @Override
    public void visitConst(Const n) {
        //Primero evaluamos la expresión (lo de después del =)
        String tempExpr = n.expr.accept(this);

        // Declara una constante
        cod3Dir.add("        " + n.ident.accept(this) + " = " + tempExpr);
    }

    /**
     * Visita el nodo 'IntLiteral' y genera instrucciones C3@
     */
    @Override
    public String visitIntLiteral(IntLiteral n) {
        // Devuelve un literal entero
        String tempResult = newTemp();
        cod3Dir.add("        " + tempResult + " = " + n.valor);

        return tempResult;
    }

    /**
     * Visita el nodo 'ExpressionList'
     */
    //@Override
    //public void visitExpressionList(ExpressionList n) {
    //No genera instrucciones por si mismas (ya la recorre LlamadaFuncion)
    //}
    /**
     * Visita el nodo 'LlamadaFuncion' y genera instrucciones C3@
     */
    @Override
    public String visitLlamadaFuncion(LlamadaFuncion n) {
        // Procesa los argumentos pasados por parámetro recorriendo
        // cada expresión de la lista de expresiones asignada a la llamada

        // Recorrer la lista lista de parámetros al revés
        for (int i = n.exprList.listaExpr.size() - 1; i >= 0; i--) {
            Expression param = n.exprList.listaExpr.get(i); // Obtenemos la expresión actual
            String argTemp = param.accept(this);           	// La expresión 
            cod3Dir.add("        param_s " + argTemp);      	// Agregar al código de 3 direcciones
        }

        // Escribimos la propia llamada a la función
        String resultTemp = newTemp();
        cod3Dir.add("        " + resultTemp + " = call " + n.ident.accept(this) + "");
        return resultTemp;
    }

    /**
     * Visita el nodo 'CrearTupla' y genera instrucciones C3@
     */
    @Override
    public void visitCrearTupla(CrearTupla n) {
        // Crea una tupla con los valores proporcionados    	
        String valor1 = n.expr1.accept(this);
        String valor2 = n.expr2.accept(this);

        //Creamos la última variable temporal
        String tempParentesis = newTemp();
        cod3Dir.add("        " + tempParentesis + " = (" + valor1 + ", " + valor2 + ")");

        //Asignamos el valor a la variable
        cod3Dir.add("        " + n.ident.accept(this) + " = " + tempParentesis);
    }

    /**
     * Visita el nodo 'GetTupla' y genera instrucciones C3@
     */
    @Override
    public String visitGetTupla(GetTupla n) {
        // Obtiene el índice que se busca en el get (0) o (1) en formato de variable temporal
        String tempValGet = n.expr.accept(this);

        //Alamacena el valor del get en una variable temporal
        String tempResult = newTemp();
        cod3Dir.add("  	" + tempResult + " = get " + tempValGet + " from " + n.ident.accept(this));
        return tempResult;
    }

    /**
     * Visita el nodo 'BooleanType' (no genera instrucciones)
     */
    @Override
    public void visitBooleanType(BooleanType n) {

    }

    /**
     * Visita el nodo 'IntegerType' (no genera instrucciones)
     */
    @Override
    public void visitIntegerType(IntegerType n) {

    }

    /**
     * Visita el nodo 'TupleType' (no genera instrucciones)
     */
    @Override
    public void visitTupleType(TupleType n) {

    }
}
