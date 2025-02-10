//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class IntegerType extends Type {
    public IntegerType(Location pos) {
      super(pos);
    }
  
    public void accept(Visitor v) {
      v.visitIntegerType(this);
    }

    public Object accept(ObjectVisitor v) {
      return v.visitIntegerType(this);
    }

  
    @Override
    public String toString() {
      return "int";
    }
}
