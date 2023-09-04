package app.qurancorpus.syntax;

public record EdgeResponse(
        int startNode,
        int endNode,
        String dependencyTag) {
}