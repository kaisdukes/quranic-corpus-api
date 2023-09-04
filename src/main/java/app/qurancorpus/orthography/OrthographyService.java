package app.qurancorpus.orthography;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import memseqdb.SeqItem;
import memseqdb.SparseSeq;

import java.util.ArrayList;

import static app.qurancorpus.ResourceReader.readResource;
import static app.qurancorpus.orthography.PauseMark.PAUSE_MARKS;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;

@Singleton
public class OrthographyService {
    private final ChapterMetadata[] chapters;
    private final SparseSeq<VerseMark> verseMarks;
    private final SparseSeq<PauseMark> pauseMarks;

    public OrthographyService(
            LocationService locationService,
            Document document,
            ObjectMapper objectMapper) {

        chapters = readChapters(document, objectMapper);
        verseMarks = readVerseMarks(locationService, objectMapper);
        pauseMarks = readPauseMarks();
    }

    public ChapterMetadata[] getChapters() {
        return chapters;
    }

    public VerseMark getVerseMark(int verseSequenceNumber) {
        return verseMarks.getItem(verseSequenceNumber);
    }

    public PauseMark getPauseMark(int tokenSequenceNumber) {
        return pauseMarks.getItem(tokenSequenceNumber);
    }

    @SneakyThrows
    private static ChapterMetadata[] readChapters(Document document, ObjectMapper objectMapper) {
        var chapterInfo = objectMapper.readValue(
                readResource("/data/chapters.json"),
                ChapterInfo[].class);

        return stream(chapterInfo)
                .map(info -> new ChapterMetadata(
                        info.chapterNumber(),
                        document.getChild(info.chapterNumber()).verses().length,
                        info.phonetic(),
                        info.translation(),
                        info.city()))
                .toArray(ChapterMetadata[]::new);
    }

    @SneakyThrows
    private static SparseSeq<VerseMark> readVerseMarks(LocationService locationService, ObjectMapper objectMapper) {
        return new SparseSeq<>(stream(objectMapper
                .readValue(readResource("/data/verses.json"), VerseInfo[].class))
                .map(verse -> new SeqItem<>(
                        locationService.getVerseSequenceNumber(
                                new Location(verse.chapterNumber(), verse.verseNumber())),
                        verse.verseMark()))
                .toList());
    }

    @SneakyThrows
    private SparseSeq<PauseMark> readPauseMarks() {
        var pauseMarks = new ArrayList<SeqItem<PauseMark>>();
        try (var reader = readResource("/data/pause-marks.tsv")) {
            String line;
            while ((line = reader.readLine()) != null) {
                var parts = line.split("\t");
                var tokenSequenceNumber = parseInt(parts[0]);
                var pauseMark = PAUSE_MARKS[parseInt(parts[1]) - 1];
                pauseMarks.add(new SeqItem<>(tokenSequenceNumber, pauseMark));
            }
        }
        return new SparseSeq<>(pauseMarks);
    }
}