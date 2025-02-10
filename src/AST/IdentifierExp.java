package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class IdentifierExp extends Expression {
  public String nombre;

  public IdentifierExp(String id, Location pos) { 
    super(pos);
    nombre=id;
  }

  public String accept(Visitor v) {
    return(v.visitIdentifierExp(this));
  }

  public String toString(){
    return nombre;
  }

  public Object accept(ObjectVisitor v) {
    return v.visitIdentifierExp(this);
  }
}
