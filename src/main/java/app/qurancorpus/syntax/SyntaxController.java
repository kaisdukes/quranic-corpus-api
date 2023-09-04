package app.qurancorpus.syntax;

import app.qurancorpus.morphology.TokenTransformer;
import app.qurancorpus.orthography.Document;
import app.qurancorpus.orthography.LocationService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;

import javax.validation.constraints.Min;
import java.util.List;

import static app.qurancorpus.arabic.encoding.unicode.UnicodeEncoder.toUnicode;
import static app.qurancorpus.orthography.Location.parseLocation;

@Controller("/syntax")
public class SyntaxController {

    @Inject
    SyntaxService syntaxService;

    @Inject
    LocationService locationService;

    @Inject
    LegacyCorpusGraphMapper legacyCorpusGraphMapper;

    @Inject
    TokenTransformer tokenTransformer;

    @Inject
    Document document;

    @Get
    public GraphResponse getSyntax(
            @QueryValue String location,
            @Min(1) @QueryValue("graph") int graphNumber) {

        var _location = parseLocation(location);
        var verseSequenceNumber = locationService.getVerseSequenceNumber(_location);
        var graphSequenceNumbers = syntaxService.getGraphsForVerse(verseSequenceNumber);
        if (graphSequenceNumbers == null) {
            return null;
        }

        var graphCount = graphSequenceNumbers.size();
        var graphSequenceNumber = graphSequenceNumbers.get(graphNumber - 1);
        var graph = syntaxService.getGraphs().get(graphSequenceNumber - 1);

        return new GraphResponse(
                graphNumber,
                graphCount,
                legacyCorpusGraphMapper.getLegacyCorpusGraphNumber(graphSequenceNumber),
                getGraphLocationResponse(graphSequenceNumber - 1),
                getGraphLocationResponse(graphSequenceNumber + 1),
                getWords(graph.getWords()),
                getEdges(graph.getEdges()),
                getPhraseNodes(graph));
    }

    private GraphLocationResponse getGraphLocationResponse(int graphSequenceNumber) {
        var graphs = syntaxService.getGraphs();
        if (graphSequenceNumber < 1 || graphSequenceNumber > graphs.size()) {
            return null;
        }
        var tokenLocation = graphs.get(graphSequenceNumber - 1).getFirstToken().location();
        var verse = document.getVerse(tokenLocation.chapterNumber(), tokenLocation.verseNumber());
        var location = verse.location();
        var verseSequenceNumber = locationService.getVerseSequenceNumber(location);
        var graphSequenceNumbers = syntaxService.getGraphsForVerse(verseSequenceNumber);
        var graphNumber = graphSequenceNumber - graphSequenceNumbers.get(0) + 1;
        return new GraphLocationResponse(location.toArray(), graphNumber);
    }

    private WordResponse[] getWords(List<Word> words) {
        var wordCount = words.size();
        var wordResponses = new WordResponse[wordCount];
        for (var i = 0; i < wordCount; i++) {
            var word = words.get(i);
            var token = word.token();
            var elidedText = word.elidedText();
            var elidedPartOfSpeech = word.elidedPartOfSpeech();
            wordResponses[i] = new WordResponse(
                    word.type(),
                    token != null ? tokenTransformer.getTokenResponse(token, false) : null,
                    elidedText != null ? toUnicode(elidedText) : null,
                    elidedPartOfSpeech != null ? elidedPartOfSpeech.tag() : null,
                    word.start(),
                    word.end());
        }
        return wordResponses;
    }

    private EdgeResponse[] getEdges(List<Edge> edges) {
        var edgeCount = edges.size();
        var edgeResponses = new EdgeResponse[edgeCount];
        for (var i = 0; i < edgeCount; i++) {
            var edge = edges.get(i);
            edgeResponses[i] = new EdgeResponse(
                    edge.dependent().index(),
                    edge.head().index(),
                    edge.relation().tag());
        }
        return edgeResponses;
    }

    private PhraseNodeResponse[] getPhraseNodes(SyntaxGraph graph) {
        var phraseCount = graph.getPhraseCount();
        var phraseNodeResponses = new PhraseNodeResponse[phraseCount];
        for (var i = 0; i < phraseCount; i++) {
            var phraseNode = graph.getPhrase(i + 1);
            phraseNodeResponses[i] = new PhraseNodeResponse(
                    phraseNode.start().index(),
                    phraseNode.end().index(),
                    phraseNode.phraseType().tag());
        }
        return phraseNodeResponses;
    }
}