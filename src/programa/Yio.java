package programa;

import scanner.*;
import parser.*;
import AST.*;
import AST.Visitor.*;
import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java.io.*;

public class Yio {

    public static void main(String[] args) {

        // Archivo de entrada configurado directamente
        //String codigoFuente = "src\\programa\\prueba1.yio";
        //String codigoFuente = "src\\programa\\prueba2.yio";
        String codigoFuente = "src\\programa\\prueba3.yio";
        //String codigoFuente = "src\\programa\\prueba4.yio";
        //String codigoFuente = "src\\programa\\prueba5.yio";
        //String codigoFuente = "src\\programa\\prueba6.yio";
        
        String archivoErrores = "00Errores";
        String archivoTokens = "01Tokens";
        String archivoTablaSimbolos = "02TablaDeSimbolos";
        String archivoCodigo3DireccionesSinOptimizar = "03Codigo3Direcciones";
        String archivoCodigoEnsambladorSinOptimizar = "04CodigoEnsamblador";
        String archivoCodigo3DireccionesOptimizado = "05Codigo3DireccionesOptimizado";
        String archivoCodigoEnsambladorOptimizado = "06CodigoEnsambladorOptimizado";

        GestorErrores gestorErrores = new GestorErrores(archivoErrores);
        Program program = null;
        TablaSimbolosVisitor visitorTabla = null;
        CodigoTresDireccionesVisitor visitorC3DSinOpt = null;
        OptimizacionesC3D optimizador = null;
        
        ///////////////////////// 
        // Análisis Léxico //
        /////////////////////////
        try {
            System.out.println("\nIniciando analisis lexico...");
            ComplexSymbolFactory sf = new ComplexSymbolFactory();
            Reader in = new BufferedReader(new FileReader(codigoFuente));
            Scanner scanner = new Scanner(in, sf, gestorErrores); // Pasar el gestor de errores al scanner
            scanner.imprimirTokens(archivoTokens); //genera el fichero con los tokens leidos
            
            if (gestorErrores.hayErrores()) {
                System.err.println("Se encontaron errores en el analisis lexico, escribiendolos al fichero"); 
                gestorErrores.imprimirErrores("-------- ANALISIS LEXICO --------");
                gestorErrores.reset();
            }
            System.out.println("Analisis lexico terminado");   

        } catch (Exception e) {
            System.err.println("Error durante el analisis lexico: " + e.getMessage());
            e.printStackTrace();
        }
            
        ///////////////////////// 
        // Análisis sintáctico //
        /////////////////////////
        try {
            System.out.println("\nIniciando analisis sintactico...");
            ComplexSymbolFactory sf = new ComplexSymbolFactory();
            Reader in = new BufferedReader(new FileReader(codigoFuente));
            Scanner scanner = new Scanner(in, sf, gestorErrores); // Pasar el gestor de errores
            Parser parser = new Parser(scanner, sf, gestorErrores); // Pasar el gestor de errores
            Symbol root = parser.parse();
            //comprobar si el analisis sintactico pudo prodcuir un nodo Program valido
            if (root == null || !(root.value instanceof Program)) {
                System.err.println("Error: el analisis sintactico no produjo un programa valido.");
                gestorErrores.imprimirErrores("-------- ANALISIS SINTACTICO --------");
                return; // Detener ejecución
            }
            program = (Program) root.value;
            
            //puede que este if sea redundante porque todos los errores sintacticos son criticos(que no generan un programa valido)
            if (gestorErrores.hayErrores()) {
                gestorErrores.imprimirErrores("-------- ANALISIS SINTACTICO --------");
                gestorErrores.reset();
            }
            // Imprimimos por pantalla el código fuente
            System.out.println("Analisis sintactico terminado");
            //program.accept(new ImprimirVisitor()); //imprime el codigo desde los nodos(DEBUGGING)

        } catch (Exception e) {
            System.err.println("Error durante el analisis sintactico: " + e.getMessage());
            e.printStackTrace();
        }

        ///////////////////////// 
        // Tabla de Simbolos   //
        /////////////////////////
        try {
                System.out.println("\nGenerando Tabla de Simbolos...");
                visitorTabla = new TablaSimbolosVisitor(gestorErrores);
                program.accept(visitorTabla);
                visitorTabla.imprimirTablaSimbolos(archivoTablaSimbolos);//crea fichero
                
                if (gestorErrores.hayErrores()) {
                    System.err.println("Se encontaron errores en la tabla de simbolos, escribiendolos al fichero"); 
                    gestorErrores.imprimirErrores("--------TABLA DE SIMBOLOS------");
                    gestorErrores.reset(); // Limpiar errores para la siguiente etapa
                }
                
                System.out.println("Tabla de Simbolos terminada");
        } catch (Exception e) {
            System.err.println("Error durante la generacion de la tabla de simbolos: " + e.getMessage());
            e.printStackTrace();
        }     

        
        ///////////////////////// 
        // Análisis Semántico  //
        /////////////////////////
        try {
            if (program != null) {
                System.out.println("\nRealizando Analisis Semantico...");
                AnalisisSemanticoVisitor visitorSemantico = new AnalisisSemanticoVisitor(gestorErrores);
                visitorSemantico.setTablaSimbolos(visitorTabla.getSymtab());
                program.accept(visitorSemantico);
                
                if (gestorErrores.hayErrores()) {
                    System.err.println("Se encontaron errores en el analisis semantico, escribiendolos al fichero"); 
                    gestorErrores.imprimirErrores("--------ANALISIS SEMANTICO------");
                    gestorErrores.reset(); // Limpiar errores para la siguiente etapa
                }
                System.out.println("Analisis Semantico terminado");
            }
        } catch (Exception e) {
            System.err.println("Error durante el analisis semantico: " + e.getMessage());
            e.printStackTrace();
        } 

        /////////////////////////////////////
        // Codigo Intermedio Sin Optimizar//
        /////////////////////////////////////
        try {
            if (program != null) {
                System.out.println("\nGenerando Codigo Intermedio Sin Optimizar...");
                visitorC3DSinOpt = new CodigoTresDireccionesVisitor();
                program.accept(visitorC3DSinOpt);
                visitorC3DSinOpt.imprimircod3Dir(archivoCodigo3DireccionesSinOptimizar);
                System.out.println("Codigo Intermedio Sin Optimizar terminado");
            }
        } catch (Exception e) {
            System.err.println("Error durante la generacion de código intermedio: " + e.getMessage());
            e.printStackTrace();
        }
        
        //////////////////////////////////////
        // Codigo Ensambaldor Sin Optimizar //
        //////////////////////////////////////
        try {
            System.out.println("\nGenerando Codigo Ensamblador Sin Optimizar...");
            TraducirEnsamblador ensSinOpt = new TraducirEnsamblador(visitorC3DSinOpt.getCod3Dir()); //Lo generamos usando el código de 3 direcciones
            ensSinOpt.generarEnsamblador(archivoCodigoEnsambladorSinOptimizar);
            System.out.println("Codigo Ensamblador Sin Optimizar Terminado");
        } catch (Exception e) {
            // Manejo de errores inesperados
            System.err.println("Error durante la generacion de Codigo Ensamblador Sin Optimizar" + e.getMessage());
            e.printStackTrace();
        }


        /////////////////////////////////////
        // Codigo Intermedio Optimizado    //
        /////////////////////////////////////
        try {
            if (visitorC3DSinOpt != null) {
                System.out.println("\nGenerando Codigo Intermedio Optimizado...");
                optimizador = new OptimizacionesC3D(visitorC3DSinOpt.getCod3Dir());
                optimizador.optimizar(); // Ejecutar todas las optimizaciones
                optimizador.imprimircod3Dir(archivoCodigo3DireccionesOptimizado); // por si se quiere el C3D optimizado
            }
        } catch (Exception e) {
            System.err.println("Error durante la generacion de Codigo Intermedio Optimizado: " + e.getMessage());
            e.printStackTrace();
        }

        //////////////////////////////////////
        // Codigo Ensambaldor Optimizado    //
        //////////////////////////////////////
        try {
            System.out.println("\nGenerando Codigo Ensamblador Optimizado...");
            TraducirEnsamblador ensOptimizado = new TraducirEnsamblador(optimizador.getCodigoOptimizado()); //Lo generamos usando el código de 3 direcciones
            ensOptimizado.generarEnsamblador(archivoCodigoEnsambladorOptimizado);
            System.out.println("Codigo Ensamblador Optimizado Terminado");
        } catch (Exception e) {
            // Manejo de errores inesperados
            System.err.println("Error durante la generacion de Codigo Ensamblador Optimizado" + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n----------------------");
        System.out.println("Analisis completado.");
    }
}
