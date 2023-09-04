package app.qurancorpus.syntax;

import app.qurancorpus.arabic.ArabicText;
import app.qurancorpus.morphology.PartOfSpeech;
import app.qurancorpus.orthography.Token;

import static app.qurancorpus.syntax.WordType.Token;

import java.util.ArrayList;
import java.util.List;

public class SyntaxGraph {
    private final List<Word> words = new ArrayList<>();
    private final List<SyntaxNode> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private int segmentNodeCount;

    public List<Word> getWords() {
        return words;
    }

    public void addWord(
            WordType type,
            Token token,
            ArabicText elidedText,
            PartOfSpeech elidedPartOfSpeech,
            int nodeCount) {

        var start = nodes.size();
        for (var i = 0; i < nodeCount; i++) {
            nodes.add(new SyntaxNode(
                    start + i,
                    null,
                    null,
                    null));
        }

        words.add(new Word(
                type,
                token,
                elidedText,
                elidedPartOfSpeech,
                start,
                start + nodeCount - 1));

        segmentNodeCount += nodeCount;
    }

    public List<SyntaxNode> getNodes() {
        return nodes;
    }

    public int getPhraseCount() {
        return nodes.size() - segmentNodeCount;
    }

    public SyntaxNode getPhrase(int phraseNumber) {
        return nodes.get(segmentNodeCount + phraseNumber - 1);
    }

    public void addPhrase(PhraseType phraseType, SyntaxNode start, SyntaxNode end) {
        nodes.add(new SyntaxNode(
                nodes.size(),
                phraseType,
                start,
                end));
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void addEdge(SyntaxNode dependent, SyntaxNode head, Relation relation) {
        edges.add(new Edge(dependent, head, relation));
    }

    public Token getFirstToken() {
        for (var word : words) {
            if (word.type() == Token) {
                return word.token();
            }
        }
        throw new UnsupportedOperationException("Failed to find first graph token.");
    }
}