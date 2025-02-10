//OK
package AST;

import java.util.List;

import AST.Visitor.ObjectVisitor;

import java.util.ArrayList;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class ExpressionList extends ASTNode{
   public List<Expression> listaExpr;

   public ExpressionList(Location pos) {
      super(pos);
      listaExpr = new ArrayList<>();
   }

   public void add(Expression expr) {
      listaExpr.add(expr);
      if (expr != null) expr.setParent(this);
   }

   public Expression get(int idx)  { 
      return listaExpr.get(idx); 
   }

   public int size() { 
      return listaExpr.size(); 
   }
}