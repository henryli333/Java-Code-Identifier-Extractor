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

        sb.append(printNode(node));
        sb.append("\n");

        List<ASTIdentifierNode> childList = new ArrayList<>(node.getChildren());
        childList.sort(ASTIdentifierNode::compare);

        for (ASTIdentifierNode child : childList) {
            formatRecursive(child, depth + 1, sb);
        }
    }

    private String printNode(ASTIdentifierNode node) {

        StringBuilder sb = new StringBuilder();

        switch (node.Kind) {
            case PACKAGE:
                sb.append("Package name: ");
                break;
            case CLASS:
                sb.append("Class name: ");
                break;
            case METHOD:
                sb.append("Method name: ");
                break;
            case CONSTRUCTOR:
                sb.append("Constructor: ");
                break;
            case PARAMETER:
                sb.append("Parameter: ");
                break;
            case FIELD:
                sb.append("Field: ");
                break;
            case VARIABLE:
                sb.append("Variable: ");
                break;
            case USE:
                sb.append("Use: ");
                break;
            // Don't print root node; only its children are important
            case ROOT:
                return "";
            default:
                throw new RuntimeException("Are you missing a case?");
        }

        if (nullOrEmpty(node.Type)) {
            return sb.append(String.format("%s [%d-%d]", node.Name, node.StartingLine, node.EndingLine)).toString();
        }
        else {
            return sb.append(String.format("(%s) %s [%d-%d]", node.Type, node.Name, node.StartingLine, node.EndingLine)).toString();
        }

    }

    private boolean nullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @Override
    public void close() throws IOException {

    }
}
