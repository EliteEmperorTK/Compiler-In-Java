//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class IntLiteral extends Expression {
  public int valor;

  public IntLiteral(int val, Location pos) {
    super(pos);
    valor=val;
  }

  public String accept(Visitor v) {
    return(v.visitIntLiteral(this));
  }

  public Object accept(ObjectVisitor v) {
    return v.visitIntLiteral(this);
  }
}
