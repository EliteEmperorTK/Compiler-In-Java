package tablaSimbolos;

import AST.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public class TablaSimbolos {

    private boolean isFunctionScope = false;
    private HashMap<String, Simbolo> tablaVariables, tablaFunciones, tablaTuplas;
    private HashMap<String, TablaSimbolos> hijo;
    private TablaSimbolos padre = null;
    private ASTNode scope = null;

    public TablaSimbolos() {
        tablaTuplas    = new HashMap<String, Simbolo>();
        tablaFunciones = new HashMap<String, Simbolo>();
        tablaVariables = new HashMap<String, Simbolo>();
        hijo = new HashMap<String, TablaSimbolos>();
        padre = null;
        scope = null;
    }

    public TablaSimbolos(TablaSimbolos p, ASTNode n) {
        tablaTuplas    = new HashMap<String, Simbolo>();
        tablaFunciones = new HashMap<String, Simbolo>();
        tablaVariables = new HashMap<String, Simbolo>();
        hijo = new HashMap<String, TablaSimbolos>();
        padre = p;
        scope = n;
    }
    
    public boolean isFunctionScope() {
        return isFunctionScope;
    }

    public void setFunctionScope(boolean isFunctionScope) {
        this.isFunctionScope = isFunctionScope;
    }

    public HashMap<String, Simbolo> getTablaTuplas() {
    	return tablaTuplas;    	
    }

    public HashMap<String, Simbolo> getTablaFunciones() {
    	return tablaFunciones;    	
    }
    
    public HashMap<String, Simbolo> getTablaVariables() {
    	return tablaVariables;    	
    }
    
    public void addSimbolo(Simbolo s) {
    if (s instanceof SimboloFuncion && tablaFunciones.containsKey(s.getNombre())) {
        System.out.println("Error: función duplicada: " + s.getNombre());
    } else if (s instanceof SimboloTupla && tablaTuplas.containsKey(s.getNombre())) {
        System.out.println("Error: tupla duplicada: " + s.getNombre());
    } else if (s instanceof SimboloVar && tablaVariables.containsKey(s.getNombre())) {
        System.out.println("Error: variable duplicada: " + s.getNombre());
    } else {
        if (s instanceof SimboloFuncion) tablaFunciones.put(s.getNombre(), s);
        else if (s instanceof SimboloTupla) tablaTuplas.put(s.getNombre(), s);
        else if (s instanceof SimboloVar)tablaVariables.put(s.getNombre(), s);
    }
}

    //la hice public
    public Simbolo getSimbolo(String i) {
        Simbolo s = tablaVariables.get(i);
        if ( s == null ) s = tablaFunciones.get(i);
        if ( s == null ) s = tablaTuplas.get(i);
        return s;
    }

    /**
    * Busca un símbolo en el ámbito actual y en los padres.
    * Útil para resolver referencias, pero no para verificar duplicados en un único ámbito.
    * 
    * si el ambito es una fucion, no buscar en el padre
    * se pueden volver a declarar variables en funciones
    */
    public Simbolo lookupSymbol(String i) {
        TablaSimbolos st = this;
        while (st != null) {
            Simbolo s = st.getSimbolo(i);
            if (s != null) return s;

            // Si estamos en un ámbito de función, no subir al padre
            if (st.isFunctionScope()) break;

            st = st.getpadre();
        }
        return null;
    }

    public Simbolo lookupSymbol(String name, String type) {
        TablaSimbolos st = this;
        while ( st != null ) {
            Simbolo s = null; 
            if ( type == "SimboloVar" ) 
            	s = st.tablaVariables.get(name);
            else if ( type == "SimboloTupla" )
            	s = st.tablaTuplas.get(name);
            else if ( type == "SimboloFuncion" )
            	s = st.tablaFunciones.get(name);
            if ( s != null ) return s;
            st = st.getpadre();
        }
        return null;
    }

    public TablaSimbolos enterScope(String i, ASTNode n) {
        TablaSimbolos st = this.getHijo(i);
        if ( st == null ) {
            st = new TablaSimbolos(this, n);
            this.addHijo(i, st);
        }
        //System.out.println("Entrando en ámbito: " + i);
        return st;
    }

    public TablaSimbolos findScope(String i) {
        TablaSimbolos st = this.getHijo(i);
        if ( st != null ) {
        	return st;
        }
        return this;
    }

    public TablaSimbolos exitScope() {
        //System.out.println("Saliendo del ámbito actual: " + (this.scope != null ? this.scope.toString() : "global"));
    	return this.getpadre();
    }

    public void addHijo(String i, TablaSimbolos st) {
        hijo.put(i, st);
        st.padre = this;
    }

    public TablaSimbolos getHijo(String i) {
        return hijo.get(i);
    }

    public TablaSimbolos getpadre() {
        return padre;
    }

    public void setScope(ASTNode n) {
    	scope = n;
    }
    
    public ASTNode getScope() {
    	return scope;
    }
    
    public void printTabs(int tabs){
        for ( int t=0; t<tabs*4; t++ ) {
        	System.out.print(" ");
        }
    }

    public void print(int level) {
        // Imprimir funciones
        for (String key : tablaFunciones.keySet()) {
            Simbolo sym = tablaFunciones.get(key);
            printTabs(level);
            System.out.println(sym.toString());
            TablaSimbolos child = hijo.get(key);
            if (child != null) child.print(level + 1);
        }
        
        // Imprimir tuplas
        for (String key : tablaTuplas.keySet()) {
            Simbolo sym = tablaTuplas.get(key);
            printTabs(level);
            System.out.println(sym.toString());
            TablaSimbolos child = hijo.get(key);
            if (child != null) child.print(level + 1);
        }

        // Imprimir variables
        for (String key : tablaVariables.keySet()) {
            Simbolo sym = tablaVariables.get(key);
            printTabs(level);
            System.out.println(sym.toString());
        }
        
        // Imprimir hijos no asociados explícitamente a funciones o tuplas
        for (String key : hijo.keySet()) {
            if (!tablaFunciones.containsKey(key) && !tablaTuplas.containsKey(key)) {
                TablaSimbolos child = hijo.get(key);
                printTabs(level);
                System.out.println("Ambito hijo: " + key);
                if (child != null) child.print(level + 1);
            }
        }
    }

    public void printTabs(BufferedWriter writer, int tabs) throws IOException {
        for (int t = 0; t < tabs * 4; t++) {
            writer.write(" ");
        }
    }

    public void imprimirTablaSimbolos(String nombreArchivo, int level){
        String nombArch = "src/programa/" + nombreArchivo + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombArch))){
            
            // Imprimir variables
            for (String key : tablaVariables.keySet()) {
                Simbolo sym = tablaVariables.get(key);
                printTabs(writer, level);
                writer.write(sym.toString() + "\n");
            }

            // Imprimir tuplas
            for (String key : tablaTuplas.keySet()) {
                Simbolo sym = tablaTuplas.get(key);
                printTabs(writer, level);
                writer.write(sym.toString() + "\n");
                TablaSimbolos child = hijo.get(key);
                if (child != null) child.imprimirTablaSimbolosRecursivo(writer, level + 1);
            }

            // Imprimir funciones
            for (String key : tablaFunciones.keySet()) {
                Simbolo sym = tablaFunciones.get(key);
                printTabs(writer, level);
                writer.write(sym.toString() + "\n");
                TablaSimbolos child = hijo.get(key);
                if (child != null) child.imprimirTablaSimbolosRecursivo(writer, level + 1);
            }

            // Imprimir hijos no asociados explícitamente a funciones o tuplas
            for (String key : hijo.keySet()) {
                if (!tablaFunciones.containsKey(key) && !tablaTuplas.containsKey(key)) {
                    TablaSimbolos child = hijo.get(key);
                    printTabs(writer, level);
                    writer.write("Ambito hijo: " + key + "\n");
                    if (child != null) child.imprimirTablaSimbolosRecursivo(writer, level + 1);
                }
            }
        } catch (IOException ex) {
                System.err.println("Error al escribir en el archivo: " + ex.getMessage());
            }  
    }

