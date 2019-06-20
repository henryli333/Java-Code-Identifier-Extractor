package output.interfaces;

import model.ASTIdentifierNode;

import java.io.Closeable;

public interface IOutputFormatter extends Closeable {

    void print(ASTIdentifierNode root);

}
