//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class LessThan extends Condition {
  public Expression expr1,expr2;
  
  public LessThan(Expression e1, Expression e2, Location pos) {
    super(pos);
    expr1=e1;
    expr2=e2;
    if (expr1 != null) expr1.setParent(this);
    if (expr2 != null) expr2.setParent(this);
  }

  public String accept(Visitor v) {
    return (v.visitLessThan(this));
  }

  public Object accept(ObjectVisitor v) {
    return v.visitLessThan(this);
  }
}
