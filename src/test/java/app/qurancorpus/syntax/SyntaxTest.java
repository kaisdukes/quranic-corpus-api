package app.qurancorpus.syntax;

import app.qurancorpus.morphology.MorphologyGraph;
import app.qurancorpus.orthography.Document;
import app.qurancorpus.orthography.Location;
import app.qurancorpus.orthography.LocationService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;

import static app.qurancorpus.ResourceReader.readResource;
import static app.qurancorpus.syntax.PhraseType.NominalSentence;
import static app.qurancorpus.syntax.PhraseType.PrepositionPhrase;
import static app.qurancorpus.syntax.Relation.Link;
import static app.qurancorpus.syntax.Relation.Predicate;
import static app.qurancorpus.syntax.Syntax.isPrepositionPhrase;
import static app.qurancorpus.syntax.Syntax.isPreventivePhrase;
import static app.qurancorpus.syntax.WordType.*;
import static java.nio.file.Files.deleteIfExists;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@MicronautTest
class SyntaxTest {

    @Inject
    Document document;

    @Inject
    MorphologyGraph morphologyGraph;

    @Inject
    SyntaxService syntaxService;

    @Inject
    LocationService locationService;

    @Inject
    GraphValidator graphValidator;

    @Inject
    GraphCoverage graphCoverage;

    @Inject
    LegacyCorpusGraphMapper legacyCorpusGraphMapper;

    @Test
    void shouldGetGraphForToken() {
        var location = new Location(84, 25, 6);
        var tokenSequenceNumber = locationService.getTokenSequenceNumber(location);
        var graphSequenceNumber = syntaxService.getGraphForToken(tokenSequenceNumber);
        var graph = syntaxService.getGraphs().get(graphSequenceNumber - 1);

        var words = graph.getWords();
        assertThat(words.size(), is(equalTo(6)));
        assertThat(words.get(0).type(), is(equalTo(Reference)));

        var star = words.get(2);
        assertThat(star.type(), is(equalTo(Elided)));
        assertThat(star.elidedText(), is(nullValue()));

        var phraseCount = graph.getPhraseCount();
        assertThat(phraseCount, is(equalTo(2)));
        assertThat(graph.getPhrase(1).phraseType(), is(equalTo(PrepositionPhrase)));
        assertThat(graph.getPhrase(2).phraseType(), is(equalTo(NominalSentence)));

        var edges = graph.getEdges();
        assertThat(edges.size(), is(equalTo(6)));
        assertThat(edges.get(1).relation(), is(equalTo(Link)));
        assertThat(edges.get(5).relation(), is(equalTo(Predicate)));
    }

    @Test
    void shouldGetGraphsForVerse() {
        verifyGraphs(new Location(1, 1), 1);
        verifyGraphs(new Location(8, 26), 5);
        verifyGraphs(new Location(77, 3), 1);
        verifyGraphs(new Location(77, 4), 1);
        verifyGraphs(new Location(77, 5), 1);
        verifyGraphs(new Location(114, 6), 1);
    }

    @Test
    @SneakyThrows
    void shouldRoundTripSyntax() {
        var tempFile = Paths.get("syntax-test.txt");
        try {

            // write
            try (var writer = new GraphWriter(tempFile)) {
                writer.writeGraphs(syntaxService.getGraphs());
            }

            // diff
            var lineCount = 0;
            try (var expectedReader = readResource("/data/syntax.txt")) {
                try (var outputReader = new BufferedReader(new FileReader(tempFile.toFile()))) {
                    String expected;
                    while ((expected = expectedReader.readLine()) != null) {
                        assertThat(outputReader.readLine(), is(equalTo(expected)));
                        lineCount++;
                    }
                }
            }
            assertThat(lineCount, is(equalTo(152623)));
        } finally {
            deleteIfExists(tempFile);
        }
    }

    @Test
    void shouldValidateGraphs() {
        var lastTokenSequenceNumber = 0;
        for (var graph : syntaxService.getGraphs()) {
            graphValidator.validate(graph);

            // graphs should be ordered
            var location = graph.getFirstToken().location();
            var tokenSequenceNumber = locationService.getTokenSequenceNumber(location);
            assertThat(tokenSequenceNumber, is(greaterThan(lastTokenSequenceNumber)));
            lastTokenSequenceNumber = tokenSequenceNumber;
        }
    }

