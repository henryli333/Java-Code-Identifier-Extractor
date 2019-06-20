import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import com.github.javaparser.utils.ParserCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import output.interfaces.IOutputFormatter;
import model.ASTIdentifierNode;
import model.IdentifierKind;
import output.SimpleOutputFormatter;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static final String DEFAULT_PACKAGE_NAME = "<default package>";

    public static void main(String[] args) throws Exception {

        // TODO: Take from args? Also preferably recursive option would be nice (see below)
        String rootFile = System.getProperty("user.dir");

        SourceRoot sourceRoot = new SourceRoot(Paths.get(rootFile));
        List<ParseResult<CompilationUnit>> compilationUnits = sourceRoot.tryToParse();

        ASTIdentifierNode root = new ASTIdentifierNode("root", IdentifierKind.ROOT);

        for (ParseResult<CompilationUnit> pr : compilationUnits) {
            if (pr.getResult().isPresent()) {
                pr.getResult().get().accept(new UniversalVisitor(), root);
            }
        }

        try (IOutputFormatter formatter = new SimpleOutputFormatter()) {
            formatter.print(root);
        }
        catch (IOException e) {
            int y = 11;
            e.printStackTrace();
        }
        finally {
            int y = 0;
            System.out.println("\nDone");
        }


        // IDEA: make this a command line application that can print to stdout and be piped into analysis program
    }

    static class UniversalVisitor extends GenericVisitorAdapter<Void, ASTIdentifierNode> {

        @Override
        public Void visit(CompilationUnit u, ASTIdentifierNode p) {

            ASTIdentifierNode compilationUnitNode = new ASTIdentifierNode(u.getPackageDeclaration().isPresent() ? u.getPackageDeclaration().get().getName().asString() : DEFAULT_PACKAGE_NAME, IdentifierKind.PACKAGE);
            p.addChild(compilationUnitNode);

            u.getTypes().accept(this, compilationUnitNode);

            return null;
        }

        @Override
        public Void visit(ClassOrInterfaceDeclaration u, ASTIdentifierNode p) {

            ASTIdentifierNode classNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.CLASS);
            p.addChild(classNode);

            u.getMembers().accept(this, classNode);

            return null;
        }


        @Override
        public Void visit(EnumDeclaration u, ASTIdentifierNode p) {
            ASTIdentifierNode classNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.CLASS);
            p.addChild(classNode);

            u.getEntries().accept(this, classNode);
            u.getMembers().accept(this, classNode);

            return null;
        }

        @Override
        public Void visit(EnumConstantDeclaration u, ASTIdentifierNode p) {
            ASTIdentifierNode variableNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.VARIABLE, p.Name);
            p.addChild(variableNode);

            return null;
        }

        @Override
        public Void visit(MethodDeclaration u, ASTIdentifierNode p) {

            ASTIdentifierNode methodNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.METHOD, u.getTypeAsString());

            p.addChild(methodNode);

            u.getParameters().accept(this, methodNode);

            if (u.getBody().isPresent()) {
                u.getBody().get().accept(this, methodNode);
            }

            return null;
        }

        @Override
        public Void visit(ConstructorDeclaration u, ASTIdentifierNode p) {
            ASTIdentifierNode methodNode = new ASTIdentifierNode("`constructor`", IdentifierKind.CONSTRUCTOR, u.getNameAsString());

            p.addChild(methodNode);

            u.getParameters().accept(this, methodNode);
            u.getBody().accept(this, methodNode);

            return null;
        }

        @Override
        public Void visit(VariableDeclarator u, ASTIdentifierNode p) {
            ASTIdentifierNode variableNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.VARIABLE, u.getTypeAsString());
            p.addChild(variableNode);

            return null;
        }

        @Override
        public Void visit(Parameter u, ASTIdentifierNode p) {
            ASTIdentifierNode variableNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.PARAMETER, u.getTypeAsString());
            p.addChild(variableNode);

            return null;
        }

        @Override
        public Void visit(LambdaExpr u, ASTIdentifierNode p) {
            u.getParameters().accept(new ModifiedUniversalVisitor(), p);

            u.getBody().accept(this, p);
            if (u.getExpressionBody().isPresent()) {
                u.getExpressionBody().get().accept(this, p);
            }

            return null;
        }

        @Override
        public Void visit(CatchClause u, ASTIdentifierNode p) {
            u.getParameter().accept(new ModifiedUniversalVisitor(), p);
            u.getBody().accept(this, p);
            return null;
        }

    }

    static class ModifiedUniversalVisitor extends Main.UniversalVisitor {
        @Override
        public Void visit(Parameter u, ASTIdentifierNode p) {
            ASTIdentifierNode variableNode = new ASTIdentifierNode(u.getNameAsString(), IdentifierKind.VARIABLE, u.getType() instanceof UnknownType ? "`inferred type`" : u.getTypeAsString());
            p.addChild(variableNode);

            return null;
        }
    }
}