// Método auxiliar para la recursividad
private void imprimirTablaSimbolosRecursivo(BufferedWriter writer, int level) throws IOException {
    // Imprimir variables
    for (String key : tablaVariables.keySet()) {
        Simbolo sym = tablaVariables.get(key);
        printTabs(writer, level);
        writer.write(sym.toString() + "\n");
    }

    // Imprimir tuplas
    for (String key : tablaTuplas.keySet()) {
        Simbolo sym = tablaTuplas.get(key);
        printTabs(writer, level);
        writer.write(sym.toString() + "\n");
        TablaSimbolos child = hijo.get(key);
        if (child != null) child.imprimirTablaSimbolosRecursivo(writer, level + 1);
    }

    // Imprimir funciones
    for (String key : tablaFunciones.keySet()) {
        Simbolo sym = tablaFunciones.get(key);
        printTabs(writer, level);
        writer.write(sym.toString() + "\n");
        TablaSimbolos child = hijo.get(key);
        if (child != null) child.imprimirTablaSimbolosRecursivo(writer, level + 1);
    }

    // Imprimir hijos no asociados explícitamente a funciones o tuplas
    for (String key : hijo.keySet()) {
        if (!tablaFunciones.containsKey(key) && !tablaTuplas.containsKey(key)) {
            TablaSimbolos child = hijo.get(key);
            printTabs(writer, level);
            writer.write("Ambito hijo: " + key + "\n");
            if (child != null) child.imprimirTablaSimbolosRecursivo(writer, level + 1);
        }
    }
}
}