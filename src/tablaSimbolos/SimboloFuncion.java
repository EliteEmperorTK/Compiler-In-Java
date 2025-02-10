package tablaSimbolos;

import AST.*;
import java.util.ArrayList;

public class SimboloFuncion extends Simbolo
{
    private ArrayList<Simbolo> parametros;

    public SimboloFuncion(String n, String t)
    {
    	super(n, t);
        parametros = new ArrayList<Simbolo>();
    }

    public Simbolo copy() {
    	SimboloFuncion m = new SimboloFuncion(nombre, tipo);
        for(int i=0;i<parametros.size();i++)
        {
        	SimboloVar vs = (SimboloVar)parametros.get(i);
            m.addParametro(vs.copy());
        }
  
        return m;
    }
    
    public void addParametro(Simbolo p) {
        for(int i=0;i<parametros.size();i++) {
        	if ( parametros.get(i) == p || parametros.get(i).getNombre() == p.getNombre() ) {
        		return;
        	}
        }
        parametros.add(p);
    }

    public ArrayList<Simbolo> getparametros() {
        return parametros;
    }
    
    public String toString() {
        String s = "";
        for ( int i=0; i<parametros.size(); i++ ) 
        {
        	s += ((i > 0) ? ", " : "") + parametros.get(i);
        }

        return "{METODO}" + tipo + " " + nombre + "(" + s + ")";
    }
    
    public boolean equals(Simbolo s) {
    	if ( s == null || !(s instanceof SimboloFuncion) ) return false;
    	
        SimboloFuncion ms = (SimboloFuncion)s;    	
    	return ms.toString().equals(this.toString());
    }
}