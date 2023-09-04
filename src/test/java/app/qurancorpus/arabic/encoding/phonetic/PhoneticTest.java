package app.qurancorpus.arabic.encoding.phonetic;

import app.qurancorpus.morphology.MorphologyGraph;
import app.qurancorpus.orthography.Document;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;

import static app.qurancorpus.arabic.encoding.buckwalter.BuckwalterDecoder.fromBuckwalter;
import static app.qurancorpus.arabic.encoding.phonetic.PhoneticEncoder.toPhonetic;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@MicronautTest
class PhoneticTest {

    @Inject
    Document document;

    @Inject
    MorphologyGraph morphologyGraph;

    @Test
    void shouldEncodeWord() {
        var text = fromBuckwalter("A^dam");
        assertThat(
                toPhonetic(new PhoneticContext(morphologyGraph, null), text),
                is(equalTo("ādam")));
    }

    @Test
    void shouldEncodeVerse() {
        assertThat(
                toPhonetic(
                        new PhoneticContext(morphologyGraph, null),
                        document.getVerse(22, 7).arabicText()),
                is(equalTo("wa-anna alssāʿata ātiyatun lā rayba fīhā wa-anna allaha yabʿathu man fī al'qubūri")));
    }

    @Test
    @SneakyThrows
    void shouldEncodeDocument() {
        try (var reader = new BufferedReader(new FileReader("regression/phonetic.txt"))) {
            for (var chapter : document.children()) {
                for (var verse : chapter.verses()) {
                    for (var token : verse.tokens()) {
                        assertThat(
                                toPhonetic(
                                        new PhoneticContext(morphologyGraph, token),
                                        token.arabicText()),
                                is(equalTo(reader.readLine())));
                    }
                }
            }
        }
    }
}