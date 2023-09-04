package app.qurancorpus.morphology.segmentation;

import app.qurancorpus.lexicography.LemmaService;
import app.qurancorpus.morphology.PartOfSpeech;
import app.qurancorpus.morphology.PronounType;
import app.qurancorpus.morphology.Segment;
import app.qurancorpus.morphology.SegmentType;
import app.qurancorpus.orthography.Location;
import app.qurancorpus.orthography.Token;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import static app.qurancorpus.arabic.encoding.unicode.UnicodeDecoder.fromUnicode;
import static java.lang.Integer.parseInt;

public class TsvReader implements Closeable {
    private final SegmentReader segmentReader;
    private final List<Morpheme> morphemes = new ArrayList<>();
    private final List<Token> tokens = new ArrayList<>();
    private final List<Segment[]> segments = new ArrayList<>();
    private Location location;

    public TsvReader(LemmaService lemmaService) {
        segmentReader = new SegmentReader(lemmaService);
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public List<Segment[]> getSegments() {
        return segments;
    }

    @Override
    public void close() {
        readToken();
    }

    public void readSegment(String line) {
        var parts = line.split("\\t");

        var location = new Location(
                parseInt(parts[0]),
                parseInt(parts[1]),
                parseInt(parts[2]));

        if (this.location != null && !location.equals(this.location)) {
            readToken();
        }

        morphemes.add(new Morpheme(
                parts[3],
                parts.length >= 5 ? parts[4] : null));

        this.location = location;
    }

    private void readToken() {
        addToken();
        readSegments();
    }

    private void addToken() {
        var arabic = new StringBuilder();
        for (var morpheme : morphemes) {
            arabic.append(morpheme.arabic());
        }
        var arabicText = fromUnicode(arabic.toString());
        var token = new Token(location, arabicText);
        tokens.add(token);
    }

    private void readSegments() {
        Segment stem = null;
        Segment objectPronoun = null;

        var segmentCount = morphemes.size();
        var segments = new Segment[segmentCount];
        var index = 0;
        for (var i = 0; i < segmentCount; i++) {
            var morpheme = morphemes.get(i);
            Segment segment;
            if (morpheme.morphology() == null) {
                if (stem == null) {
                    throw new UnsupportedOperationException("Stem not found.");
                }
                segment = new Segment(SegmentType.Suffix, PartOfSpeech.Pronoun);
                segment.setPerson(stem.getPerson());
                segment.setGender(stem.getGender());
                segment.setNumber(stem.getNumber());
                segment.setPronounType(PronounType.Subject);
            } else {
                segment = segmentReader.read(morpheme.morphology(), stem != null);
            }

            segment.setSegmentNumber(i + 1);

            var arabicText = fromUnicode(morpheme.arabic());
            segment.setArabicText(arabicText);

            segment.setStartIndex(index);
            index += arabicText.getLength();
            segment.setEndIndex(index);

            if (segment.getPartOfSpeech() == PartOfSpeech.Pronoun
                    && segment.getType() == SegmentType.Suffix
                    && morpheme.morphology() != null) {
                segment.setPronounType(objectPronoun != null ? PronounType.SecondObject : PronounType.Object);
                if (objectPronoun == null) {
                    objectPronoun = segment;
                }
            }

            if (segment.getType() == SegmentType.Stem) {
                stem = segment;
            }

            segments[i] = segment;
        }

        this.segments.add(segments);
        morphemes.clear();
    }

    private record Morpheme(String arabic, String morphology) {
    }
}