package visitor;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import model.ASTIdentifierNode;
import model.IdentifierKind;

public class IdentifierExtractorVisitor extends GenericVisitorAdapter<Void, ASTIdentifierNode> {

    public static final String DEFAULT_PACKAGE_NAME = "<default package>";

    protected final CompilationUnit _cu;

    public IdentifierExtractorVisitor(CompilationUnit cu) {
        _cu = cu;
    }

    @Override
    public Void visit(CompilationUnit u, ASTIdentifierNode p) {

        ASTIdentifierNode compilationUnitNode = new ASTIdentifierNode(u.getPackageDeclaration().isPresent() ? u.getPackageDeclaration().get().getName().asString() : DEFAULT_PACKAGE_NAME, IdentifierKind.PACKAGE, u.getBegin().get().line);
        p.addChild(compilationUnitNode);

        u.getTypes().accept(this, compilationUnitNode);

        return null;
    }

    @Override
    public Void visit(ClassOrInterfaceDeclaration u, ASTIdentifierNode p) {

        ASTIdentifierNode classNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.CLASS, u.getBegin().get().line);
        p.addChild(classNode);

        u.getMembers().accept(this, classNode);

        return null;
    }


    @Override
    public Void visit(EnumDeclaration u, ASTIdentifierNode p) {
        ASTIdentifierNode classNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.CLASS, u.getBegin().get().line);
        p.addChild(classNode);

        u.getEntries().accept(this, classNode);
        u.getMembers().accept(this, classNode);

        return null;
    }

    @Override
    public Void visit(EnumConstantDeclaration u, ASTIdentifierNode p) {
        ASTIdentifierNode variableNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.VARIABLE, u.getBegin().get().line, p.Name);
        p.addChild(variableNode);

        return null;
    }

    @Override
    public Void visit(MethodDeclaration u, ASTIdentifierNode p) {

        ASTIdentifierNode methodNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.METHOD, u.getBegin().get().line, u.getTypeAsString());

        p.addChild(methodNode);

        u.getParameters().accept(this, methodNode);

        if (u.getBody().isPresent()) {
            u.getBody().get().accept(this, methodNode);
        }

        return null;
    }

    @Override
    public Void visit(ConstructorDeclaration u, ASTIdentifierNode p) {
        ASTIdentifierNode methodNode = new ASTIdentifierNode("`constructor`", IdentifierKind.CONSTRUCTOR, u.getBegin().get().line, u.getNameAsString());

        p.addChild(methodNode);

        u.getParameters().accept(this, methodNode);
        u.getBody().accept(this, methodNode);

        return null;
    }

    @Override
    public Void visit(VariableDeclarator u, ASTIdentifierNode p) {
        ASTIdentifierNode variableNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.VARIABLE, u.getBegin().get().line, u.getTypeAsString());
        p.addChild(variableNode);

        return null;
    }

    @Override
    public Void visit(Parameter u, ASTIdentifierNode p) {
        ASTIdentifierNode variableNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.PARAMETER, u.getBegin().get().line, u.getTypeAsString());
        p.addChild(variableNode);

        return null;
    }

    @Override
    public Void visit(LambdaExpr u, ASTIdentifierNode p) {
        u.getParameters().accept(new NestedParameterVisitor(this) {
            @Override
            public Void visit(Parameter u, ASTIdentifierNode p) {
                ASTIdentifierNode variableNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.VARIABLE, u.getBegin().get().line, u.getType() instanceof UnknownType ? "`inferred type`" : u.getTypeAsString());
                p.addChild(variableNode);

                return null;
            }
        }, p);

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
}
