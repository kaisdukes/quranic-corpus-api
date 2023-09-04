package app.qurancorpus.morphology.segmentation;

import app.qurancorpus.morphology.Segment;
import app.qurancorpus.orthography.Location;

import static app.qurancorpus.arabic.encoding.unicode.UnicodeEncoder.toUnicode;

public class TsvWriter {

    private TsvWriter() {
    }

    public static String writeSegment(Location location, Segment segment) {
        var line = new StringBuilder();
        line.append(location.chapterNumber());
        line.append('\t');
        line.append(location.verseNumber());
        line.append('\t');
        line.append(location.tokenNumber());
        line.append('\t');
        line.append(toUnicode(segment.getArabicText()));
        line.append('\t');
        line.append(new MorphologyWriter().write(segment));
        return line.toString();
    }
}