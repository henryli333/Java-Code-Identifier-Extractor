import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.utils.SourceRoot;

import output.JsonOutputFormatter;
import output.SimpleOutputFormatter;
import model.ASTIdentifierNode;
import model.IdentifierKind;
import output.interfaces.OutputFormatter;
import visitor.IdentifierExtractorVisitor;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    enum Formatter {
        TABULATED,
        JSON
    }

    private static final Formatter FORMATTER_TYPE = Formatter.JSON;

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

        try (OutputFormatter formatter = getFormatter(FORMATTER_TYPE)) {
            System.out.println(formatter.format(root));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
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

    private static OutputFormatter getFormatter(Formatter f) {
        switch (f) {
            case TABULATED:
                return new SimpleOutputFormatter();
            case JSON:
                return new JsonOutputFormatter();
            default:
                return new JsonOutputFormatter();
        }
    }

}
