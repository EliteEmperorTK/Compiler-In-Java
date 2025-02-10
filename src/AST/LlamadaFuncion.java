//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class LlamadaFuncion extends Expression {
  public Identifier ident;
  public ExpressionList exprList;


  public LlamadaFuncion(Identifier id, ExpressionList el, Location pos) {
    super(pos);
    ident=id; exprList=el; 
    if (ident != null) ident.setParent(this);
    if (exprList != null) exprList.setParent(this);
  }

  public String accept(Visitor v) {
    return (v.visitLlamadaFuncion(this));
  }

  public Object accept(ObjectVisitor v) {
    return v.visitLlamadaFuncion(this);
  }
}