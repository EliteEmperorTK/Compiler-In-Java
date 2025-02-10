//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class If extends Statement {
  public Condition cond;
  public StatementList stateList1,stateList2;

  public If(Condition e, StatementList s1, StatementList s2, Location pos) {
    super(pos);
    cond=e;
    stateList1=s1;
    stateList2=s2;

    if (cond != null) cond.setParent(this);
    if (stateList1 != null) stateList1.setParent(this);
    if (stateList2 != null) stateList2.setParent(this);
  }

  public void accept(Visitor v) {
    v.visitIf(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visitIf(this);
  }
}

