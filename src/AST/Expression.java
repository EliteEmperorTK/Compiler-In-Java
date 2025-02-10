//OK
package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Expression extends ASTNode {
    public Expression(Location pos) {
        super(pos);
    }
    public abstract String accept(Visitor v);

    public abstract Object accept(ObjectVisitor v);
}
