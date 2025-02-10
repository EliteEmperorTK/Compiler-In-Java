//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class GetTupla extends Expression {
  public Identifier ident;
  public Expression expr;
  
  public GetTupla(Identifier id, Expression ex, Location pos) {
    super(pos);
    ident = id;
    expr = ex;
    if (ident != null) ident.setParent(this);
    if (expr != null) expr.setParent(this);
  }

  public String accept(Visitor v) {
    return (v.visitGetTupla(this));
  }

  public Object accept(ObjectVisitor v) {
    return v.visitGetTupla(this);
  }
}