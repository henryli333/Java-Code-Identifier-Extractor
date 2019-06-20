package model;

// NB: Keep the kind ordered from highest level to lowest level so that the ordinals can be used for sorting
public enum IdentifierKind {
    ROOT,
    PACKAGE,
    CLASS,
    METHOD,
    CONSTRUCTOR,
    PARAMETER,
    VARIABLE
}
