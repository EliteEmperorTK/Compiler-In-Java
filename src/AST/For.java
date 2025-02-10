//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class For extends Statement {
  public AssignStatement assState;
  public Identifier ident;
  public Expression expr;
  public Condition cond;
  public StatementList stateList;

  public For(AssignStatement asSt, Condition c, Identifier id, Expression ex, StatementList st, Location pos) {
    super(pos);
    assState = asSt; cond=c; ident=id;
    expr=ex; stateList=st; 
    if (assState != null) assState.setParent(this);
    if (cond != null) cond.setParent(this);
    if (ident != null) ident.setParent(this);
    if (expr != null) expr.setParent(this);
    if (stateList != null) stateList.setParent(this);
  }

  public void accept(Visitor v) {
    v.visitFor(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visitFor(this);
  }
}

