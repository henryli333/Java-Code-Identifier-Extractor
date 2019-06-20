import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.utils.SourceRoot;

import output.interfaces.IOutputFormatter;
import model.ASTIdentifierNode;
import model.IdentifierKind;
import output.SimpleOutputFormatter;
import visitor.IdentifierExtractorVisitor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        // TODO: Take from args? Also preferably recursive option would be nice (see below)
        String rootFile = System.getProperty("user.dir");

        SourceRoot sourceRoot = new SourceRoot(Paths.get(rootFile));
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(new JavaParserTypeSolver(rootFile));
        sourceRoot.getParserConfiguration().setSymbolResolver(symbolSolver);

        List<ParseResult<CompilationUnit>> compilationUnits = sourceRoot.tryToParse();

        ASTIdentifierNode root = new ASTIdentifierNode("root", IdentifierKind.ROOT);

        for (ParseResult<CompilationUnit> pr : compilationUnits) {
            if (pr.getResult().isPresent()) {
                CompilationUnit cu = pr.getResult().get();
                cu.accept(new IdentifierExtractorVisitor(cu), root);
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

        /*YamlPrinter printer = new YamlPrinter(true);
        for (ParseResult<CompilationUnit> pr : compilationUnits) {
            if (pr.getResult().isPresent()) {
                System.out.println(printer.output(pr.getResult().get()));
            }
        }*/

        // IDEA: make this a command line application that can print to stdout and be piped into analysis program
    }

    private static List<File> getAllJavaFilesFromRoot(File rootDir) {

        List<File> javaFiles = new ArrayList<>();
        javaFiles.addAll(Arrays.asList(rootDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".java");
            }
        })));

        for (File dir : rootDir.listFiles((dir, name) -> Paths.get(dir.toString(), name).toFile().isDirectory())) {
            javaFiles.addAll(getAllJavaFilesFromRoot(dir));
        }

        return javaFiles;
    }

}
