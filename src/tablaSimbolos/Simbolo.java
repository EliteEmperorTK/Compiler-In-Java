package tablaSimbolos;

import AST.*;

public abstract class Simbolo
{
    protected String tipo;
    protected String nombre;
	
    public Simbolo(String n, String t) 
    {
    	this.tipo = t;
    	this.nombre = n;
    }

    public String getTipo()
    {
        return tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public abstract String toString();
    
    public abstract Simbolo copy();
    
    public abstract boolean equals(Simbolo s);
}