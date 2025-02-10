//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class Param extends ASTNode{
  public Type ty;
  public Identifier ident;
 
  public Param(Type t, Identifier id, Location pos) {
    super(pos);
    ty=t;
    ident=id;
    if (ty != null) ty.setParent(this);
    if (ident != null) ident.setParent(this);
  }

  public void accept(Visitor v) {
    v.visitParam(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visitParam(this);
  }
}
