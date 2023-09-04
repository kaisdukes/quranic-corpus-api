package app.qurancorpus.morphology;

import app.qurancorpus.arabic.ArabicText;
import app.qurancorpus.lexicography.LemmaService;
import app.qurancorpus.morphology.segmentation.TsvReader;
import app.qurancorpus.orthography.Document;
import app.qurancorpus.orthography.Token;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static app.qurancorpus.arabic.encoding.unicode.UnicodeEncoder.toUnicode;
import static app.qurancorpus.morphology.segmentation.TsvWriter.writeSegment;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@MicronautTest
class SegmentTest {

    @Inject
    Document document;

    @Inject
    MorphologyGraph morphologyGraph;

    @Inject
    LemmaService lemmaService;

    @Test
    void shouldRoundTripMorphologyFromTsv() {

        // The standard morphology file is exported at token level. The purpose of this test is to
        // ensure that if we export data at segment level, then the morphology is fully recoverable.

        // round trip
        var tsvReader = new TsvReader(lemmaService);
        for (var chapter : document.children()) {
            for (var verse : chapter.verses()) {
                for (var token : verse.tokens()) {
                    for (var segment : morphologyGraph.query(token)) {
                        var line = writeSegment(token.location(), segment);
                        tsvReader.readSegment(line);
                    }
                }
            }
        }
        tsvReader.close();

        // verify
        var tokens = tsvReader.getTokens();
        var segments = tsvReader.getSegments();
        var tokenSequenceNumber = 0;
        for (var chapter : document.children()) {
            for (var verse : chapter.verses()) {
                for (var token : verse.tokens()) {
                    verifyToken(tokens.get(tokenSequenceNumber), token);
                    verifySegments(segments.get(tokenSequenceNumber), morphologyGraph.query(token));
                    tokenSequenceNumber++;
                }
            }
        }
    }

    private void verifyToken(Token t1, Token t2) {
        assertThat(t1.location(), is(equalTo(t2.location())));
        verifyArabic(t1.arabicText(), t2.arabicText());
    }

    private void verifySegments(Segment[] s1, Segment[] s2) {
        assertThat(s1.length, is(equalTo(s2.length)));
        for (var i = 0; i < s1.length; i++) {
            verifySegment(s1[i], s2[i]);
        }
    }

    private void verifySegment(Segment s1, Segment s2) {
        verifyArabic(s1.getArabicText(), s2.getArabicText());
        assertThat(s1.getType(), is(equalTo(s2.getType())));
        assertThat(s1.getStartIndex(), is(equalTo(s2.getStartIndex())));
        assertThat(s1.getSegmentNumber(), is(equalTo(s2.getSegmentNumber())));
        assertThat(s1.getEndIndex(), is(equalTo(s2.getEndIndex())));
        assertThat(s1.getPartOfSpeech(), is(equalTo(s2.getPartOfSpeech())));
        verifyArabic(s1.getRoot(), s2.getRoot());
        assertThat(s1.getLemma(), is(equalTo(s2.getLemma())));
        assertThat(s1.getPerson(), is(equalTo(s2.getPerson())));
        assertThat(s1.getGender(), is(equalTo(s2.getGender())));
        assertThat(s1.getNumber(), is(equalTo(s2.getNumber())));
        assertThat(s1.getAspect(), is(equalTo(s2.getAspect())));
        assertThat(s1.getMood(), is(equalTo(s2.getMood())));
        assertThat(s1.getVoice(), is(equalTo(s2.getVoice())));
        assertThat(s1.getForm(), is(equalTo(s2.getForm())));
        assertThat(s1.getDerivation(), is(equalTo(s2.getDerivation())));
        assertThat(s1.getState(), is(equalTo(s2.getState())));
        assertThat(s1.getCase(), is(equalTo(s2.getCase())));
        assertThat(s1.getPronounType(), is(equalTo(s2.getPronounType())));
        assertThat(s1.getSpecial(), is(equalTo(s2.getSpecial())));
    }

    private void verifyArabic(ArabicText a1, ArabicText a2) {
        if (a1 == null) {
            assertThat(a2, is(nullValue()));
        } else {
            assertThat(a2, is(not(nullValue())));
            assertThat(toUnicode(a1), is(equalTo(toUnicode(a2))));
        }
    }
}