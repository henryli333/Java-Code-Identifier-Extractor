package visitor;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import model.ASTIdentifierNode;
import model.IdentifierKind;

import java.lang.reflect.Field;

public class IdentifierExtractorVisitor extends GenericVisitorAdapter<Void, ASTIdentifierNode> {

    public static final String DEFAULT_PACKAGE_NAME = "<default package>";

    protected final CompilationUnit _cu;
    protected final TypeSolver _ts;

    public IdentifierExtractorVisitor(CompilationUnit cu, TypeSolver ts) {
        _cu = cu;
        _ts = ts;
    }

    @Override
    public Void visit(CompilationUnit u, ASTIdentifierNode p) {

        ASTIdentifierNode compilationUnitNode =
                new ASTIdentifierNode(
                        u.getPackageDeclaration().isPresent() ? u.getPackageDeclaration().get().getName().asString() : DEFAULT_PACKAGE_NAME,
                        IdentifierKind.PACKAGE,
                        u.getBegin().get().line,
                        u.getEnd().get().line
                );

        p.addChild(compilationUnitNode);

        u.getTypes().accept(this, compilationUnitNode);

        return null;
    }

    @Override
    public Void visit(ClassOrInterfaceDeclaration u, ASTIdentifierNode p) {

        ASTIdentifierNode classNode =
                new ASTIdentifierNode(
                        u.getNameAsString(),
                        IdentifierKind.CLASS,
                        u.getBegin().get().line,
                        u.getEnd().get().line
                );

        p.addChild(classNode);

        u.getMembers().accept(this, classNode);

        return null;
    }


    @Override
    public Void visit(EnumDeclaration u, ASTIdentifierNode p) {
        ASTIdentifierNode classNode =
                new ASTIdentifierNode(
                        u.getNameAsString(),
                        IdentifierKind.CLASS,
                        u.getBegin().get().line,
                        u.getEnd().get().line
                );

        p.addChild(classNode);

        u.getEntries().accept(this, classNode);
        u.getMembers().accept(this, classNode);

        return null;
    }

    @Override
    public Void visit(EnumConstantDeclaration u, ASTIdentifierNode p) {
        ASTIdentifierNode variableNode =
                new ASTIdentifierNode(
                        u.getNameAsString(),
                        IdentifierKind.VARIABLE,
                        u.getBegin().get().line,
                        u.getEnd().get().line,
                        p.Name
                );

        p.addChild(variableNode);

        return null;
    }

    @Override
    public Void visit(MethodDeclaration u, ASTIdentifierNode p) {

        ASTIdentifierNode methodNode =
                new ASTIdentifierNode(
                        u.getNameAsString(),
                        IdentifierKind.METHOD,
                        u.getBegin().get().line,
                        u.getEnd().get().line,
                        u.getTypeAsString()
                );

        p.addChild(methodNode);

        u.getParameters().accept(this, methodNode);

        if (u.getBody().isPresent()) {
            u.getBody().get().accept(this, methodNode);
        }

        return null;
    }

    @Override
    public Void visit(ConstructorDeclaration u, ASTIdentifierNode p) {
        ASTIdentifierNode methodNode =
                new ASTIdentifierNode(
                        "`constructor`",
                        IdentifierKind.CONSTRUCTOR,
                        u.getBegin().get().line,
                        u.getEnd().get().line,
                        u.getNameAsString()
                );

        p.addChild(methodNode);

        u.getParameters().accept(this, methodNode);
        u.getBody().accept(this, methodNode);

        return null;
    }

    @Override
    public Void visit(VariableDeclarator u, ASTIdentifierNode p) {
        ASTIdentifierNode variableNode =
                new ASTIdentifierNode(
                        u.getNameAsString(),
                        p.Kind == IdentifierKind.CLASS ? IdentifierKind.FIELD : IdentifierKind.VARIABLE,
                        u.getBegin().get().line,
                        u.getEnd().get().line,
                        u.getTypeAsString()
                );

        p.addChild(variableNode);

        return null;
    }

    @Override
    public Void visit(Parameter u, ASTIdentifierNode p) {
        ASTIdentifierNode variableNode =
                new ASTIdentifierNode(
                        u.getNameAsString(),
                        IdentifierKind.PARAMETER,
                        u.getBegin().get().line,
                        u.getEnd().get().line,
                        u.getTypeAsString()
                );

        p.addChild(variableNode);

        return null;
    }

    @Override
    public Void visit(LambdaExpr u, ASTIdentifierNode p) {
        u.getParameters().accept(new NestedParameterVisitor(this), p);

        u.getBody().accept(this, p);
        if (u.getExpressionBody().isPresent()) {
            u.getExpressionBody().get().accept(this, p);
        }

        return null;
    }

    @Override
    public Void visit(CatchClause u, ASTIdentifierNode p) {
        u.getParameter().accept(new NestedParameterVisitor(this), p);
        u.getBody().accept(this, p);
        return null;
    }

    @Override
    public Void visit(NameExpr u, ASTIdentifierNode p) {

        ResolvedValueDeclaration rvd;

        try {
            rvd = JavaParserFacade.get(_ts).solve(u).getCorrespondingDeclaration();
        }
        catch (Exception e) {
            rvd = null;
        }

        if (rvd == null) {
            return null;
        }

        if (rvd.isField()) {
            String name = rvd.getName();
            String type = null;

            try {
                type = rvd.getType().describe();
            }
            catch (UnsolvedSymbolException use) {
            }

            ASTIdentifierNode fieldNode =
                    new ASTIdentifierNode(
                            name,
                            IdentifierKind.FIELD,
                            u.getBegin().get().line,
                            u.getEnd().get().line,
                            type
                    );

            p.addChild(fieldNode);
        }

        return null;
    }
}
