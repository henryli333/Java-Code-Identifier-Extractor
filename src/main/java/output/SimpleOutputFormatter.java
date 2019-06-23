package output;

import model.ASTIdentifierNode;
import output.interfaces.OutputFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleOutputFormatter implements OutputFormatter {

    @Override
    public String format(ASTIdentifierNode root) {
        StringBuilder sb = new StringBuilder();
        formatRecursive(root, -1, sb);
        return sb.toString();
    }

    private void formatRecursive(ASTIdentifierNode node, int depth, StringBuilder sb) {
        for (int i = 0; i < depth; ++i) {
            sb.append("\t");
        }

        sb.append(printNode(node) + "\n");

        List<ASTIdentifierNode> childList = new ArrayList<>(node.getChildren());
        childList.sort(ASTIdentifierNode::ordinalCompare);

        for (ASTIdentifierNode child : childList) {
            formatRecursive(child, depth + 1, sb);
        }
    }

    private String printNode(ASTIdentifierNode node) {

        String prefix;

        switch (node.Kind) {
            case PACKAGE:
                prefix = "Package name: ";
                break;
            case CLASS:
                prefix = "Class name: ";
                break;
            case METHOD:
                prefix = "Method name: ";
                break;
            case CONSTRUCTOR:
                prefix = "Constructor: ";
                break;
            case PARAMETER:
                prefix = "Parameter: ";
                break;
            case VARIABLE:
                prefix = "Variable: ";
                break;
            // Don't print root node; only its children are important
            case ROOT:
                return "";
            default:
                throw new RuntimeException("Are you missing a case?");
        }

        return prefix + (nullOrEmpty(node.Type) ? "" : "(" + node.Type + ") ") + node.Name;
    }

    private boolean nullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @Override
    public void close() throws IOException {

    }
}
