package tablaSimbolos;

import AST.*;
import java.util.ArrayList;

public class SimboloTupla extends Simbolo {
    private String primerTipo;
    private String segundoTipo;

    public SimboloTupla(String name, String primerTipo, String segundoTipo) {
        super(name, "tupla");
        this.primerTipo = primerTipo;
        this.segundoTipo = segundoTipo;
    }

    public String getprimerTipo() {
        return primerTipo;
    }

    public String getsegundoTipo() {
        return segundoTipo;
    }

    @Override
    public String toString() {
        return "Tupla " +  this.nombre + "(" + primerTipo + ", " + segundoTipo + ")";
    }

    @Override
    public Simbolo copy() {
        return new SimboloTupla(this.nombre, this.primerTipo, this.segundoTipo);
    }

    @Override
    public boolean equals(Simbolo s) {
        if (this == s) {
            return true;
        }
        if (s == null || !(s instanceof SimboloTupla)) {
            return false;
        }
        SimboloTupla other = (SimboloTupla) s;
        return this.nombre.equals(other.nombre)
                && this.primerTipo.equals(other.primerTipo)
                && this.segundoTipo.equals(other.segundoTipo);
    }
}
