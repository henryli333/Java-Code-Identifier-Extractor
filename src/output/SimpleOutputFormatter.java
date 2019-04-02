package output;

import model.ASTIdentifierNode;
import output.interfaces.IOutputFormatter;

public class SimpleOutputFormatter implements IOutputFormatter {

    @Override
    public void print(ASTIdentifierNode root) {
        print(root, -1);
    }

    private void print(ASTIdentifierNode node, int depth) {

        for (int i = 0; i < depth; ++i) {
            System.out.print("\t");
        }

        System.out.print(printNode(node) + "\n");

        for (ASTIdentifierNode child : node.getChildren()) {
            print(child, depth + 1);
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
