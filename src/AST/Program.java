//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class Program extends ASTNode{
    public MainClass main;

    public Program(MainClass m, Location pos) {
        super(pos);
        main=m; 
        if (main != null) main.setParent(this);
      }

    public void accept(Visitor v) {
        v.visitProgram(this);
      }

    public Object accept(ObjectVisitor v) {
      return v.visitProgram(this);
    }
}
