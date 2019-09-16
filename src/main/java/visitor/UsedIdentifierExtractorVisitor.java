package visitor;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import model.ASTIdentifierNode;
import model.IdentifierKind;

public class UsedIdentifierExtractorVisitor extends DeclaredIdentifierExtractorVisitor {

    public UsedIdentifierExtractorVisitor(CompilationUnit compilationUnit, TypeSolver typeSolver) {
        super(compilationUnit, typeSolver);
    }

    @Override
    public Void visit(AssignExpr ASTNode, ASTIdentifierNode identifierNode) {
        ASTNode.getValue().accept(this, identifierNode);

        ASTIdentifierNode targetNode =
                new ASTIdentifierNode(
                    ASTNode.getTarget().asNameExpr().getName().asString(),
                    IdentifierKind.USE,
                    ASTNode.getBegin().get().line,
                    ASTNode.getEnd().get().line
                );

        identifierNode.addChild(targetNode);

        return null;
    }

    @Override
    public Void visit(SimpleName ASTNode, ASTIdentifierNode identifierNode) {
        ASTIdentifierNode nameNode =
                new ASTIdentifierNode(
                        ASTNode.getIdentifier(),
                        IdentifierKind.USE,
                        ASTNode.getBegin().get().line,
                        ASTNode.getEnd().get().line
                );

        identifierNode.addChild(nameNode);

        return null;
    }

    @Override
    public Void visit(Name ASTNode, ASTIdentifierNode identifierNode) {
        ASTIdentifierNode nameNode =
                new ASTIdentifierNode(
                        ASTNode.getIdentifier(),
                        IdentifierKind.USE,
                        ASTNode.getBegin().get().line,
                        ASTNode.getEnd().get().line
                );

        identifierNode.addChild(nameNode);

        if (ASTNode.getQualifier().isPresent()) {
            ASTNode.getQualifier().get().accept(this, identifierNode);
        }

        return null;
    }

    @Override
    public Void visit(MethodReferenceExpr ASTNode, ASTIdentifierNode identifierNode) {
        ASTNode.getScope().accept(this, identifierNode);

        ASTIdentifierNode referenceNode =
                new ASTIdentifierNode(
                        ASTNode.getIdentifier(),
                        IdentifierKind.USE,
                        ASTNode.getBegin().get().line,
                        ASTNode.getEnd().get().line
                );

        identifierNode.addChild(referenceNode);

        return null;
    }

    @Override
    public Void visit(MarkerAnnotationExpr ASTNode, ASTIdentifierNode identifierNode) {
        return null;
    }

    @Override
    public Void visit(NormalAnnotationExpr ASTNode, ASTIdentifierNode identifierNode) {
        return null;
    }

    @Override
    public Void visit(SingleMemberAnnotationExpr ASTNode, ASTIdentifierNode identifierNode) {
        return null;
    }
}
