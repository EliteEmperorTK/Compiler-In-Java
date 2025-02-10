//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class BooleanType extends Type {
  public BooleanType(Location pos) {
    super(pos);
  }
  public void accept(Visitor v) {
    v.visitBooleanType(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visitBooleanType(this);
  }

  @Override
  public String toString() {
    return "boolean";
  }
}
