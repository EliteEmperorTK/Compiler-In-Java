//OK
package AST;

import java.util.List;
import java.util.ArrayList;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class StatementList extends ASTNode {
   public List<Statement> listaStatements;

   public StatementList(Location pos) {
      super(pos);
      listaStatements = new ArrayList<Statement>();
   }

   public void add(Statement state) {
      listaStatements.add(state);
      if (state != null) state.setParent(this);
   }

   public Statement get(int i)  { 
      return listaStatements.get(i); 
   }

   public int size() { 
      return listaStatements.size(); 
   }
}
