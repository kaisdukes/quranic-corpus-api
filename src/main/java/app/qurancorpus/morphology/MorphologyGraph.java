package app.qurancorpus.morphology;

import app.qurancorpus.nlg.ArabicGrammarGenerator;
import app.qurancorpus.nlg.SegmentDescriptionGenerator;
import app.qurancorpus.nlg.SummaryGenerator;
import app.qurancorpus.orthography.Location;
import app.qurancorpus.orthography.Token;
import memseqdb.Graph;

import java.util.Map;

import static app.qurancorpus.morphology.Morphology.getStem;
import static app.qurancorpus.morphology.Morphology.isDeterminerAl;
import static app.qurancorpus.morphology.PartOfSpeech.Determiner;

public class MorphologyGraph implements Graph<Token, Segment[]> {
    private final Map<Location, Segment[]> segmentMap;

    public MorphologyGraph(Map<Location, Segment[]> segmentMap) {
        this.segmentMap = segmentMap;
    }

    @Override
    public Segment[] query(Token token) {
        return segmentMap.get(token.location());
    }

    public WordMorphology getWordMorphology(Token token) {

        // summary
        var segments = query(token);
        var stem = getStem(segments);
        var summaryGenerator = new SummaryGenerator(token, segments, stem);
        var summary = summaryGenerator.generate();

        // segment descriptions
        var segmentCount = isDeterminerAl(segments) ? segments.length - 1 : segments.length;
        var segmentDescriptions = new String[segmentCount];
        var i = 0;
        for (var segment : segments) {
            if (segment.getPartOfSpeech() != Determiner) {
                var segmentDescriptionGenerator = new SegmentDescriptionGenerator(segments, stem, segment);
                segmentDescriptions[i++] = segmentDescriptionGenerator.generate();
            }
        }

        // arabic grammar
        var arabicGrammarGenerator = new ArabicGrammarGenerator(token, segments);
        var arabicGrammar = arabicGrammarGenerator.generate();
        return new WordMorphology(summary, segmentDescriptions, arabicGrammar);
    }
}