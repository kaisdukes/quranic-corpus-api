package app.qurancorpus.irab;

import app.qurancorpus.CorpusClient;
import app.qurancorpus.orthography.Location;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.micronaut.http.HttpStatus.BAD_REQUEST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
class IrabApiTest {

    @Inject
    CorpusClient client;

    @Test
    void shouldGetIrab() {
        var irab = client.getIrab(
                new Location(2, 43, 1),
                new Location(2, 43, 7));

        assertThat(irab.length, is(equalTo(3)));
        assertThat(irab[0], startsWith("وَأَقِيمُوا الصَّلاةَ: الواو: عاطفة"));
        assertThat(irab[1], startsWith("وَآتُوا الزَّكاةَ وَارْكَعُوا: الواو: عاطفة"));
        assertThat(irab[2], startsWith("مَعَ الرَّاكِعِينَ: جار ومجرور"));
    }

    @Test
    void shouldRejectLongRequest() {
        var error = assertThrows(
                HttpClientResponseException.class,
                () -> client.getIrab(
                        new Location(1, 1, 1),
                        new Location(2, 3, 1)));

        assertThat(error.getStatus(), is(equalTo(BAD_REQUEST)));
    }
}