package output;

import model.ASTIdentifierNode;
import model.IdentifierKind;
import output.interfaces.IOutputFormatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimpleOutputFormatter implements IOutputFormatter {

    @Override
    public String print(ASTIdentifierNode root) {
        StringBuilder sb = new StringBuilder();
        print(root, -1, sb);
        return sb.toString();
    }

    private void print(ASTIdentifierNode node, int depth, StringBuilder sb) {

        for (int i = 0; i < depth; ++i) {
            sb.append("\t");
        }

        sb.append(printNode(node)).append("\n");

        List<ASTIdentifierNode> childList = new ArrayList<>(node.getChildren());
        childList.sort(ASTIdentifierNode::ordinalCompare);

        for (ASTIdentifierNode child : childList) {
            print(child, depth + 1, sb);
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
            case VARIABLE:
                prefix = "Variable name: ";
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
}
