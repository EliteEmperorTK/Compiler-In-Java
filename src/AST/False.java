//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class False extends Expression {
  public False(Location pos) {
    super(pos);
  }
  public String accept(Visitor v) {
    return (v.visitFalse(this));
  }

  public Object accept(ObjectVisitor v) {
    return v.visitFalse(this);
  }
}
