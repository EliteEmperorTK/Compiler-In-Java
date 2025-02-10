//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class True extends Expression {
  public True(Location pos) {
    super(pos);
  }
  public String accept(Visitor v) {
    return (v.visitTrue(this));
  }

  public Object accept(ObjectVisitor v) {
    return v.visitTrue(this);
  }
}
