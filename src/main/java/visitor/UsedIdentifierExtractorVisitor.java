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
    public Void visit(AssignExpr n, ASTIdentifierNode arg) {
        n.getValue().accept(this, arg);

        ASTIdentifierNode targetNode =
                new ASTIdentifierNode(
                    n.getTarget().asNameExpr().getName().asString(),
                    IdentifierKind.USE,
                    n.getBegin().get().line,
                    n.getEnd().get().line
                );

        arg.addChild(targetNode);

        return null;
    }

    @Override
    public Void visit(SimpleName n, ASTIdentifierNode arg) {
        ASTIdentifierNode nameNode =
                new ASTIdentifierNode(
                        n.getIdentifier(),
                        IdentifierKind.USE,
                        n.getBegin().get().line,
                        n.getEnd().get().line
                );

        arg.addChild(nameNode);

        return null;
    }

    @Override
    public Void visit(Name n, ASTIdentifierNode arg) {
        ASTIdentifierNode nameNode =
                new ASTIdentifierNode(
                        n.getIdentifier(),
                        IdentifierKind.USE,
                        n.getBegin().get().line,
                        n.getEnd().get().line
                );

        arg.addChild(nameNode);

        if (n.getQualifier().isPresent()) {
            n.getQualifier().get().accept(this, arg);
        }

        return null;
    }

    @Override
    public Void visit(MethodReferenceExpr n, ASTIdentifierNode arg) {
        n.getScope().accept(this, arg);

        ASTIdentifierNode referenceNode =
                new ASTIdentifierNode(
                        n.getIdentifier(),
                        IdentifierKind.USE,
                        n.getBegin().get().line,
                        n.getEnd().get().line
                );

        arg.addChild(referenceNode);

        return null;
    }

    @Override
    public Void visit(MarkerAnnotationExpr n, ASTIdentifierNode arg) {
        return null;
    }

    @Override
    public Void visit(NormalAnnotationExpr n, ASTIdentifierNode arg) {
        return null;
    }

    @Override
    public Void visit(SingleMemberAnnotationExpr n, ASTIdentifierNode arg) {
        return null;
    }
}
