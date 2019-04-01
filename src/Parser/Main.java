package Parser;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.sun.source.tree.*;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SimpleTreeVisitor;

public class Main {
    public static void main(String[] args) throws Exception {

        // TODO: Take from args? Also preferably recursive option would be nice (see below)
        String file = "./src/Parser/Main.java";

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        // TODO: Get a list instead of single
        Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(file);
        JavacTask javac = (JavacTask) compiler
                .getTask(null, fileManager, null, null, null, fileObjects);

        Iterable<? extends CompilationUnitTree> trees = javac.parse();

        for (CompilationUnitTree tree : trees) {
            tree.accept(new UniversalVisitor(), null);
        }
    }

    // TODO: Provide some tree structure as in and out parameter so we can construct a tree to serialise into json instead of printing
    static class UniversalVisitor extends SimpleTreeVisitor<Void, Void> {
        @Override
        public Void visitCompilationUnit(CompilationUnitTree cut, Void p) {
            System.out.println("Package name: " + cut.getPackageName());

            for (Tree t : cut.getTypeDecls()) {
                if (t instanceof ClassTree) {
                    ClassTree ct = (ClassTree) t;
                    ct.accept(this, null);
                }
            }
            return super.visitCompilationUnit(cut, p);
        }

        @Override
        public Void visitClass(ClassTree ct, Void p) {
            System.out.println("Class name: " + ct.getSimpleName());

            for (Tree t : ct.getMembers()) {
                if (t instanceof MethodTree) {
                    MethodTree mt = (MethodTree) t;
                    mt.accept(this, null);
                }
                else if (t instanceof VariableTree) {
                    VariableTree vt = (VariableTree) t;
                    vt.accept(this, null);
                }
                else if (t instanceof ClassTree) {
                    ClassTree ict = (ClassTree) t;
                    ict.accept(this, null);
                }
            }

            return super.visitClass(ct, p);
        }

        @Override
        public Void visitMethod(MethodTree mt, Void p) {
            System.out.println("Method name: (" + mt.getReturnType() + ") " + mt.getName());

            for (StatementTree st : mt.getBody().getStatements()) {
                // TODO: try/catch/finally; switch; if; for/enchancedfor/while/dowhile; block(do recursive)
                if (st instanceof VariableTree) {
                    VariableTree vt = (VariableTree) st;
                    vt.accept(this, null);
                }
                else if (st instanceof ForLoopTree) {
                    ForLoopTree flt = (ForLoopTree) st;
                    flt.accept(this, null);
                } else if (st instanceof EnhancedForLoopTree) {
                    EnhancedForLoopTree ehflt = (EnhancedForLoopTree) st;
                    ehflt.accept(this, null);
                } else if (st instanceof DoWhileLoopTree) {
                    DoWhileLoopTree dwlt = (DoWhileLoopTree) st;
                    dwlt.accept(this, null);
                }
            }

            return super.visitMethod(mt, p);
        }

        @Override
        public Void visitVariable(VariableTree vt, Void p) {
            System.out.println("Variable name: (" + vt.getType() + ") " + vt.getName());
            return super.visitVariable(vt, p);
        }

        // TODO: Visitors for above
    }
}
