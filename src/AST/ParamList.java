//Ok
package AST;

import java.util.List;

import AST.Visitor.ObjectVisitor;

import java.util.ArrayList;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class ParamList extends ASTNode{
   public List<Param> listaParametros;

   public ParamList(Location pos) {
      super(pos);
      listaParametros = new ArrayList<Param>();
   }

   public void add(Param par) {
      listaParametros.add(par);
      if (par != null) par.setParent(this);
   }

   public Param get(int idx)  { 
      return listaParametros.get(idx); 
   }

   public int size() { 
      return listaParametros.size(); 
   }
}
