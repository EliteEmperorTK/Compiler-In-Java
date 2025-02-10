//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class Const extends AssignStatement {
  public Type ty;
  public Identifier ident;
  public Expression expr;
  
  public Const(Type t, Identifier id, Expression e,Location pos) {
    super(pos);
    ty=t;
    ident=id;
    expr=e;
    if (ty != null) ty.setParent(this);
    if (ident != null) ident.setParent(this);
    if (expr != null) expr.setParent(this);
  }

  public void accept(Visitor v) {
    v.visitConst(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visitConst(this);
  }
}
