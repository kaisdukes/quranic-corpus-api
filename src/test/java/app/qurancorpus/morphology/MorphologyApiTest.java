package app.qurancorpus.morphology;

import app.qurancorpus.CorpusClient;
import app.qurancorpus.orthography.Document;
import app.qurancorpus.orthography.Location;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static app.qurancorpus.orthography.VerseMark.Sajdah;
import static app.qurancorpus.orthography.VerseMark.Section;
import static io.micronaut.http.HttpStatus.BAD_REQUEST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
class MorphologyApiTest {

    @Inject
    Document document;

    @Inject
    CorpusClient client;

    @Test
    void shouldGetMorphology() {
        var verses = client.getMorphology(
                new Location(4, 79), 2,
                "sahih-international",
                null);
        assertThat(verses.length, is(equalTo(2)));

        var verse79 = verses[0];
        assertThat(verse79.location(), is(equalTo(new int[]{4, 79})));
        assertThat(verse79.tokens().length, is(equalTo(18)));
        assertThat(verse79.translations()[0].translation(), startsWith("What comes to you of good is from Allah"));

        var verse80 = verses[1];
        assertThat(verse80.location(), is(equalTo(new int[]{4, 80})));
        assertThat(verse80.tokens().length, is(equalTo(12)));

        var token = verse79.tokens()[12];
        assertThat(token.location(), is(equalTo(new int[]{4, 79, 13})));
        assertThat(token.translation(), is(equalTo("And We have sent you")));
        assertThat(token.phonetic(), is(equalTo("wa-arsalnāka")));

        var segments = token.segments();
        assertThat(segments.length, is(equalTo(4)));

        var verb = segments[1];
        assertThat(verb.arabic(), is(equalTo("أَرْسَلْ")));
        assertThat(verb.posTag(), is(equalTo("V")));

        var subjectPronoun = segments[2];
        assertThat(subjectPronoun.arabic(), is(equalTo("نَٰ")));
        assertThat(subjectPronoun.posTag(), is(equalTo("PRON")));
        assertThat(subjectPronoun.pronounType(), is(equalTo("subj")));

        var objectPronoun = segments[3];
        assertThat(objectPronoun.arabic(), is(equalTo("كَ")));
        assertThat(objectPronoun.posTag(), is(equalTo("PRON")));
        assertThat(objectPronoun.pronounType(), is(equalTo("obj")));
    }

    @Test
    void shouldGetMorphologyWithFeatures() {
        var verses = client.getMorphology(
                new Location(65, 2), 1,
                null,
                true);
        assertThat(verses.length, is(equalTo(1)));

        var verse2 = verses[0];
        assertThat(verse2.location(), is(equalTo(new int[]{65, 2})));
        assertThat(verse2.tokens().length, is(equalTo(30)));
        assertThat(verse2.translations(), is(nullValue()));

        var token = verse2.tokens()[3];
        assertThat(token.location(), is(equalTo(new int[]{65, 2, 4})));
        assertThat(token.translation(), is(equalTo("then retain them")));
        assertThat(token.phonetic(), is(equalTo("fa-amsikūhunna")));

        var segments = token.segments();
        assertThat(segments.length, is(equalTo(4)));

        var prefix = segments[0];
        assertThat(prefix.arabic(), is(equalTo("فَ")));
        assertThat(prefix.posTag(), is(nullValue()));
        assertThat(prefix.morphology(), is(equalTo("f:RSLT+")));

        var verb = segments[1];
        assertThat(verb.arabic(), is(equalTo("أَمْسِكُ")));
        assertThat(verb.posTag(), is(nullValue()));
        assertThat(verb.morphology(), is(equalTo("POS:V IMPV (IV) LEM:>amosaka ROOT:msk 2MP")));

        var subjectPronoun = segments[2];
        assertThat(subjectPronoun.arabic(), is(equalTo("و")));
        assertThat(subjectPronoun.posTag(), is(nullValue()));
        assertThat(subjectPronoun.pronounType(), is(nullValue()));
        assertThat(subjectPronoun.morphology(), is(nullValue()));

        var objectPronoun = segments[3];
        assertThat(objectPronoun.arabic(), is(equalTo("هُنَّ")));
        assertThat(objectPronoun.posTag(), is(nullValue()));
        assertThat(objectPronoun.pronounType(), is(nullValue()));
        assertThat(objectPronoun.morphology(), is(equalTo("PRON:3FP")));
    }

