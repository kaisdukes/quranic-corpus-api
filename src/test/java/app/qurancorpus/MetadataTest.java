package app.qurancorpus;

import app.qurancorpus.orthography.ChapterMetadata;
import app.qurancorpus.translation.TranslationMetadata;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@MicronautTest
class MetadataTest {

    @Inject
    CorpusClient client;

    @Test
    void shouldGetMetadata() {
        var metadata = client.getMetadata();

        verifyChapters(metadata.chapters());
        verifyTranslations(metadata.translations());
    }

    private void verifyChapters(ChapterMetadata[] chapters) {
        assertThat(chapters.length, is(equalTo(114)));

        var chapter = chapters[32];
        assertThat(chapter.chapterNumber(), is(equalTo(33)));
        assertThat(chapter.verseCount(), is(equalTo(73)));
        assertThat(chapter.phonetic(), is(equalTo("Al-Aḥzāb")));
        assertThat(chapter.translation(), is(equalTo("The Combined Forces")));
        assertThat(chapter.city(), is(equalTo("Madinah")));
    }

    private void verifyTranslations(TranslationMetadata[] translations) {
        assertThat(translations.length, is(equalTo(7)));

        var translation = translations[5];
        assertThat(translation.key(), is(equalTo("mohsin-khan")));
        assertThat(translation.name(), is(equalTo("Mohsin Khan")));
    }
}