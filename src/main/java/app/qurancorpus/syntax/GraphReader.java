package app.qurancorpus.syntax;

import app.qurancorpus.Interval;
import app.qurancorpus.morphology.PartOfSpeech;
import app.qurancorpus.orthography.Document;
import lombok.SneakyThrows;

import java.io.BufferedReader;

import static app.qurancorpus.arabic.encoding.unicode.UnicodeDecoder.fromUnicode;
import static app.qurancorpus.orthography.Location.parseLocation;
import static app.qurancorpus.syntax.WordType.*;
import static java.lang.Integer.parseInt;
import static java.text.MessageFormat.format;

public class GraphReader {
    private final Document document;
    private final BufferedReader reader;
    private SyntaxGraph graph;
    private int nodeSequenceNumber;
    private int lineNumber;

    public GraphReader(Document document, BufferedReader reader) {
        this.document = document;
        this.reader = reader;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @SneakyThrows
    public SyntaxGraph readGraph() {
        graph = new SyntaxGraph();
        nodeSequenceNumber = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            ++lineNumber;
            if (line.length() == 0 || line.startsWith("--")) {
                continue;
            }

            if (line.equals("go")) {
                return graph;
            }

            if (line.indexOf('=') >= 0) {
                readNode(line);
            } else {
                readEdge(line);
            }
        }
        return null;
    }

    private void readNode(String line) {

        // names
        var parts = line.split(" = ");
        var names = parts[0].split(",");
        var nodeCount = names.length;
        for (var name : names) {
            var nodeNumber = parseNodeName(name.trim());
            var expectedNodeNumber = ++nodeSequenceNumber;
            if (nodeNumber != expectedNodeNumber) {
                throw new UnsupportedOperationException(
                        format("Expected node {0} not {1}.", expectedNodeNumber, nodeNumber));
            }
        }

        // type
        var definition = parts[1];
        var index = definition.indexOf('(');
        if (index == -1) {
            throw new UnsupportedOperationException("Expected '('.");
        }
        var tag = definition.substring(0, index);

        // value
        var value = definition.substring(index + 1, definition.length() - 1);

        // node
        PhraseType phraseType;
        if (tag.equals("word")) {
            readWord(Token, value, nodeCount);
        } else if (tag.equals("reference")) {
            readWord(Reference, value, nodeCount);
        } else if ((phraseType = PhraseType.parse(tag)) != null) {
            readPhrase(phraseType, nodeCount, value);
        } else {
            readElidedWord(tag, nodeCount, value);
        }
    }

    private void readWord(WordType type, String value, int nodeCount) {
        graph.addWord(
                type,
                document.getToken(parseLocation(value)),
                null,
                null,
                nodeCount);
    }

    private void readElidedWord(String tag, int nodeCount, String value) {
        if (nodeCount != 1) {
            throw new UnsupportedOperationException("Expected a single name for elided node.");
        }

        graph.addWord(
                Elided,
                null,
                value.equals("*") ? null : fromUnicode(value),
                PartOfSpeech.parse(tag),
                1);
    }

    private void readPhrase(PhraseType phraseType, int nodeCount, String value) {
        if (nodeCount != 1) {
            throw new UnsupportedOperationException("Expected a single name for phrase node.");
        }
        var interval = readInterval(value);
        graph.addPhrase(phraseType, interval.start(), interval.end());
    }

    private void readEdge(String line) {
        var index = line.indexOf('(');
        if (index == -1) {
            throw new UnsupportedOperationException("Expected '('.");
        }
        var name = line.substring(0, index);
        var relation = Relation.parse(name);
        var interval = readInterval(line.substring(index + 1, line.length() - 1));
        graph.addEdge(interval.start(), interval.end(), relation);
    }

    private Interval<SyntaxNode> readInterval(String value) {
        var index = value.indexOf('-');
        if (index == -1) {
            throw new UnsupportedOperationException("Expected '-' for node interval.");
        }
        return new Interval<>(
                getNode(value.substring(0, index - 1)),
                getNode(value.substring(index + 2)));
    }

    private SyntaxNode getNode(String name) {
        return graph.getNodes().get(parseNodeName(name) - 1);
    }

    private int parseNodeName(String name) {
        if (name.charAt(0) != 'n') {
            throw new UnsupportedOperationException(
                    format("Node name {0} should start with n.", name));
        }
        return parseInt(name.substring(1));
    }
}