package app.qurancorpus.syntax;

import app.qurancorpus.CorpusClient;
import app.qurancorpus.orthography.Location;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static app.qurancorpus.syntax.WordType.Elided;
import static app.qurancorpus.syntax.WordType.Token;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@MicronautTest
class SyntaxApiTest {

    @Inject
    CorpusClient client;

    @Test
    void shouldGetSyntax() {
        var graph = client.getSyntax(new Location(4, 79), 3);
        assertThat(graph.graphNumber(), is(equalTo(3)));
        assertThat(graph.graphCount(), is(equalTo(4)));
        assertThat(graph.legacyCorpusGraphNumber(), is(equalTo(2553)));

        var words = graph.words();
        assertThat(words.length, is(equalTo(3)));

        var word = words[0];
        assertThat(word.type(), is(equalTo(Token)));

        var token = word.token();
        assertThat(token.location(), is(equalTo(new int[]{4, 79, 13})));
        assertThat(token.translation(), is(equalTo("And We have sent you")));
        assertThat(token.phonetic(), is(equalTo("wa-arsalnāka")));

        var segments = token.segments();
        assertThat(segments.length, is(equalTo(4)));

        var verb = segments[1];
        assertThat(verb.arabic(), is(equalTo("أَرْسَلْ")));
        assertThat(verb.posTag(), is(equalTo("V")));

        var subjectPronoun = segments[2];
        assertThat(subjectPronoun.arabic(), is(equalTo("نَٰ")));
        assertThat(subjectPronoun.posTag(), is(equalTo("PRON")));
        assertThat(subjectPronoun.pronounType(), is(equalTo("subj")));

        var objectPronoun = segments[3];
        assertThat(objectPronoun.arabic(), is(equalTo("كَ")));
        assertThat(objectPronoun.posTag(), is(equalTo("PRON")));
        assertThat(objectPronoun.pronounType(), is(equalTo("obj")));

        var edges = graph.edges();
        assertThat(edges.length, is(equalTo(5)));

        var edge = edges[0];
        assertThat(edge.startNode(), is(equalTo(2)));
        assertThat(edge.endNode(), is(equalTo(1)));
        assertThat(edge.dependencyTag(), is(equalTo("subj")));

        var phraseNodes = graph.phraseNodes();
        assertThat(phraseNodes.length, is(equalTo(1)));

        var phraseNode = phraseNodes[0];
        assertThat(phraseNode.startNode(), is(equalTo(4)));
        assertThat(phraseNode.endNode(), is(equalTo(5)));
        assertThat(phraseNode.phraseTag(), is(equalTo("PP")));

        var prev = graph.prev();
        assertThat(prev.location(), is(equalTo(new int[]{4, 79})));
        assertThat(prev.graphNumber(), is(equalTo(2)));

        var next = graph.next();
        assertThat(next.location(), is(equalTo(new int[]{4, 79})));
        assertThat(next.graphNumber(), is(equalTo(4)));
    }

    @Test
    void shouldGetGraphWithElidedWord() {
        var graph = client.getSyntax(new Location(70, 5), 1);

        var words = graph.words();
        assertThat(words.length, is(equalTo(4)));

        var word = words[1];
        assertThat(word.type(), is(equalTo(Elided)));
        assertThat(word.elidedText(), is(equalTo("أَنتَ")));
        assertThat(word.elidedPosTag(), is(equalTo("PRON")));

        assertThat(graph.edges().length, is(equalTo(4)));
        assertThat(graph.phraseNodes(), is(nullValue()));

        var prev = graph.prev();
        assertThat(prev.location(), is(equalTo(new int[]{70, 4})));
        assertThat(prev.graphNumber(), is(equalTo(2)));

        var next = graph.next();
        assertThat(next.location(), is(equalTo(new int[]{70, 6})));
        assertThat(next.graphNumber(), is(equalTo(1)));
    }

    @Test
    void shouldNotFindGraph() {
        assertThat(
                client.getSyntax(new Location(58, 1), 1),
                is(nullValue()));
    }
}