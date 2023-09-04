package app.qurancorpus.orthography;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
class LocationServiceTest {

    @Inject
    LocationService locationService;

    @Test
    void shouldGetVerseSequenceNumber() {
        assertThat(
                locationService.getVerseSequenceNumber(
                        new Location(72, 26)),
                is(equalTo(5473)));
    }

    @Test
    void shouldRejectInvalidVerse() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> locationService.getVerseSequenceNumber(
                        new Location(72, 29)));
    }

    @Test
    void shouldGetTokenSequenceNumber() {
        assertThat(
                locationService.getTokenSequenceNumber(
                        new Location(55, 4, 2)),
                is(equalTo(68545)));
    }

    @Test
    void shouldRejectInvalidToken() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> locationService.getTokenSequenceNumber(
                        new Location(55, 4, 3)));
    }
}