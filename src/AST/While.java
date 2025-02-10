//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class While extends Statement {
  public Condition cond;
  public StatementList stateList;

  public While(Condition c, StatementList sl, Location pos) {
    super(pos);
    cond=c; stateList=sl; 
    if (cond != null) cond.setParent(this);
    if (stateList != null) stateList.setParent(this);
  }

  public void accept(Visitor v) {
    v.visitWhile(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visitWhile(this);
  }
}

