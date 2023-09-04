package app.qurancorpus.morphology;

import app.qurancorpus.morphology.segmentation.MorphologyWriter;
import app.qurancorpus.orthography.Document;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static app.qurancorpus.ResourceReader.readResource;
import static app.qurancorpus.morphology.Diptote.isDiptoteWithGenitiveFatha;
import static app.qurancorpus.morphology.Morphology.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@MicronautTest
class MorphologyTest {

    @Inject
    Document document;

    @Inject
    MorphologyGraph morphologyGraph;

    @Test
    @SneakyThrows
    void shouldRoundTripMorphology() {
        try (var reader = readResource("/data/morphology.txt")) {
            var writer = new MorphologyWriter();
            for (var chapter : document.children()) {
                for (var verse : chapter.verses()) {
                    for (var token : verse.tokens()) {
                        var segments = morphologyGraph.query(token);
                        assertThat(writer.write(segments), is(equalTo(reader.readLine())));
                    }
                }
            }
        }
    }

    @Test
    void shouldCountDiptotes() {
        var n = 0;
        for (var chapter : document.children()) {
            for (var verse : chapter.verses()) {
                for (var token : verse.tokens()) {
                    var segments = morphologyGraph.query(token);
                    if (isDiptoteWithGenitiveFatha(getStem(segments))) {
                        n++;
                    }
                }
            }
        }
        assertThat(n, is(equalTo(330)));
    }

    @Test
    void shouldCountEmphasisNoonWithTanween() {
        var n = 0;
        for (var chapter : document.children()) {
            for (var verse : chapter.verses()) {
                for (var token : verse.tokens()) {
                    for (var segment : morphologyGraph.query(token)) {
                        if (isEmphasisNoonWithTanween(segment)) {
                            n++;
                        }
                    }
                }
            }
        }
        assertThat(n, is(equalTo(2)));
    }

    @Test
    void shouldCountSuffixElision() {
        var n = 0;
        for (var chapter : document.children()) {
            for (var verse : chapter.verses()) {
                for (var token : verse.tokens()) {
                    for (var segment : morphologyGraph.query(token)) {
                        if (isSuffixElision(token, segment)) {
                            n++;
                        }
                    }
                }
            }
        }
        assertThat(n, is(equalTo(225)));
    }
}