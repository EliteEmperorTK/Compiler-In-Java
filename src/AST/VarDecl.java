//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class VarDecl extends AssignStatement {
  public Type ty;
  public Identifier ident;
  public Expression expr;
  
  public VarDecl(Type t, Identifier id,Expression e, Location pos) {
    super(pos);
    ty=t; ident=id; expr=e;
    if (ty != null) ty.setParent(this);
    if (ident != null) ident.setParent(this);
    if (expr != null) expr.setParent(this);
  }

  public void accept(Visitor v) {
    v.visitVarDecl(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visitVarDecl(this);
  }
}
