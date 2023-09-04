package app.qurancorpus.syntax;

import app.qurancorpus.Interval;
import app.qurancorpus.orthography.Document;
import app.qurancorpus.orthography.LocationService;
import app.qurancorpus.orthography.Token;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;

@Singleton
public class GraphCoverage {
    private final List<Interval<Token>> coverage;
    private final int tokenCount;

    public GraphCoverage(
            Document document,
            SyntaxService syntaxService,
            LocationService locationService) {

        var tokens = new Token[document.tokenCount()];
        var graphs = syntaxService.getGraphs();
        var tokenCount = 0;
        for (var graph : graphs) {
            for (var word : graph.getWords()) {
                if (word.type() == WordType.Token) {
                    var token = word.token();
                    var location = token.location();
                    var index = locationService.getTokenSequenceNumber(location) - 1;
                    if (tokens[index] != null) {
                        throw new UnsupportedOperationException(
                                format("Duplicate token at {0}", location));
                    }
                    tokens[index] = token;
                    tokenCount++;
                }
            }
        }

        this.coverage = getCoverage(tokens);
        this.tokenCount = tokenCount;
    }

    public int getTokenCount() {
        return tokenCount;
    }

    public List<Interval<Token>> getCoverage() {
        return coverage;
    }

    private List<Interval<Token>> getCoverage(Token[] tokens) {
        var coverage = new ArrayList<Interval<Token>>();
        Token start = null;
        Token end = null;
        for (var token : tokens) {
            if (token != null) {
                if (start == null) {
                    start = token;
                }
                end = token;
            } else if (start != null) {
                coverage.add(new Interval<>(start, end));
                start = null;
            }
        }
        if (start != null) {
            coverage.add(new Interval<>(start, end));
        }
        return coverage;
    }
}