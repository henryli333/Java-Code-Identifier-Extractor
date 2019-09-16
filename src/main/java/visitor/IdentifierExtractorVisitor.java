package visitor;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import model.ASTIdentifierNode;
import model.IdentifierKind;

public class IdentifierExtractorVisitor extends GenericVisitorAdapter<Void, ASTIdentifierNode> {

    public static final String DEFAULT_PACKAGE_NAME = "<default package>";

    protected final CompilationUnit _compilationUnit;
    protected final TypeSolver _typeSolver;

    public IdentifierExtractorVisitor(CompilationUnit compilationUnit, TypeSolver typeSolver) {
        _compilationUnit = compilationUnit;
        _typeSolver = typeSolver;
    }

    @Override
    public Void visit(CompilationUnit ASTNode, ASTIdentifierNode identifierNode) {

        ASTIdentifierNode compilationUnitNode =
                new ASTIdentifierNode(
                        ASTNode.getPackageDeclaration().isPresent() ? ASTNode.getPackageDeclaration().get().getName().asString() : DEFAULT_PACKAGE_NAME,
                        IdentifierKind.PACKAGE,
                        ASTNode.getBegin().get().line,
                        ASTNode.getEnd().get().line
                );

        identifierNode.addChild(compilationUnitNode);

        ASTNode.getTypes().accept(this, compilationUnitNode);

        return null;
    }

    @Override
    public Void visit(ClassOrInterfaceDeclaration ASTNode, ASTIdentifierNode identifierNode) {

        ASTIdentifierNode classNode =
                new ASTIdentifierNode(
                        ASTNode.getNameAsString(),
                        IdentifierKind.CLASS,
                        ASTNode.getBegin().get().line,
                        ASTNode.getEnd().get().line
                );

        identifierNode.addChild(classNode);

        ASTNode.getMembers().accept(this, classNode);

        return null;
    }

    @Override
    public Void visit(EnumDeclaration ASTNode, ASTIdentifierNode identifierNode) {
        ASTIdentifierNode classNode =
                new ASTIdentifierNode(
                        ASTNode.getNameAsString(),
                        IdentifierKind.CLASS,
                        ASTNode.getBegin().get().line,
                        ASTNode.getEnd().get().line
                );

        identifierNode.addChild(classNode);

        ASTNode.getEntries().accept(this, classNode);
        ASTNode.getMembers().accept(this, classNode);

        return null;
    }

    @Override
    public Void visit(EnumConstantDeclaration ASTNode, ASTIdentifierNode identifierNode) {
        ASTIdentifierNode variableNode =
                new ASTIdentifierNode(
                        ASTNode.getNameAsString(),
                        IdentifierKind.VARIABLE,
                        ASTNode.getBegin().get().line,
                        ASTNode.getEnd().get().line,
                        identifierNode.Name
                );

        identifierNode.addChild(variableNode);

        return null;
    }

    @Override
    public Void visit(MethodDeclaration ASTNode, ASTIdentifierNode identifierNode) {

        ASTIdentifierNode methodNode =
                new ASTIdentifierNode(
                        ASTNode.getNameAsString(),
                        IdentifierKind.METHOD,
                        ASTNode.getBegin().get().line,
                        ASTNode.getEnd().get().line,
                        ASTNode.getTypeAsString()
                );

        identifierNode.addChild(methodNode);

        ASTNode.getParameters().accept(this, methodNode);

        if (ASTNode.getBody().isPresent()) {
            ASTNode.getBody().get().accept(this, methodNode);
        }

        return null;
    }

    @Override
    public Void visit(ConstructorDeclaration ASTNode, ASTIdentifierNode identifierNode) {
        ASTIdentifierNode methodNode =
                new ASTIdentifierNode(
                        "`constructor`",
                        IdentifierKind.CONSTRUCTOR,
                        ASTNode.getBegin().get().line,
                        ASTNode.getEnd().get().line,
                        ASTNode.getNameAsString()
                );

        identifierNode.addChild(methodNode);

        ASTNode.getParameters().accept(this, methodNode);
        ASTNode.getBody().accept(this, methodNode);

        return null;
    }

    @Override
    public Void visit(VariableDeclarator ASTNode, ASTIdentifierNode identifierNode) {

        if (ASTNode.getInitializer().isPresent()) {
            ASTNode.getInitializer().get().accept(this, identifierNode);
        }

        ASTIdentifierNode variableNode =
                new ASTIdentifierNode(
                        ASTNode.getNameAsString(),
                        identifierNode.Kind == IdentifierKind.CLASS ? IdentifierKind.FIELD : IdentifierKind.VARIABLE,
                        ASTNode.getBegin().get().line,
                        ASTNode.getEnd().get().line,
                        ASTNode.getTypeAsString()
                );

        identifierNode.addChild(variableNode);

        return null;
    }

    @Override
    public Void visit(Parameter ASTNode, ASTIdentifierNode identifierNode) {
        ASTIdentifierNode variableNode =
                new ASTIdentifierNode(
                        ASTNode.getNameAsString(),
                        IdentifierKind.PARAMETER,
                        ASTNode.getBegin().get().line,
                        ASTNode.getEnd().get().line,
                        ASTNode.getTypeAsString()
                );

        identifierNode.addChild(variableNode);

        return null;
    }

    @Override
    public Void visit(LambdaExpr ASTNode, ASTIdentifierNode identifierNode) {
        ASTNode.getParameters().accept(new NestedParameterVisitor(this), identifierNode);

        ASTNode.getBody().accept(this, identifierNode);
        if (ASTNode.getExpressionBody().isPresent()) {
            ASTNode.getExpressionBody().get().accept(this, identifierNode);
        }

        return null;
    }

    @Override
    public Void visit(CatchClause ASTNode, ASTIdentifierNode identifierNode) {
        ASTNode.getParameter().accept(new NestedParameterVisitor(this), identifierNode);
        ASTNode.getBody().accept(this, identifierNode);
        return null;
    }

    @Override
    public Void visit(NameExpr ASTNode, ASTIdentifierNode identifierNode) {

        ResolvedValueDeclaration resolvedDeclaration;

        try {
            resolvedDeclaration = JavaParserFacade.get(_typeSolver).solve(ASTNode).getCorrespondingDeclaration();
        }
        catch (Exception e) {
            return null;
        }

        if (resolvedDeclaration == null) {
            return null;
        }

        if (resolvedDeclaration.isField()) {
            String name = resolvedDeclaration.getName();
            String type = null;

            try {
                type = resolvedDeclaration.getType().describe();
            }
            catch (UnsolvedSymbolException use) {
            }

            ASTIdentifierNode fieldNode =
                    new ASTIdentifierNode(
                            name,
                            IdentifierKind.FIELD,
                            ASTNode.getBegin().get().line,
                            ASTNode.getEnd().get().line,
                            type
                    );

            identifierNode.addChild(fieldNode);
        }
        else {
            ASTNode.getName().accept(this, identifierNode);
        }

        return null;
    }

}
