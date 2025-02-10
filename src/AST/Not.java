//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class Not extends Condition {
  public Expression expr;
  
  public Not(Expression e, Location pos) {
    super(pos);
    expr=e; 
    if (expr != null) expr.setParent(this);
  }

  public String accept(Visitor v) {
    return (v.visitNot(this));
  }

  public Object accept(ObjectVisitor v) {
    return v.visitNot(this);
  }
}
