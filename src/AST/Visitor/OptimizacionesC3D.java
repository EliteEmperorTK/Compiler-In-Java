package AST.Visitor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OptimizacionesC3D {
    private List<String> codigoOptimizado;
    private int contadorOptimizaciones = 0; // Contador de optimizaciones realizadas

    public OptimizacionesC3D(List<String> cod3Dir) {
        // Realizar una copia del código de entrada para trabajar sobre ella
        this.codigoOptimizado = new ArrayList<>(cod3Dir);
    }

    // Método para cargar código desde un archivo
    public void cargarDesdeArchivo(String nombreArchivo) {
        String nombArch = "src/programa/" + nombreArchivo;
        try (BufferedReader reader = new BufferedReader(new FileReader(nombArch))) {
            codigoOptimizado = new ArrayList<>(); // Limpiar el código anterior
            String linea;
            while ((linea = reader.readLine()) != null) {
                codigoOptimizado.add(linea);
            }
            //System.out.println("Codigo cargado correctamente desde: " + nombreArchivo);
        } catch (IOException ex) {
            System.err.println("Error al leer el archivo: " + ex.getMessage());
        }
    }
    
    // Método para ejecutar todas las optimizaciones
    public void optimizar() {
        eliminarCodigoInaccesible();
        reducirRamificaciones();
        eliminarEtiquetasRedundantes();
        operacionesConstantes();
        propagacionDeCopias();
        System.out.println("Optimizacion completada. Total optimizaciones aplicadas: " + contadorOptimizaciones);
    }
    
    // Optimización 1: Eliminación de código inaccesible
    private void eliminarCodigoInaccesible() {
        //System.out.println("Aplicando optimizacion: Eliminacion de codigo inaccesible...");
        int contador1=contadorOptimizaciones;
        Set<String> etiquetasReferenciadas = new HashSet<>();
        Set<Integer> lineasInaccesibles = new HashSet<>();
        Set<Integer> lineasIgnorar = new HashSet<>();

        // Paso 1: Detectar etiquetas referenciadas en saltos
        //System.out.println("Detectando etiquetas referenciadas...");
        for (String instruccion : codigoOptimizado) {
            if (instruccion.contains("goto")) {
                String etiqueta = extraerEtiquetaDeSalto(instruccion);
                if (etiqueta != null) {
                    etiquetasReferenciadas.add(etiqueta);
                    //System.out.println("Etiqueta referenciada detectada: " + etiqueta);
                }
            }
        }

        // Paso 2: Identificar instrucciones inaccesibles entre `goto` y su destino
        //System.out.println("\nMarcando lineas inaccesibles...");
        for (int i = 0; i < codigoOptimizado.size(); i++) {
            String instruccion = codigoOptimizado.get(i);
            //System.out.println("Procesando instruccion: [" + (i+1) + "] " + instruccion);
            //si ya fue marcada como innacesible se skipea el check
            if(lineasInaccesibles.contains(i)){
                continue;
            }
            if (instruccion.startsWith("goto")) {
                String etiquetaDestino = extraerEtiquetaDeSalto(instruccion);
                int lineaDestino = buscarLineaPorEtiqueta(etiquetaDestino);
                //System.out.println("Linea destino: [" + (lineaDestino+1) + "] ");

                // Verificar si se encontro la etiqueta de destino
                if (lineaDestino == -1) {
                    //System.out.println("Advertencia: No se encontro la etiqueta de destino: " + etiquetaDestino);
                    continue;
                }
                
                // si la linea destino es menor a la actual(i) procesamos las lineas
                // de destino hasta actual, si estan marcadas como innacesibles las quitamos
                if(lineaDestino<i){
                    for(int k=lineaDestino; k<i;k++){
                        if(lineasInaccesibles.contains(k)){
                            lineasInaccesibles.remove(k);
                            //System.out.println("Quitando linea como inaccesible: [" + (k+1) + "] ");
                        }
                    }
                }

                // Marcar lineas entre el `goto` y su destino como inaccesibles
                if (lineaDestino > i) {
                    for (int j = i + 1; j < lineaDestino; j++) {
                        //si la linea tiene etiqueta
                        String etActual = lineaActualtieneEtiqueta(codigoOptimizado.get(j));
                        if(etActual!=null){
                            //System.out.println("etiqueta de la linea actual: " + etActual);
                            //si la etiqueta es referenciada
                            if(etiquetasReferenciadas.contains(etActual)){
                                int referencia = buscarLineaReferenciaDe(etActual);
                                //si donde se referencia la etiqueta es accesible
                                if(!lineasInaccesibles.contains(referencia)){
                                    for (int k = i + 1; k < lineaDestino; k++) {
                                        lineasIgnorar.add(k);
                                        //System.out.println("Marcando linea como ignorar: [" + (k+1) + "] ");
                                    }
                                }
                            }       
                        }
                        //no marca las etiquetas como innacesibles
                        //if (!codigoOptimizado.get(j).endsWith(": skip")) {
                        //    lineasInaccesibles.add(j);
                        //    System.out.println("Marcando linea como inaccesible: [" + (j+1) + "] " + codigoOptimizado.get(j));
                        //}
                        if(lineasIgnorar.contains(j)){
                            
                        }else{
                        lineasInaccesibles.add(j);
                        //System.out.println("Marcando linea como inaccesible: [" + (j+1) + "] " + codigoOptimizado.get(j));
                        }
                        
                    }
                }
            }
        }

        // Paso 3: Eliminar etiquetas no referenciadas y lineas marcadas
        //System.out.println("\nEliminando lineas y etiquetas no usadas...");
        List<String> codigoFiltrado = new ArrayList<>();
        for (int i = 0; i < codigoOptimizado.size(); i++) {
            String instruccion = codigoOptimizado.get(i);

            // Si la linea esta marcada como inaccesible, eliminarla
            if (lineasInaccesibles.contains(i) && !lineasIgnorar.contains(i)) {
                contadorOptimizaciones++; // Contar cada linea eliminada
                //System.out.println("Eliminando linea inaccesible: [" + (i+1) + "] " + instruccion);
                continue;
            }

            // Mantener la linea si no esta marcada para eliminacion
            codigoFiltrado.add(instruccion);
        }

        // Actualizar el codigo optimizado
        codigoOptimizado = codigoFiltrado;
        //System.out.println("Optimizacion de Eliminacion de codigo inaccesible completada.optimizaciones:"+(contadorOptimizaciones-contador1));
    }
    
    // Optimización 6: Operaciones constantes
    private void operacionesConstantes() {
        //System.out.println("Aplicando optimizacion: Operaciones constantes...");
        int contador1 = contadorOptimizaciones;
        List<String> codigoNuevo = new ArrayList<>();

        for (String linea : codigoOptimizado) {
            //System.out.println("Procesando linea: " + linea);

            // Detectar si es una operación aritmética simple
            if (esOperacionConstante(linea)) {
                // Separar la instrucción en partes
                String[] partes = linea.split("=");
                String variable = partes[0].trim(); // tX
                String operacion = partes[1].trim(); // Operacion

                // Evaluar la operación
                String resultado = evaluarOperacion(operacion);
                if (resultado != null) {
                    // Reemplazar la operación por el resultado
                    codigoNuevo.add(variable + " = " + resultado);
                    contadorOptimizaciones++; // Contar la optimización
                    continue;
                }
            }

            // Agregar la línea si no fue optimizada
            codigoNuevo.add(linea);
        }

        // Actualizar el código optimizado
        codigoOptimizado = codigoNuevo;
        //System.out.println("Optimizacion de operaciones constantes completada.optimizaciones:"+(contadorOptimizaciones-contador1));
    }
    
    // Optimización 2: Reducción de ramificaciones
    private void reducirRamificaciones() {
        //System.out.println("Aplicando optimizacion: Reduccion de ramificaciones...");
        int contador1= contadorOptimizaciones;
        
        // Lista que almacenará el código después de optimizar ramificaciones
        List<String> codigoOptimizadoTemp = new ArrayList<>();

        for (int i = 0; i < codigoOptimizado.size(); i++) {
            String instruccion = codigoOptimizado.get(i);

            // Verificar si la instrucción actual es un salto condicional
            if (esCondicional(instruccion) && i + 1 < codigoOptimizado.size()) {
                String siguienteInstruccion = codigoOptimizado.get(i + 1);

                // Verificar si la siguiente instrucción es un salto incondicional
                if (siguienteInstruccion.contains("goto")) {
                    // Extraer las etiquetas de las instrucciones
                    String etiquetaCondicional = extraerEtiquetaDeSalto(instruccion);
                    String etiquetaIncondicional = extraerEtiquetaDeSalto(siguienteInstruccion);
                    // Verificar si la etiqueta condicional sigue inmediatamente después
                    if (i + 2 < codigoOptimizado.size() && 
                        codigoOptimizado.get(i + 2).equals(etiquetaCondicional + ":     skip")) {

                        // Invertir el condicional
                        String condicionalInvertido = invertirCondicional(instruccion, etiquetaIncondicional);

                        // Agregar el condicional optimizado
                        codigoOptimizadoTemp.add(condicionalInvertido);

                        // Agregar el bloque que venía después del condicional original
                        codigoOptimizadoTemp.add(codigoOptimizado.get(i + 2)); // Etiqueta condicional

                        // Marcar como optimizado
                        contadorOptimizaciones++;

                        // Saltar las líneas optimizadas (condicional + incondicional + etiqueta)
                        i += 2;
                        continue;
                    }
                }
            }

            // Si no se optimizó, agregar la instrucción sin cambios
            codigoOptimizadoTemp.add(instruccion);
        }

        // Actualizar el código optimizado si se realizaron cambios
        //System.out.println("Optimizacion de Reduccion de ramificaciones aplicada.optimizaciones:"+(contadorOptimizaciones-contador1));
        codigoOptimizado = codigoOptimizadoTemp;
    }
    
    // Optimización 12: Propagación de copias
    private void propagacionDeCopias() {
        //System.out.println("Aplicando optimizacion: Propagacion de copias...");
        int contador1= contadorOptimizaciones;
        List<String> codigoNuevo = new ArrayList<>();
        
        for (int i = 0; i < codigoOptimizado.size(); i++) {
            String linea = codigoOptimizado.get(i);
            
            // Detectar asignaciones simples (temporales)
            if (esAsignacionTemporal(linea)) {
                String[] partes = linea.split("=");
                String temporal = partes[0].trim(); // tX
                String valor = partes[1].trim();   // valor

                // Verificar si el temporal solo se usa en la siguiente línea
                if (i + 1 < codigoOptimizado.size() && codigoOptimizado.get(i + 1).contains(temporal)) {
                    String lineaSiguiente = codigoOptimizado.get(i + 1);
                    // Validar que no haya más de tres direcciones
                    if (contarOperadores(lineaSiguiente) >= 1 && contarOperadores(linea) >= 1) {
                        // Agregar la línea al nuevo código si no fue optimizada
                        //System.out.println("valor: "+ valor);
                        codigoNuevo.add(linea);
                        continue;
                    }   
                    // Reemplazar el temporal con su valor en la siguiente línea
                    lineaSiguiente = lineaSiguiente.replace(temporal, valor);
                    codigoNuevo.add(lineaSiguiente);
                    i++; // Saltar la línea siguiente porque ya se procesó
                    contadorOptimizaciones++; // Incrementar contador
                    continue; // No agregar la línea actual (se eliminó)
                }
            }

            // Agregar la línea al nuevo código si no fue optimizada
            codigoNuevo.add(linea);
        }

        // Actualizar el código optimizado
        codigoOptimizado = codigoNuevo;
        //System.out.println("Optimizacion de propagacion de copias completada.optimizaciones:"+(contadorOptimizaciones-contador1));
    }
    
    private void eliminarEtiquetasRedundantes() {
        //System.out.println("Aplicando optimizacion: eliminar etiquetas redundantes...");
        int contador1= contadorOptimizaciones;
        Set<String> etiquetasReferenciadas = new HashSet<>();

        // Paso 1: Detectar etiquetas referenciadas
        for (String instruccion : codigoOptimizado) {
            String etiqueta = extraerEtiquetaDeSalto(instruccion);

            if (etiqueta != null) {
                etiquetasReferenciadas.add(etiqueta);
            }
        }
        /*
        for(String x : etiquetasReferenciadas){
            System.out.println("etiqueta referenciada:" + x);
        }
        */

        // Paso 2: Filtrar código eliminando etiquetas no referenciadas
        List<String> codigoFiltrado = new ArrayList<>();
        for (String instruccion : codigoOptimizado) {
            String etiquetaActual = lineaActualtieneEtiqueta(instruccion);

            // Conservar la instrucción si:
            // 1. No es una etiqueta
            // 2. Es una etiqueta referenciada
            if (etiquetaActual == null || etiquetasReferenciadas.contains(etiquetaActual)) {
                codigoFiltrado.add(instruccion);
            }else {
                //System.out.println("linea eliminada :" + instruccion);
                contadorOptimizaciones++;
            }
        }
        codigoOptimizado = codigoFiltrado;
        //System.out.println("Optimizacion de etiquetas redundantes completada.optimizaciones:"+(contadorOptimizaciones-contador1));
    }
    
    // Contar el número de operadores en una línea
    private int contarOperadores(String linea) {
        // Consideramos los operadores aritméticos básicos: +, -, *, /
        String[] operadores = {"\\+", "-", "\\*", "/"};
        int count = 0;
        for (String op : operadores) {
            count += linea.split(op, -1).length - 1;
        }
        return count;
    }
    
    // Verificar si una línea es una asignación temporal simple
    private boolean esAsignacionTemporal(String linea) {
        // Una asignación temporal simple tiene la forma "tX = valor"
        return linea.matches("        t\\d+ = .*");
    }
    
    // Método auxiliar: Verificar si una instrucción es condicional
    private boolean esCondicional(String instruccion) {
        return instruccion.contains("if") && instruccion.contains("goto");
    }

    // Método auxiliar: Invertir el condicional de una instrucción
    private String invertirCondicional(String instruccion, String nuevaEtiqueta) {
        // Separar la instrucción condicional
        String[] partes = instruccion.split("goto");
        String condicion = "	"+partes[0].trim(); // "if y == t3"
        String etiquetaOriginal = partes[1].trim(); // Etiqueta del condicional original

        // Invertir el operador de comparación
        String condicionInvertida = invertirCondicion(condicion);

        // Construir la nueva instrucción condicional
        return condicionInvertida + " goto " + nuevaEtiqueta;
    }

    // Método auxiliar: Invertir una condición
    private String invertirCondicion(String condicion) {
        // Manejar condiciones lógicas con && y ||
        if (condicion.contains("&&")) {
            String[] partes = condicion.split("&&");
            String parteIzquierda = partes[0].trim();
            String[] partes2 = parteIzquierda.split("if");
            String parteIzquierda2 = partes2[1].trim();
            
            String parteDerecha = partes[1].trim();
            return "	if !" + parteIzquierda2 + " || !" + parteDerecha;
        } else if (condicion.contains("||")) {
            String[] partes = condicion.split("\\|\\|");
            String parteIzquierda = partes[0].trim();
            String[] partes2 = parteIzquierda.split("if");
            String parteIzquierda2 = partes2[1].trim();
            
            String parteDerecha = partes[1].trim();
            return "	if !" + parteIzquierda2 + " && !" + parteDerecha;
        }

        // Manejar condiciones aritméticas normales
        if (condicion.contains("==")) {
            return condicion.replace("==", "!=");
        } else if (condicion.contains("!=")) {
            return condicion.replace("!=", "==");
        } else if (condicion.contains("<")) {
            return condicion.replace("<", ">=");
        } else if (condicion.contains(">")) {
            return condicion.replace(">", "<=");
        } else if (condicion.contains("<=")) {
            return condicion.replace("<=", ">");
        } else if (condicion.contains(">=")) {
            return condicion.replace(">=", "<");
        } else if (condicion.contains("if !")) {
            String variableSinNegacion = condicion.replace("	if !", "");
            return "	if " + variableSinNegacion + " == 1";
    }

        // Retornar la condición sin cambios si no se puede invertir
        return condicion;
    }

    // Obtener el código optimizado
    public List<String> getCodigoOptimizado() {
        return codigoOptimizado;
    }
    
    // Obtener el contador de optimizaciones realizadas
    public int getContadorOptimizaciones() {
        return contadorOptimizaciones;
    }
    
    // Buscar la línea donde se encuentra una etiqueta específica
    private int buscarLineaPorEtiqueta(String etiqueta) {
        for (int i = 0; i < codigoOptimizado.size(); i++) {
            if (codigoOptimizado.get(i).equals(etiqueta + ": skip")) {
                return i;
            }
        }
        return -1; // Etiqueta no encontrada
    }

    // Método auxiliar: Extraer etiqueta de una instrucción de salto
    private String extraerEtiquetaDeSalto(String instruccion) {
        if (instruccion.contains("goto")) {
            String[] partes = instruccion.split("goto");
            return partes[1].trim();
        }
        return null;
    }
    
    // devuelve el nombre de la etiqueta(si hay)de la linea actual
    private String lineaActualtieneEtiqueta(String linea) {
        if (linea.endsWith(":     skip")) {
            String[] partes = linea.split(":");
            //System.out.println("parte 1:" + partes[0]);
            return partes[0];
        }
        return null;
    }
    
    //devuelve la linea en que la etiqueta fue referenciada
    private int buscarLineaReferenciaDe(String etiqueta) {
        for (int i = 0; i < codigoOptimizado.size(); i++) {
            String instruccion = codigoOptimizado.get(i);
            if ((instruccion.contains("goto " + etiqueta) || 
                 instruccion.contains("if ") && instruccion.contains("goto " + etiqueta))) {
                return i;
            }
        }
        return -1;
    }
    
    // Verificar si una línea es una operación constante
    private boolean esOperacionConstante(String linea) {
        // Detectar operaciones de la forma "tX = CONST op CONST" (ejemplo: t1 = 2 + 3)
        return linea.matches("        t\\d+ = \\d+ (\\+|\\-|\\*|/) \\d+");
    }
    
    // Evaluar una operación constante
    private String evaluarOperacion(String operacion) {
        try {
            String[] partes = operacion.split(" ");
            if (partes.length == 3) {
                int op1 = Integer.parseInt(partes[0]); // Primer operando
                System.out.println("op1:" + op1);
                String operador = partes[1];          // Operador (+, -, *, /)
                System.out.println("operador:" + operador);
                int op2 = Integer.parseInt(partes[2]); // Segundo operando
                System.out.println("op2:" + op2);

                // Evaluar la operación
                switch (operador) {
                    case "+": return String.valueOf(op1 + op2);
                    case "-": return String.valueOf(op1 - op2);
                    case "*": return String.valueOf(op1 * op2);
                    case "/": 
                        if (op2 != 0) return String.valueOf(op1 / op2);
                        else return null; // División por 0 no válida
                    default: return null;
                }
            }
        } catch (NumberFormatException e) {
            // No es una operación válida
            return null;
        }
        return null;
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
            for (String line : codigoOptimizado) {
                writer.write(line + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            System.err.println("Error al escribir en el archivo: " + ex.getMessage());
        }
    }
}
