package app.qurancorpus.orthography;

import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import memseqdb.Seq;

import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;

@Singleton
public class LocationService {
    private final Seq<Location> verseSequenceNumbers;
    private final Seq<Location> tokenSequenceNumbers;

    @SneakyThrows
    public LocationService(Document document) {
        var verseLocations = new ArrayList<Location>();
        var tokenLocations = new ArrayList<Location>();

        for (var chapter : document.children()) {
            for (var verse : chapter.verses()) {
                verseLocations.add(verse.location());
                for (var token : verse.tokens()) {
                    tokenLocations.add(token.location());
                }
            }
        }

        verseSequenceNumbers = new Seq<>(verseLocations);
        tokenSequenceNumbers = new Seq<>(tokenLocations);
    }

    public int getVerseSequenceNumber(Location location) {
        var sequenceNumber = verseSequenceNumbers.getSequenceNumber(location);
        if (sequenceNumber == 0) {
            throw new UnsupportedOperationException(format("Verse {0} not found.", location));
        }
        return sequenceNumber;
    }

    public int getTokenSequenceNumber(Location location) {
        var sequenceNumber = tokenSequenceNumbers.getSequenceNumber(location);
        if (sequenceNumber == 0) {
            throw new UnsupportedOperationException(format("Token {0} not found.", location));
        }
        return sequenceNumber;
    }
}