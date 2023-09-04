package app.qurancorpus.syntax;

import app.qurancorpus.orthography.Document;
import app.qurancorpus.orthography.LocationService;
import jakarta.inject.Singleton;
import memseqdb.SeqItem;
import memseqdb.SeqPair;
import memseqdb.SparseSeq;
import memseqdb.SparseSeq2Seq;

import java.util.ArrayList;
import java.util.List;

import static app.qurancorpus.ResourceReader.readResource;
import static app.qurancorpus.syntax.WordType.Token;
import static java.text.MessageFormat.format;

@Singleton
public class SyntaxService {
    private final List<SyntaxGraph> graphs = new ArrayList<>();
    private final SparseSeq2Seq tokenToGraph;
    private final SparseSeq<VerseGraphs> verseToGraphs;

    public SyntaxService(Document document, LocationService locationService) {
        var path = "/data/syntax.txt";
        var reader = new GraphReader(document, readResource(path));
        var tokenToGraph = new ArrayList<SeqPair>();
        var verseToGraphs = new ArrayList<SeqItem<VerseGraphs>>();
        try {
            var lastVerseSequenceNumber = 0;
            VerseGraphs verseGraphs = null;
            SyntaxGraph graph;
            while ((graph = reader.readGraph()) != null) {
                graphs.add(graph);
                var graphSequenceNumber = graphs.size();

                var indexedGraphByVerse = false;
                for (var word : graph.getWords()) {
                    if (word.type() != Token) {
                        continue;
                    }

                    // index graph by token
                    var location = word.token().location();
                    var tokenSequenceNumber = locationService.getTokenSequenceNumber(location);
                    tokenToGraph.add(new SeqPair(tokenSequenceNumber, graphSequenceNumber));

                    // new verse?
                    var verse = document.getVerse(location.chapterNumber(), location.verseNumber());
                    var verseSequenceNumber = locationService.getVerseSequenceNumber(verse.location());
                    if (verseSequenceNumber > lastVerseSequenceNumber) {
                        lastVerseSequenceNumber = verseSequenceNumber;
                        indexedGraphByVerse = false;
                        verseGraphs = null;
                    }

                    // index graph by verse
                    if (!indexedGraphByVerse) {
                        if (verseGraphs == null) {
                            verseGraphs = new VerseGraphs(new ArrayList<>());
                            verseToGraphs.add(new SeqItem<>(verseSequenceNumber, verseGraphs));
                        }
                        verseGraphs.graphSequenceNumbers().add(graphSequenceNumber);
                        indexedGraphByVerse = true;
                    }
                }
            }
            this.tokenToGraph = new SparseSeq2Seq(tokenToGraph);
            this.verseToGraphs = new SparseSeq<>(verseToGraphs);
        } catch (Exception e) {
            throw new UnsupportedOperationException(
                    format("{0}:{1}: {2}", path, reader.getLineNumber(), e.getMessage()),
                    e);
        }
    }

    public List<SyntaxGraph> getGraphs() {
        return graphs;
    }

    public int getGraphForToken(int tokenSequenceNumber) {
        return tokenToGraph.getSequenceNumberB(tokenSequenceNumber);
    }

    public List<Integer> getGraphsForVerse(int verseSequenceNumber) {
        var verseGraphs = verseToGraphs.getItem(verseSequenceNumber);
        return verseGraphs != null ? verseGraphs.graphSequenceNumbers() : null;
    }

    private record VerseGraphs(List<Integer> graphSequenceNumbers) {
    }
}