    @Test
    void shouldGetGraphCoverage() {

        // stats
        assertThat(syntaxService.getGraphs().size(), is(equalTo(7373)));
        assertThat(graphCoverage.getTokenCount(), is(equalTo(32617)));

        // coverage
        var coverage = graphCoverage.getCoverage();
        assertThat(coverage.size(), is(equalTo(2)));
        assertThat(coverage.get(0).start().location(), is(equalTo(new Location(1, 1, 1))));
        assertThat(coverage.get(0).end().location(), is(equalTo(new Location(9, 91, 26))));
        assertThat(coverage.get(1).start().location(), is(equalTo(new Location(59, 1, 1))));
        assertThat(coverage.get(1).end().location(), is(equalTo(new Location(114, 6, 3))));
    }

    @Test
    void shouldGetLegacyCorpusGraphNumbers() {
        {
            var graphSequenceNumber = 5373;
            var graph = syntaxService.getGraphs().get(graphSequenceNumber - 1);
            var location = new Location(8, 75, 18);
            assertThat(graph.getFirstToken().location(), is(equalTo(location)));
            assertThat(legacyCorpusGraphMapper.getLegacyCorpusGraphNumber(graphSequenceNumber), is(equalTo(5373)));
        }

        {
            var location = new Location(84, 25, 6);
            var tokenSequenceNumber = locationService.getTokenSequenceNumber(location);
            var graphSequenceNumber = syntaxService.getGraphForToken(tokenSequenceNumber);
            assertThat(legacyCorpusGraphMapper.getLegacyCorpusGraphNumber(graphSequenceNumber), is(equalTo(6659)));
        }

        var lastGraphNumber = syntaxService.getGraphs().size();
        assertThat(legacyCorpusGraphMapper.getLegacyCorpusGraphNumber(lastGraphNumber), is(equalTo(6967)));
    }

    @Test
    void shouldCountPrepositionPhrasesInTokens() {
        var n1 = 0;
        var n2 = 0;
        for (var chapter : document.children()) {
            for (var verse : chapter.verses()) {
                for (var token : verse.tokens()) {
                    var segments = morphologyGraph.query(token);
                    if (isPrepositionPhrase(segments)) {
                        n1++;
                    }
                    var segmentCount = segments.length;
                    for (var i = 0; i < segmentCount; i++) {
                        if (isPrepositionPhrase(segments, i)) {
                            n2++;
                        }
                    }
                }
            }
        }
        assertThat(n1, is(equalTo(7494)));
        assertThat(n2, is(equalTo(7494)));
    }

    @Test
    void shouldCountPreventivePhrasesInTokens() {
        var n1 = 0;
        var n2 = 0;
        for (var chapter : document.children()) {
            for (var verse : chapter.verses()) {
                for (var token : verse.tokens()) {
                    var segments = morphologyGraph.query(token);
                    if (isPreventivePhrase(segments)) {
                        n1++;
                    }
                    var segmentCount = segments.length;
                    for (var i = 0; i < segmentCount; i++) {
                        if (isPreventivePhrase(segments, i)) {
                            n2++;
                        }
                    }
                }
            }
        }
        assertThat(n1, is(equalTo(160)));
        assertThat(n2, is(equalTo(160)));
    }

    private void verifyGraphs(Location location, int graphCount) {
        var verseSequenceNumber = locationService.getVerseSequenceNumber(location);
        var graphSequenceNumbers = syntaxService.getGraphsForVerse(verseSequenceNumber);
        assertThat(graphSequenceNumbers.size(), is(equalTo(graphCount)));

        var graphs = syntaxService.getGraphs();
        for (var graphSequenceNumber : graphSequenceNumbers) {
            var matchVerse = false;
            for (var word : graphs.get(graphSequenceNumber - 1).getWords()) {
                if (word.type() != Token) {
                    continue;
                }
                var tokenLocation = word.token().location();
                if (tokenLocation.chapterNumber() == location.chapterNumber()
                        && tokenLocation.verseNumber() == location.verseNumber()) {
                    matchVerse = true;
                    break;
                }
            }
            assertThat(matchVerse, is(equalTo(true)));
        }
    }
}