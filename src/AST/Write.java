//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class Write extends Statement {
  public Expression expr;

  public Write(Expression e, Location pos) {
    super(pos);
    expr=e; 
    if (expr != null) expr.setParent(this);
  }

  public void accept(Visitor v) {
    v.visitWrite(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visitWrite(this);
  }
}
