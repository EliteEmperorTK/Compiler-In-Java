//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Condition extends ASTNode {
    
    public Condition(Location pos) {
        super(pos);
    }
    public abstract String accept(Visitor v);

    public abstract Object accept(ObjectVisitor v);
}
