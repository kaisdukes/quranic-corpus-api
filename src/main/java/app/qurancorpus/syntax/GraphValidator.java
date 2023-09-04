package app.qurancorpus.syntax;

import app.qurancorpus.Interval;
import app.qurancorpus.morphology.MorphologyGraph;
import app.qurancorpus.orthography.Document;
import app.qurancorpus.orthography.Token;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

import static app.qurancorpus.morphology.PartOfSpeech.Determiner;
import static app.qurancorpus.syntax.GraphInterval.phraseInterval;
import static app.qurancorpus.syntax.WordType.Elided;
import static app.qurancorpus.syntax.WordType.Token;
import static java.text.MessageFormat.format;

@Singleton
public class GraphValidator {

    @Inject
    Document document;

    @Inject
    MorphologyGraph morphologyGraph;

    public void validate(SyntaxGraph graph) {
        validateTokenSequence(graph);
        validateSegments(graph);
        validatePhraseOrder(graph);
        validateEdgeOrder(graph);
    }

    private void validateTokenSequence(SyntaxGraph graph) {
        var words = graph.getWords();
        if (words.isEmpty()) {
            throw new UnsupportedOperationException("Graph has no words.");
        }

        Token prevToken = null;
        for (var word : words) {
            if (word.type() != Token) {
                continue;
            }

            var token = word.token();
            if (prevToken == null) {
                prevToken = token;
                continue;
            }

            var expectedToken = document.getNextToken(prevToken);
            if (expectedToken != token) {
                throw new UnsupportedOperationException(
                        format("Token out of sequence at {0}.", token.location()));
            }
            prevToken = token;
        }
    }

    private void validateSegments(SyntaxGraph graph) {
        for (var word : graph.getWords()) {
            if (word.type() == Elided) {
                continue;
            }
            var token = word.token();
            var visibleSegments = 0;
            for (var segment : morphologyGraph.query(token)) {
                if (segment.getPartOfSpeech() != Determiner) {
                    visibleSegments++;
                }
            }
            var nodeCount = word.end() - word.start() + 1;
            if (visibleSegments != nodeCount) {
                throw new UnsupportedOperationException(
                        format(
                                "Expected {0} segments not {1} at {2}.",
                                visibleSegments,
                                nodeCount,
                                token.location()));
            }
        }
    }

    private void validatePhraseOrder(SyntaxGraph graph) {
        var count = graph.getPhraseCount();
        var intervals = new ArrayList<Interval<Integer>>();
        for (var i = 0; i < count; i++) {
            intervals.add(phraseInterval(graph.getPhrase(i + 1)));
        }
        validateIntervals(intervals);
    }

    private void validateEdgeOrder(SyntaxGraph graph) {
        var intervals = graph
                .getEdges()
                .stream()
                .map(GraphInterval::edgeInterval)
                .toList();

        validateIntervals(intervals);
    }

    private void validateIntervals(List<Interval<Integer>> intervals) {
        var count = intervals.size();
        for (var i = 1; i < count; i++) {
            var a = intervals.get(i - 1);
            var b = intervals.get(i);

            var a1 = (int) a.start();
            var a2 = (int) a.end();
            var b1 = (int) b.start();
            var b2 = (int) b.end();

            if (a1 == b1 && a2 == b2) {
                throw new UnsupportedOperationException(
                        format("Duplicate interval {0}.", a));
            }

            // A is to the right of B: valid order
            if (a2 <= b1) {
                continue;
            }

            if (a1 >= b2) {
                throw new UnsupportedOperationException(
                        format("Interval {0} is to the left of interval {1}.", a, b));
            }

            // A is covered by B: valid order
            if (a1 >= b1 && a2 <= b2) {
                continue;
            }

            if (b1 >= a1 && b2 <= a2) {
                throw new UnsupportedOperationException(
                        format("Interval {0} is covered by interval {1}.", b, a));
            }

            if (b1 <= a1) {
                throw new UnsupportedOperationException(
                        format("Intervals intersect but interval {0} should start before interval {1}.", a, b));
            }
        }
    }
}