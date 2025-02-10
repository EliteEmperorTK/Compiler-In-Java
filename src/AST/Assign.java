//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class Assign extends AssignStatement {
  public Identifier ident;
  public Expression expr;

  public Assign(Identifier id, Expression e, Location pos) {
    super(pos);
    ident=id;
    expr=e; 
    if (ident != null) ident.setParent(this);
    if (expr != null) expr.setParent(this);
  }

  public void accept(Visitor v) {
    v.visitAssign(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visitAssign(this);
  }
}