    @Test
    void shouldGetMorphologyForChapter() {
        var chapterNumber = 2;
        var verseNumber = 1;
        var verseCount = 5;
        var location = new Location(chapterNumber, verseNumber);
        var verses = client.getMorphology(location, verseCount, null, null);

        while (verses.length > 0) {
            verseNumber += verses.length;
            verses = client.getMorphology(new Location(chapterNumber, verseNumber), verseCount, null, null);
        }

        var lastVerse = verseNumber + verses.length - 1;
        assertThat(
                lastVerse,
                is(equalTo(document.getChild(chapterNumber).verses().length)));
    }

    @Test
    void shouldRejectZeroVerseCount() {
        var error = assertThrows(
                HttpClientResponseException.class,
                () -> client.getMorphology(new Location(2, 1), 0, null, null));

        assertThat(error.getStatus(), is(equalTo(BAD_REQUEST)));
    }

    @Test
    void shouldRejectLargeVerseCount() {
        var error = assertThrows(
                HttpClientResponseException.class,
                () -> client.getMorphology(new Location(3, 5), 11, null, null));

        assertThat(error.getStatus(), is(equalTo(BAD_REQUEST)));
    }

    @Test
    void morphologyShouldIncludeVerseMarks() {
        var verseWithSajdah = client.getMorphology(new Location(7, 206), 1, null, null);
        assertThat(verseWithSajdah[0].verseMark(), is(equalTo(Sajdah)));

        var verseWithSection = client.getMorphology(new Location(100, 9), 1, null, null);
        assertThat(verseWithSection[0].verseMark(), is(equalTo(Section)));
    }

    @Test
    void shouldGetNoTranslations() {
        var verses = client.getMorphology(
                new Location(12, 16),
                1,
                null,
                null);

        assertThat(verses[0].translations(), is(nullValue()));
    }

    @Test
    void shouldHandleEmptyTranslationsQuery() {
        var verses = client.getMorphology(
                new Location(12, 16),
                1,
                "",
                null);

        assertThat(verses[0].translations(), is(nullValue()));
    }

    @Test
    void shouldGetMultipleTranslations() {
        var verses = client.getMorphology(
                new Location(12, 16),
                1,
                "sahih-international,yusuf-ali,arberry",
                null);

        var translations = verses[0].translations();
        assertThat(translations.length, is(equalTo(3)));
        for (var translation : translations) {
            assertThat(
                    translation.name().length(),
                    is(greaterThanOrEqualTo(1)));

            assertThat(
                    translation.translation().length(),
                    is(greaterThanOrEqualTo(1)));
        }
    }

    @Test
    void shouldGetWordMorphology() {
        var wordMorphology = client.getWordMorphology(
                new Location(104, 4, 2));

        var token = wordMorphology.token();
        assertThat(token.location(), is(equalTo(new int[]{104, 4, 2})));
        assertThat(token.translation(), is(equalTo("Surely he will be thrown")));
        assertThat(token.phonetic(), is(equalTo("layunbadhanna")));

        var segments = token.segments();
        assertThat(segments.length, is(equalTo(3)));

        var prefix = segments[0];
        assertThat(prefix.arabic(), is(equalTo("لَ")));
        assertThat(prefix.posTag(), is(equalTo("EMPH")));

        var verb = segments[1];
        assertThat(verb.arabic(), is(equalTo("يُنۢبَذَ")));
        assertThat(verb.posTag(), is(equalTo("V")));

        var suffix = segments[2];
        assertThat(suffix.arabic(), is(equalTo("نَّ")));
        assertThat(suffix.posTag(), is(equalTo("EMPH")));

        assertThat(
                wordMorphology.summary(),
                startsWith("The second word of verse (104:4) is divided into 3 morphological segments."));

        var segmentDescriptions = wordMorphology.segmentDescriptions();
        assertThat(segmentDescriptions.length, is(equalTo(3)));
        assertThat(segmentDescriptions[0], is(equalTo("emphatic prefix {lām}")));
        assertThat(segmentDescriptions[1], is(equalTo("3rd person masculine singular passive imperfect verb")));
        assertThat(segmentDescriptions[2], is(equalTo("emphatic suffix {nūn}")));

        assertThat(
                wordMorphology.arabicGrammar(),
                is(equalTo("اللام لام التوكيد\nفعل مضارع مبني للمجهول والنون للتوكيد")));
    }
}