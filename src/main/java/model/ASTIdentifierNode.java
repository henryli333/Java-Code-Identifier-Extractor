package model;

import java.util.*;

public class ASTIdentifierNode {

    public final String Name;
    public final IdentifierKind Kind;
    public final String Type;

    private Collection<ASTIdentifierNode> _children = new ArrayList<>();

    public ASTIdentifierNode(String name, IdentifierKind kind, String type) {
        Name = name;
        Kind = kind;
        Type = type;
    }

    public ASTIdentifierNode(String name, IdentifierKind kind) {
        this(name, kind, null);
    }

    public void addChild(ASTIdentifierNode child) {
        _children.add(child);
    }

    public Collection<ASTIdentifierNode> getChildren() {
        return _children;
    }

    public static int ordinalCompare(ASTIdentifierNode o1, ASTIdentifierNode o2) {
        if (o1.Kind.ordinal() < o2.Kind.ordinal())
            return -1;
        else if (o1.Kind.ordinal() == o2.Kind.ordinal()) {
            return o1.Name.compareTo(o2.Name);
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ASTIdentifierNode)) return false;
        ASTIdentifierNode that = (ASTIdentifierNode) o;
        return Objects.equals(Name, that.Name) &&
                Kind == that.Kind &&
                Objects.equals(Type, that.Type);
    }

}
