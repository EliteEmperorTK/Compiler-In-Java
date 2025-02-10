//OK
package AST;

import AST.Visitor.Visitor;
import AST.Visitor.ObjectVisitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class MainClass extends ASTNode{
  public StatementList stateList;

  public MainClass(StatementList sl, Location pos){
    super(pos);
    stateList = sl;
    if (stateList != null)  stateList.setParent(this);
  }

  public void accept(Visitor v){
    v.visitMainClass(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visitMainClass(this);
  }
}

