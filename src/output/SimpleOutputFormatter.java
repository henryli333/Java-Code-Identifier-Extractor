package output;

import model.ASTIdentifierNode;
import model.IdentifierKind;
import output.interfaces.IOutputFormatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

        List<ASTIdentifierNode> childList = new ArrayList<>(node.getChildren());
        childList.sort((o1, o2) -> {
            if (o1.Kind.ordinal() > o2.Kind.ordinal())
                return -1;
            else if (o1.Kind.ordinal() == o2.Kind.ordinal()) {
                return o1.Name.compareTo(o2.Name);
            }
            else {
                return 1;
            }
        });

        for (ASTIdentifierNode child : childList) {
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
