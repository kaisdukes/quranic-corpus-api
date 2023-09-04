package app.qurancorpus.translation;

import app.qurancorpus.orthography.Location;
import app.qurancorpus.orthography.LocationService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@MicronautTest
class TranslationTest {

    @Inject
    LocationService locationService;

    @Inject
    TranslationService translationService;

    @Test
    @SneakyThrows
    void shouldGetTokenTranslation() {
        var location = new Location(82, 7, 3);
        var tokenSequenceNumber = locationService.getTokenSequenceNumber(location);
        assertThat(
                translationService.getTokenTranslation(tokenSequenceNumber),
                is(equalTo("then fashioned you")));
    }
}