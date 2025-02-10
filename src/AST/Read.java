//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class Read extends Statement {
  public Identifier ident;

  public Read(Identifier id, Location pos) {
    super(pos);
    ident=id; 
    if (ident != null) ident.setParent(this);
  }

  public void accept(Visitor v) {
    v.visitRead(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visitRead(this);
  }
}
