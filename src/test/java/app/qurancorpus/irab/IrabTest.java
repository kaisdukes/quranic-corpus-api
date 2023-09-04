package app.qurancorpus.irab;

import app.qurancorpus.orthography.Location;
import app.qurancorpus.orthography.LocationService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import memseqdb.SeqRange;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@MicronautTest
class IrabTest {

    @Inject
    IrabGraph irabGraph;

    @Inject
    LocationService locationService;

    @Test
    void shouldGetIrab() {
        var start = new Location(78, 1, 1);
        var end = new Location(78, 2, 3);

        var irab = irabGraph.query(
                new SeqRange(
                        locationService.getTokenSequenceNumber(start),
                        locationService.getTokenSequenceNumber(end)));

        assertThat(irab.length, is(equalTo(4)));
        assertThat(irab[0], startsWith("عَمَّ: أصله: عما"));
        assertThat(irab[1], startsWith("يَتَساءَلُونَ: فعل مضارع مرفوع"));
        assertThat(irab[2], startsWith("عَنِ النَّبَإِ: جار ومجرور"));
        assertThat(irab[3], startsWith("الْعَظِيمِ: صفة"));
    }
}