//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class FunctionDef extends Statement {
  public Type ty;
  public Identifier ident;
  public ParamList paramList;
  public StatementList stateList;
  public Expression expr;


  public FunctionDef(Type t,Identifier id, ParamList pl, 
                    StatementList sl, Expression e, Location pos) {
    super(pos);
    ident=id; paramList=pl;
    stateList=sl; expr=e;
    ty = t;
    if (ty != null) t.setParent(this);
    if (ident != null) ident.setParent(this);
    if (paramList != null) paramList.setParent(this);
    if (stateList != null) stateList.setParent(this);
    if (expr != null) expr.setParent(this);
  }
 
  public void accept(Visitor v) {
    v.visitFunctionDef(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visitFunctionDef(this);
  }
}
