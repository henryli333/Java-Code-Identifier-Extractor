package model;

import java.util.Collection;
import java.util.HashSet;

public class ASTIdentifierNode {

    public final String Name;
    public final IdentifierKind Kind;
    public final String Type;

    public static int ordinalCompare(ASTIdentifierNode o1, ASTIdentifierNode o2) {
        if (o1.Kind.ordinal() > o2.Kind.ordinal())
            return -1;
        else if (o1.Kind.ordinal() == o2.Kind.ordinal()) {
            return o1.Name.compareTo(o2.Name);
        } else {
            return 1;
        }
    }

    private Collection<ASTIdentifierNode> _children = new HashSet<>();

    public ASTIdentifierNode(String name, IdentifierKind kind, String type) {
        Name = name;
        Kind = kind;
        Type = type;
    }

    public ASTIdentifierNode(String name, IdentifierKind kind) {
        this(name, kind,null);
    }

    public void addChild(ASTIdentifierNode child) {
        _children.add(child);
    }

    public Collection<ASTIdentifierNode> getChildren() {
        return _children;
    }

}
