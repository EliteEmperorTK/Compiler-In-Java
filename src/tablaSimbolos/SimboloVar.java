package tablaSimbolos;

import AST.*;

public class SimboloVar extends Simbolo {
	private boolean isConst; // Flag para constantes

	public SimboloVar(String n, String t) {
		super(n, t);
		this.isConst = false;
	}

	public SimboloVar(String n, String t, boolean isConst) {
		super(n, t);
		this.isConst = isConst;
	}

	public boolean isConst() {
		return isConst;
	}
	
	public String toString() {
		return (isConst ? "{CONST}" : "{VAR}") + getTipo() + " " + getNombre();
	}
	
	public Simbolo copy() {
		return new SimboloVar(nombre, tipo);
	}
	
    public boolean equals(Simbolo s) {
    	if ( s == null || !(s instanceof SimboloVar) )
    		return false;
    	
    	SimboloVar vs = (SimboloVar)s;    	
    	return vs.toString().equals(this.toString());
    }
}