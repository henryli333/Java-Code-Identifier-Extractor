package visitor;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.UnknownType;
import model.ASTIdentifierNode;
import model.IdentifierKind;

public class NestedParameterVisitor extends IdentifierExtractorVisitor {

    public NestedParameterVisitor(IdentifierExtractorVisitor visitor) {
        super(visitor._cu, visitor._ts);
    }

    @Override
    public Void visit(Parameter u, ASTIdentifierNode p) {
        ASTIdentifierNode variableNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.VARIABLE, u.getBegin().get().line, u.getEnd().get().line, u.getType() instanceof UnknownType ? "`inferred type`" : u.getTypeAsString());
        p.addChild(variableNode);

        return null;
    }
}
