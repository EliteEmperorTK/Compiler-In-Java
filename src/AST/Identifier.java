//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class Identifier extends ASTNode {
  public String nombre;

  public Identifier(String id, Location pos) { 
    super(pos);
    nombre=id;
  }

  public String accept(Visitor v) {
    return (v.visitIdentifier(this));
  }

  public String toString(){
    return nombre;
  }

  public Object accept(ObjectVisitor v) {
    return v.visitIdentifier(this);
  }
}
