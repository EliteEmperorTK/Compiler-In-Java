//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class TupleType extends Type {
    private String firstType;
    private String secondType;

    public TupleType(String firstType, String secondType, Location pos) {
        super(pos);
        this.firstType = firstType;
        this.secondType = secondType;
    }

    public String getFirstType() {
        return firstType;
    }

    public String getSecondType() {
        return secondType;
    }

    
    //@Override
    public void accept(Visitor v) { 
        v.visitTupleType(this);
    }
    

    //@Override
    public Object accept(ObjectVisitor v) {
        return v.visitTupleType(this);
    }

    //@Override
    public String toString() {
        return "Tuple(" + firstType + ", " + secondType + ")";
    }

    //@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TupleType)) return false;
        TupleType other = (TupleType) obj;
        return firstType.equals(other.firstType) && secondType.equals(other.secondType);
    }
}

