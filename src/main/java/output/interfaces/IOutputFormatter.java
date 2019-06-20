package output.interfaces;

import model.ASTIdentifierNode;

import java.io.Closeable;
import java.io.IOException;

public interface IOutputFormatter extends Closeable {

    void print(ASTIdentifierNode root) throws IOException;

}
