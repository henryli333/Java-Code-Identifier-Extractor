import com.github.javaparser.JavaParser;
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
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import visitor.IdentifierExtractorVisitor;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Command(name = "JavaIdentifierExtractor", description = "Looks at Java source code and extracts code identifiers for methods")
public class Main implements Runnable {

    enum Formatter {
        TABULATED,
        JSON
    }

    @Option(names = {"-f", "--format"}, description = "Format from: ${COMPLETION-CANDIDATES}\n(default: ${DEFAULT-VALUE})")
    private Formatter formatterType = Formatter.JSON;

    @Option(names = {"-r", "--recursive"}, description = "Recursively inspect directories")
    private boolean isRecursive;

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Displays a help message")
    private boolean helpRequested;

    @Parameters(paramLabel = "ROOT-PATHS", description = "1 or more files/directories for inspection", arity = "1..*")
    private String[] rootDirs;

    public static void main(String[] args) {
        CommandLine cli = new CommandLine(new Main());
        cli.setCaseInsensitiveEnumValuesAllowed(true);

        int exitCode = cli.execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {

        ASTIdentifierNode root = new ASTIdentifierNode("root", IdentifierKind.ROOT);

        if (isRecursive) {
            for (String dir : rootDirs) {
                SourceRoot sourceRoot = new SourceRoot(Paths.get(dir).toAbsolutePath().normalize());
                JavaSymbolSolver symbolSolver = new JavaSymbolSolver(new JavaParserTypeSolver(dir));
                sourceRoot.getParserConfiguration().setSymbolResolver(symbolSolver);

                try {
                    List<ParseResult<CompilationUnit>> compilationUnits = sourceRoot.tryToParse();

                    for (ParseResult<CompilationUnit> pr : compilationUnits) {
                        if (pr.getResult().isPresent()) {
                            CompilationUnit cu = pr.getResult().get();
                            cu.accept(new IdentifierExtractorVisitor(cu), root);
                        }
                    }

                    output(root);
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        else {
            for (String dir : rootDirs) {
                try {
                    CompilationUnit cu = new JavaParser().parse(Paths.get(dir)).getResult().get();
                    cu.accept(new IdentifierExtractorVisitor(cu), root);
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                output(root);
            }
        }




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

    private void output(ASTIdentifierNode root) {

        try (OutputFormatter formatter = getFormatter(formatterType)) {
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
    }

}
