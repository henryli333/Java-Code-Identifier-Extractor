package visitor;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.UnknownType;
import model.ASTIdentifierNode;
import model.IdentifierKind;

public class NestedParameterVisitorDeclared extends DeclaredIdentifierExtractorVisitor {

    public NestedParameterVisitorDeclared(DeclaredIdentifierExtractorVisitor visitor) {
        super(visitor._compilationUnit, visitor._typeSolver);
    }

    @Override
    public Void visit(Parameter ASTNode, ASTIdentifierNode identifierNode) {
        ASTIdentifierNode variableNode =
                new ASTIdentifierNode(
                        ASTNode.getNameAsString(),
                        IdentifierKind.VARIABLE,
                        ASTNode.getBegin().get().line,
                        ASTNode.getEnd().get().line,
                        ASTNode.getType() instanceof UnknownType ? null : ASTNode.getTypeAsString()
                );

        identifierNode.addChild(variableNode);

        return null;
    }
}
