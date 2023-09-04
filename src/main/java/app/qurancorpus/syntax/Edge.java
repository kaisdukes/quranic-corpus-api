package app.qurancorpus.syntax;

public record Edge(SyntaxNode dependent, SyntaxNode head, Relation relation) {
}