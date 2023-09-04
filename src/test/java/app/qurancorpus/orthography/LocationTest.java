package app.qurancorpus.orthography;

import org.junit.jupiter.api.Test;

import static app.qurancorpus.orthography.Location.parseLocation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class LocationTest {

    @Test
    void shouldParseVerseLocation() {
        assertThat(
                parseLocation("72:26"),
                is(equalTo(new Location(72, 26))));
    }

    @Test
    void shouldParseTokenLocation() {
        assertThat(
                parseLocation("55:4:2"),
                is(equalTo(new Location(55, 4, 2))));
    }
}