package app.qurancorpus.orthography;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static app.qurancorpus.orthography.PauseMark.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@MicronautTest
class PauseMarkTest {

    @Inject
    LocationService locationService;

    @Inject
    OrthographyService orthographyService;

    @Test
    void shouldGetPauseMarks() {
        verifyPauseMark(5, 64, 10, Compulsory);
        verifyPauseMark(6, 51, 8, NotPermissible);
        verifyPauseMark(4, 19, 10, ContinuationPreferred);
        verifyPauseMark(4, 23, 49, PausePreferred);
        verifyPauseMark(4, 18, 19, Permissible);
        verifyPauseMark(5, 26, 4, Interchangeable);
    }

    private void verifyPauseMark(int chapterNumber, int verseNumber, int tokenNumber, PauseMark expected) {
        var location = new Location(chapterNumber, verseNumber, tokenNumber);
        var tokenSequenceNumber = locationService.getTokenSequenceNumber(location);
        var output = orthographyService.getPauseMark(tokenSequenceNumber);
        assertThat(expected, is(equalTo(output)));
    }
}