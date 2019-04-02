package parser;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.sun.source.tree.*;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SimpleTreeVisitor;
import model.ASTIdentifierNode;
import model.IdentifierKind;
import output.SimpleOutputFormatter;
import output.interfaces.IOutputFormatter;

public class Main {
    public static void main(String[] args) throws Exception {

        // TODO: Take from args? Also preferably recursive option would be nice (see below)
        String file = "./src/parser/Main.java";

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        // TODO: Get a list instead of single
        Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(file);
        JavacTask javac = (JavacTask) compiler
                .getTask(null, fileManager, null, null, null, fileObjects);

        Iterable<? extends CompilationUnitTree> trees = javac.parse();

        ASTIdentifierNode root = new ASTIdentifierNode("root", IdentifierKind.ROOT);

        for (CompilationUnitTree tree : trees) {
            tree.accept(new UniversalVisitor(), root);
        }

        IOutputFormatter formatter = new SimpleOutputFormatter();
        formatter.print(root);

        // IDEA: make this a command line application that can print to stdout and be piped into analysis program
    }

    // TODO: Provide some tree structure as in and out parameter so we can construct a tree to serialise into json instead of printing
    static class UniversalVisitor extends SimpleTreeVisitor<Void, ASTIdentifierNode> {

        @Override
        public Void visitCompilationUnit(CompilationUnitTree cut, ASTIdentifierNode p) {

            ASTIdentifierNode compilationUnitNode = new ASTIdentifierNode(cut.getPackageName().toString(), IdentifierKind.PACKAGE);
            p.addChild(compilationUnitNode);

            for (Tree t : cut.getTypeDecls()) {
                t.accept(this, compilationUnitNode);
            }
            return super.visitCompilationUnit(cut, p);
        }

        @Override
        public Void visitClass(ClassTree ct, ASTIdentifierNode p) {

            ASTIdentifierNode classNode = new ASTIdentifierNode(ct.getSimpleName().toString(), IdentifierKind.CLASS);
            p.addChild(classNode);

            for (Tree t : ct.getMembers()) {
                t.accept(this, classNode);
            }

            return super.visitClass(ct, p);
        }

        @Override
        public Void visitMethod(MethodTree mt, ASTIdentifierNode p) {

            ASTIdentifierNode methodNode = new ASTIdentifierNode(mt.getName().toString(), IdentifierKind.METHOD, mt.getReturnType().toString());
            p.addChild(methodNode);

            for (VariableTree vt : mt.getParameters()) {
                vt.accept(this, methodNode);
            }

            for (StatementTree st : mt.getBody().getStatements()) {
                st.accept(this, methodNode);
            }

            return super.visitMethod(mt, p);
        }

        @Override
        public Void visitBlock(BlockTree bt, ASTIdentifierNode p) {

            for (StatementTree st : bt.getStatements()) {
                st.accept(this, p);
            }

            return super.visitBlock(bt, p);
        }

        @Override
        public Void visitDoWhileLoop(DoWhileLoopTree dwlt, ASTIdentifierNode p) {

            dwlt.getStatement().accept(this, p);

            return super.visitDoWhileLoop(dwlt, p);
        }

        @Override
        public Void visitWhileLoop(WhileLoopTree wlt, ASTIdentifierNode p) {

            wlt.getStatement().accept(this, p);

            return super.visitWhileLoop(wlt, p);
        }

        @Override
        public Void visitEnhancedForLoop(EnhancedForLoopTree eflt, ASTIdentifierNode p) {

            eflt.getVariable().accept(this, p);
            eflt.getStatement().accept(this, p);

            return super.visitEnhancedForLoop(eflt, p);
        }

        @Override
        public Void visitSwitch(SwitchTree st, ASTIdentifierNode p) {

            for (CaseTree ct : st.getCases()) {
                ct.accept(this, p);
            }

            return super.visitSwitch(st, p);
        }

        @Override
        public Void visitCase(CaseTree ct, ASTIdentifierNode p) {

            for (StatementTree st : ct.getStatements()) {
                st.accept(this, p);
            }

            return super.visitCase(ct, p);
        }

        @Override
        public Void visitTry(TryTree tt, ASTIdentifierNode p) {

            tt.getBlock().accept(this, p);

            for (CatchTree ct : tt.getCatches()) {
                ct.accept(this, p);
            }

            tt.getFinallyBlock().accept(this, p);

            // Does this do anything?
            for (Tree t : tt.getResources()) {
                t.accept(this, p);
            }

            return super.visitTry(tt, p);
        }

        @Override
        public Void visitCatch(CatchTree ct, ASTIdentifierNode p) {

            ct.getParameter().accept(this, p);
            ct.getBlock().accept(this, p);

            return super.visitCatch(ct, p);
        }

        @Override
        public Void visitConditionalExpression(ConditionalExpressionTree cet, ASTIdentifierNode p) {

            cet.getFalseExpression().accept(this, p);
            cet.getTrueExpression().accept(this, p);

            return super.visitConditionalExpression(cet, p);
        }

        @Override
        public Void visitIf(IfTree it, ASTIdentifierNode p) {

            it.getThenStatement().accept(this, p);
            it.getElseStatement().accept(this, p);

            return super.visitIf(it, p);
        }

        // TODO: Discuss how to deal with lambdas. New kind, or flatten onto enclosing method?
        @Override
        public Void visitLambdaExpression(LambdaExpressionTree let, ASTIdentifierNode p) {
            return super.visitLambdaExpression(let, p);
        }

        // TODO: Figure out what this is
        @Override
        public Void visitMemberSelect(MemberSelectTree mst, ASTIdentifierNode p) {

            mst.getExpression().accept(this, p);

            return super.visitMemberSelect(mst, p);
        }

        // TODO: Figure our what this is
        @Override
        public Void visitMemberReference(MemberReferenceTree mrt, ASTIdentifierNode p) {
            return super.visitMemberReference(mrt, p);
        }

        @Override
        public Void visitVariable(VariableTree vt, ASTIdentifierNode p) {
            ASTIdentifierNode variableNode = new ASTIdentifierNode(vt.getName().toString(), IdentifierKind.VARIABLE, vt.getType().toString());
            p.addChild(variableNode);

            return super.visitVariable(vt, p);
        }

        @Override
        public Void visitForLoop(ForLoopTree flt, ASTIdentifierNode p) {

            for (StatementTree st : flt.getInitializer()) {
                st.accept(this, p);
            }

            flt.getStatement().accept(this, p);

            return super.visitForLoop(flt, p);
        }

    }
}
