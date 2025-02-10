package AST.Visitor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TraducirEnsamblador {

    private List<String> cod3Dir;

    private Set<String> nombresVariables = new HashSet<>(); // Solo nombres de variables
    private Set<String> variables = new HashSet<>();

    private List<String> instrucciones = new ArrayList<>();

    //Mapa de String (nombre de la sub rutina) y Lista de Strings (instrucciones de la subrutina, incluyendo el RTS)
    private HashMap<String, List<String>> subrutinasMap = new HashMap<>();
    private String subrutinaActual = null; //Al encontrar una subrutina, se pone el nombre aquí y no se añaden instrucciones a la ejecución principal hasta llegar al RTS 

    private boolean hayRead = false;
    private boolean hayWrite = false;

    public TraducirEnsamblador(List<String> c3D) {
        cod3Dir = c3D;
    }

    /**
     * Genera código ensamblador a partir de una lista de instrucciones de
     * código de tres direcciones traduciendo línea a línea.
     *
     * @param nombreArchivo Nombre del archivo donde se generará el código
     * ensamblador.
     */
    public void generarEnsamblador(String nombreArchivo) {

        instrucciones.add("  START:                  ; first instruction of program\n");

        // Traducimos cada línea de código de 3 direcciones a ensamblador, además de llenar el set con las variables
        for (String line : cod3Dir) {
            String asmLine = traducirAEnsamblador(line);
            if (asmLine != null) {
                //Si no estamos en ninguna subrutina, simplemente escribimos el código en las isntrucciones principales
                if (subrutinaActual == null) {
                    instrucciones.add("      " + asmLine + "\n");

                } else { //Si subrutina actual no es null --> añadimos la instrucción a la lista de la subrutina
                    subrutinasMap.get(subrutinaActual).add(asmLine);
                }

            }
        }

        // Finalización del programa
        /*instrucciones.add("       MOVE.L #0, D0       ; Código de salida 0\n");
        instrucciones.add("       TRAP #0             ; Finaliza la ejecución\n");*/
        instrucciones.add("       MOVE.B #9, D0       ; Código de salida 0\n");
        instrucciones.add("       TRAP #15             ; Finaliza la ejecución\n");
        instrucciones.add("\n  SIMHALT                 ; halt simulator\n");

        //Comprobamos si existe alguna instrucción read o write y, en caso afirmativo, escribimos la subrutina correspondiente
        if (hayWrite) {
            subrutinasMap.put("print_int", new ArrayList<>());
            String aux = "        MOVE.L (SP)+, A6  ;Guardamos en A6 el valor de retorno de la pila\n"
                    + "              MOVE.L (SP)+, (A1)     ; Cargar el valor a imprimir desde la pila (primer parámetro)\n"
                    + "              MOVE.L #1,D0        ;Cargamos la instrucción de escribir por pantalla\n"
                    + "              TRAP #15 \n"
                    + "              MOVE.L A6,-(SP)\n"
                    + "              RTS                    ; Retornar de la subrutina\n";

            subrutinasMap.get("print_int").add(aux);
        }

        if (hayRead) {
            subrutinasMap.put("read_int", new ArrayList<>());
            String aux = "         MOVE.L (SP)+, A6  ;Guardamos en A6 el valor de retorno de la pila\n"
                    + "               MOVE #2, D0 \n"
                    + "               TRAP #15 \n"
                    + "               MOVE.L (A1), D7        ; Almacenar el valor leído en D7\n"
                    + "               MOVE.L A6,-(SP)\n"
                    + "               RTS                    ; Retornar de la subrutina\n";

            subrutinasMap.get("read_int").add(aux);
        }

        escribirEnsamblador(nombreArchivo);
    }

    /**
     * Traduce una línea de código de tres direcciones a ensamblador x86-64.
     *
     * @param line Línea de código de tres direcciones.
     * @return Línea de ensamblador correspondiente.
     */
    private String traducirAEnsamblador(String line) {
        // Separar tokens
        String[] tokens = splitTokens(line.trim());

        // Debugging de tokens
        //System.out.println("DEBUG: Procesando línea: " + line);
        //System.out.println("DEBUG: Tokens: " + Arrays.toString(tokens));

        if (tokens.length == 0) {
            return null; // Línea vacía o inválida
        }

        // Si no es una palabra reservada y es o un temporal ("t") o una variable y no tiene 7 tokens (no es la declaración de una tupla)
        if (!esPalReservada(tokens[0]) && (tokens[0].startsWith("t") || tokens[0].matches("[a-zA-Z_][a-zA-Z0-9_]*")) && tokens.length != 7) {

            //Comprobamos si la variable ya existe
            if (!nombresVariables.contains(tokens[0])) {
                //Si no existe, se mete en la lista de nombres
                nombresVariables.add(tokens[0]);

                //Creamos la instancia de la tabla de variables
                String aux = tokens[0] + "          DS.L    1\n";
                variables.add(aux);
            }

            //Seguimos con la propia instrucción
            // Asignación directa
            if (tokens.length == 3 && tokens[1].equals("=")) {
                //Si empieza por 't' o 'f' es una variable temporal, true o false
                if (tokens[2].equals("true")) {
                    return "MOVE.L #TRUE, (" + tokens[0] + ")";
                } else if (tokens[2].equals("false")) {
                    return "MOVE.L #FALSE, (" + tokens[0] + ")";
                } else if (tokens[2].startsWith("t")) {
                    return "MOVE.L (" + tokens[2] + ") " + ", (" + tokens[0] + ")";

                } else {  //Sinó es un entero
                    return "MOVE.L #" + tokens[2] + ", (" + tokens[0] + ")";
                }
            } // Operaciones aritméticas
            else if (tokens.length == 5 && tokens[1].equals("=")) {
                String op = tokens[3];
                String asmOp = switch (op) {
                    case "+" ->
                        "ADD.L";
                    case "-" ->
                        "SUB.L";
                    case "*" ->
                        "MULS";
                    default ->
                        null;
                };
                if (asmOp == null) {
                    throw new IllegalArgumentException("Operación no soportada: " + op);
                }
                return "MOVE.L (" + tokens[2] + ")" + ", D0\n            " + asmOp + " (" + tokens[4] + ")" + ", D0\n            MOVE.L D0, (" + tokens[0] + ")";
            }
        }

        // Condicionales
        if (tokens[0].equals("if")) {
            // Comparaciones simples
            //if ! bool1 goto e3 
            if (tokens.length == 5 && tokens[1].equals("!")) {
                return "MOVE.L (" + tokens[2] + ") , D0\n            EORI.L #1, D0\n" + "            TST.L D0\n" + "            BNE " + tokens[4];
            } // Comparaciones booleanas
            else if (tokens.length == 6 && tokens[2].equals("&&")) {
                return "MOVE.L (" + tokens[1] + "), D0\n            AND.L (" + tokens[3] + ")" + ", D0\n            TST.L D0\n            BNE " + tokens[5];
            } else if (tokens.length == 6 && tokens[2].equals("||")) {
                return "MOVE.L (" + tokens[1] + "), D0\n            OR.L (" + tokens[3] + "), D0\n            TST.L D0\n            BNE " + tokens[5];
            } else if (tokens.length == 6 && tokens[2].equals("<")) {
                return "MOVE.L (" + tokens[1] + "), D0\n            CMP.L (" + tokens[3] + "), D0\n            BLT " + tokens[5]; // Salto si es menor
            } else if (tokens.length == 6 && tokens[2].equals(">")) {
                return "MOVE.L (" + tokens[1] + "), D0\n            CMP.L (" + tokens[3] + "), D0\n            BGT " + tokens[5]; // Salto si es mayor
            } else if (tokens.length == 6 && tokens[2].equals("==")) {
                return "MOVE.L (" + tokens[1] + "), D0\n            CMP.L (" + tokens[3] + "), D0\n            BEQ " + tokens[5]; // Salto si es mayor
            }
        }

        // Boolean operations as direct assignments
        if (tokens.length >= 5 && tokens[1].equals("=")) {
            //x = ! bool
            if (tokens[2].equals("!")) {
                // Negación
                return "    MOVE.L (" + tokens[3] + "), D0\n" // Cargar el valor en D0
                        + "    EORI.L #1, D0\n" // Realizar la operación XOR con 1
                        + "    MOVE.L D0, (" + tokens[0]+ ")";        // Guardar el resultado en la variable de destino

            } else if (tokens[3].equals("&&")) { // x = bool1 && bool2
                return "    MOVE.L (" + tokens[2] + "), D0\n" // Cargar el primer operando en D0
                        + "    AND.L (" + tokens[4] + "), D0\n" // Realizar la operación AND con el segundo operando
                        + "    MOVE.L D0, (" + tokens[0] + ")";       // Guardar el resultado en la variable de destino

            } else if (tokens[3].equals("||")) { // x = bool1 || bool2
                return "    MOVE.L (" + tokens[2] + "), D0\n" // Cargar el primer operando en D0
                        + "    OR.L (" + tokens[4] + "), D0\n" // Realizar la operación OR con el segundo operando
                        + "    MOVE.L D0, (" + tokens[0] + ")";       // Guardar el resultado en la variable de destino
            }
        }

        // Saltos incondicionales
        if (tokens[0].equals("goto")) {
            return "BRA " + tokens[1];
        }

        // Etiquetas
        if (tokens[0].endsWith(":")) {
            if (tokens[0].equals("end_main:")) { //Si es la última etiqueta, hacemos un salto de línea antes de escribirla
                return "\n     " + tokens[0];

            } else if (!Character.isDigit(tokens[0].charAt(1)) && !tokens[0].equals("main:")) {
                //Si el segundo caracter no es un número -> la etiqueta representa una subrutina (a no ser que sea el main)
                subrutinaActual = tokens[0].substring(1, tokens[0].length() - 1); // Copiamos la etiqueta excepto los : (y subrutina actual deja de ser null)
                subrutinasMap.put(subrutinaActual, new ArrayList<>());      //Inicializamos la subrutina actual
                return null; //Pasamos a la siguiente instrucción
            } else {
                return tokens[0];
            }
        }

        // Parámetros y llamadas a funciones //
        if (tokens[0].equals("param_s")) { //Parametros para llamar a la función
            return "MOVE.L (" + tokens[1] + "), -(SP)";
        }

        if (tokens[0].equals("param_c")) { //Parametros que recibe la función
            return "    MOVE.L (SP)+, (" + tokens[1] + ")";
        }

        if (tokens[0].equals("call")) {
            return "       JSR " + tokens[1];
        }

        if (tokens[0].equals("rtn")) {
            String aux = "";
            //length = 4 --> se retorna un valor   (Ej: rtn nsumar, x)
            if(tokens.length == 4){
                aux += "    MOVE.L (" + tokens[3] + "), -(SP)  ; Metemos en la pila el valor a retornar \n";
            }
            aux += "          MOVE.L A6,-(SP)      ;Metemos en la pila la dirección de retorno \n          RTS";
            
            subrutinasMap.get(subrutinaActual).add(aux); //Añadimos el RTS y damos por finalizada la subrutina actual
            subrutinaActual = null; // Ya no estamos en una subrutina
            return null;            //Pasamos a la siguiente instruccion (esta ya se ha tratado)
        }

        //Preamble = principio de la función --> guardamos la dirección de retorno en A6
        if (tokens[0].equals("pmb")) {
            return "    MOVE.L (SP)+, A6   ;Metemos en A6 la dirección de retorno";
            //return "    ; Parámetros para " + tokens[1];
        }
        if (tokens.length >= 4 && tokens[1].equals("=") && tokens[2].equals("call")) {
            return "JSR " + tokens[3] + "\n            MOVE.L (SP)+, D6   ;Desempilamos el valor retornado por la funcion\n            MOVE.L D6, (" + tokens[0]+ ")";
        }

        // Operaciones de E/S
        if (tokens[0].equals("write")) {
            hayWrite = true;

            String target = tokens[2];
            return "MOVE.L (" + target + "), -(SP)\n            JSR print_int\n"; //Empilamos el valor a leer y vamos a la subrutina para hacerlo
            //return "    MOVE.L (" + target + "), -(SP)\n          JSR print_int\n      ADDQ.L #4, SP\n";
        }

        if (tokens[0].equals("read")) {
            hayRead = true;
            return "JSR read_int\n            MOVE.L D7, (" + tokens[2] + ")";
        }

        // Creación de la tupla
        // t12 = (t10, t11)
        if (tokens.length == 7 && tokens[1].equals("=") && tokens[2].equals("(") && tokens[4].equals(",")) {
            if (!nombresVariables.contains(tokens[0])) {
                nombresVariables.add(tokens[0]);

                String aux = tokens[0] + "          DS.L     2\n"; //Añadimos la tupla a las variables
                variables.add(aux);
            }

            String t1 = tokens[3];  // Primer valor de la tupla
            String t2 = tokens[5];  // Segundo valor de la tupla
            return "LEA " + tokens[0] + ", A0\n" // Cargar la dirección base de la tupla en A0
                    + "            MOVE.L " + t1 + ", (A0)\n" // Guardar el primer valor en la dirección base
                    + "            MOVE.L " + t2 + ", 4(A0)";    // Guardar el segundo valor en la posición desplazada en 4 bytes
        }

        // Acceso a tuplas
        // Ejemplo: t14 = get t13 from t
        if (tokens[2].equals("get") && tokens[4].equals("from")) {
            String offset = tokens[2].equals("0") ? "0" : "4";
            return "LEA " + tokens[3] + ", A0\n" // Cargar la dirección base en A0
                    + "            MOVE.L " + offset + "(A0), " + tokens[0]; // Acceder al elemento con desplazamiento
        }

        return null;   //Si no se puede traducir, se devuelve null
        // Lanza error si no se pudo traducir la línea
        //throw new RuntimeException("No se pudo traducir la línea: '" + line + "'. No se reconoce el formato.");
    }

    private void escribirEnsamblador(String nombreArchivo) {
        String nombArch = "src/programa/" + nombreArchivo + ".X68";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombArch))) {

            // Sección de texto donde se define el código ejecutable
            writer.write("  ORG    $1000\n;Constantes\nTRUE    EQU 1\nFALSE   EQU 0\n\n;Declaracion de variables\n");

            // Escribimos cada variable
            for (String line : variables) {
                writer.write(line + "\n");
            }

            // Escribimos cada instrucción (incluyendo el final del programa)
            for (String line : instrucciones) {
                writer.write("      " + line + "\n");
            }

            // Escribe las subrutinas al final
            for (HashMap.Entry<String, List<String>> entry : subrutinasMap.entrySet()) {

                //Escribimos el nombre de la subrutina
                writer.write("\n" + entry.getKey() + ":\n");

                //Imprimimos cada instrucción
                for (String instr : entry.getValue()) {
                    writer.write("      " + instr + "\n");
                }
            }

            writer.write("  END    START            ; last line of source\n");

            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de ensamblador: " + e.getMessage());
        }
    }

    private static String[] splitTokens(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '(') {
                // Complete any previous token
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken = new StringBuilder();
                }
                // Add opening parenthesis as a token
                tokens.add("(");
            } else if (c == ')') {
                // Complete any current token
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken = new StringBuilder();
                }
                // Add closing parenthesis as a token
                tokens.add(")");
            } else if (c == ',') {
                // In tuple, treat comma as a separator
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken = new StringBuilder();
                }
                tokens.add(",");
            } else if (Character.isWhitespace(c)) {
                // Complete any current token
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken = new StringBuilder();
                }
            } else if (c == '!') {
                //separamos ! de la variable booleana
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken = new StringBuilder();
                }
                tokens.add("!");
            } else {
                // Build current token
                currentToken.append(c);
            }
        }

        // Add last token if exists
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens.toArray(new String[0]);
    }

    private boolean esPalReservada(String pal) {
        //Si es alguna de las palabras reservadas (excepto true y false) se devuelve true
        switch (pal) {
            case "write": ;
            case "read": ;
            case "rtn": ;
            case "goto": ;
            case "if": ;
            case "pmb": ;
            case "get": ;
            case "param_s":
                return true;
        }
        //No es una palabra reservada --> devolvemos false
        return false;
    }
}