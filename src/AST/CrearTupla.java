//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class CrearTupla extends Statement {
  public Identifier ident;
  public Expression expr1, expr2;
  
  public CrearTupla(Identifier id, Expression e1, Expression e2, Location pos) {
    super(pos);
    ident = id;
    expr1 = e1;
    expr2 = e2;
    if (ident != null) ident.setParent(this);
    if (expr1 != null) expr1.setParent(this);
    if (expr2 != null) expr2.setParent(this);
  }

  public void accept(Visitor v) {
    v.visitCrearTupla(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visitCrearTupla(this);
  }
}