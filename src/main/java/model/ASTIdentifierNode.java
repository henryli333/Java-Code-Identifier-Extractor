package model;

import java.util.*;

public class ASTIdentifierNode {

    public final String Name;
    public final IdentifierKind Kind;
    public final int StartingLine;
    public final int EndingLine;
    public final String Type;

    private Collection<ASTIdentifierNode> _children = new ArrayList<>();

    public ASTIdentifierNode(String name, IdentifierKind kind, int startingLine, int endingLine, String type) {
        Name = name;
        Kind = kind;
        StartingLine = startingLine;
        EndingLine = endingLine;
        Type = type;
    }

    public ASTIdentifierNode(String name, IdentifierKind kind, int startingLine, int endingLine) {
        this(name, kind, startingLine, endingLine, null);
    }

    public void addChild(ASTIdentifierNode child) {
        _children.add(child);
    }

    public Collection<ASTIdentifierNode> getChildren() {
        return _children;
    }

    public static int compare(ASTIdentifierNode o1, ASTIdentifierNode o2) {
        return o1.StartingLine - o2.StartingLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ASTIdentifierNode)) return false;
        ASTIdentifierNode that = (ASTIdentifierNode) o;
        return Objects.equals(Name, that.Name) &&
                Kind == that.Kind &&
                StartingLine == that.StartingLine &&
                Objects.equals(Type, that.Type);
    }

}
