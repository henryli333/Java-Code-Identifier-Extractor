package output.interfaces;

import model.ASTIdentifierNode;

import java.io.Closeable;
import java.io.IOException;

public interface OutputFormatter extends Closeable {

    String format(ASTIdentifierNode root);

}
