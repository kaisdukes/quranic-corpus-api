package app.qurancorpus.syntax;

import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static app.qurancorpus.arabic.encoding.unicode.UnicodeEncoder.toUnicode;
import static app.qurancorpus.syntax.WordType.Reference;

public class GraphWriter implements Closeable {
    private final BufferedWriter writer;

    public GraphWriter(Path path) throws IOException {
        this(new BufferedWriter(new FileWriter(path.toFile())));
    }

    public GraphWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    @SneakyThrows
    public void writeGraphs(List<SyntaxGraph> graphs) {
        var graphCount = graphs.size();
        for (var i = 0; i < graphCount; i++) {
            var graph = graphs.get(i);
            if (i > 0) {
                writer.write('\n');
            }
            writeGraph(graph);
        }
    }

    @SneakyThrows
    private void writeGraph(SyntaxGraph graph) {

        // words
        var words = graph.getWords();
        if (!words.isEmpty()) {
            writer.write("-- words");
            writer.write('\n');
            for (var word : words) {
                writeWord(word);
                writer.write('\n');
            }
        }

        // phrases
        var phraseCount = graph.getPhraseCount();
        if (phraseCount > 0) {
            writer.write('\n');
            writer.write("-- phrases");
            writer.write('\n');
            for (var i = 0; i < phraseCount; i++) {
                writePhraseNode(graph.getPhrase(i + 1));
                writer.write('\n');
            }
        }

        // edges
        var edgeCount = graph.getEdges().size();
        if (edgeCount > 0) {
            writer.write('\n');
            writer.write("-- edges");
            writer.write('\n');
            for (var edge : graph.getEdges()) {
                writeEdge(edge);
                writer.write('\n');
            }
        }

        // batch
        writer.write('\n');
        writer.write("go");
        writer.write('\n');
    }

    private void writeWord(Word word) {
        switch (word.type()) {
            case Token, Reference -> writeToken(word);
            case Elided -> writeElidedWord(word);
        }
    }

    @SneakyThrows
    private void writeToken(Word word) {

        // nodes
        for (var i = word.start(); i <= word.end(); i++) {
            if (i > word.start()) {
                writer.write(", ");
            }
            write(i);
        }

        // token
        writer.write(" = ");
        writer.write(word.type() == Reference ? "reference" : "word");
        writer.write('(');
        writer.write(word.token().location().toString());
        writer.write(')');
    }

    @SneakyThrows
    private void writeElidedWord(Word word) {
        write(word.start());
        writer.write(" = ");
        writer.write(word.elidedPartOfSpeech().tag());
        writer.write('(');
        var arabicText = word.elidedText();
        writer.write(arabicText == null ? "*" : toUnicode(arabicText));
        writer.write(')');
    }

    @SneakyThrows
    private void writePhraseNode(SyntaxNode node) {
        write(node.index());
        writer.write(" = ");
        writer.write(node.phraseType().tag());
        writer.write('(');
        write(node.start().index());
        writer.write(" - ");
        write(node.end().index());
        writer.write(')');
    }

    @SneakyThrows
    private void writeEdge(Edge edge) {
        writer.write(edge.relation().tag());
        writer.write('(');
        write(edge.dependent().index());
        writer.write(" - ");
        write(edge.head().index());
        writer.write(')');
    }

    @SneakyThrows
    private void write(int index) {
        writer.write('n');
        writer.write(Integer.toString(index + 1));
    }

    @Override
    @SneakyThrows
    public void close() {
        writer.close();
    }
}