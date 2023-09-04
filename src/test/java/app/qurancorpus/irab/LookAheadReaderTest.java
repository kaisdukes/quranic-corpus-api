package app.qurancorpus.irab;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LookAheadReaderTest {

    @Test
    public void shouldNotReadEmptyInput() {
        try (var reader = createLookAheadReader("", 5)) {
            assertThat(reader.canRead(), is(equalTo(false)));
        }
    }

    @Test
    public void shouldReadSingleCharacterInput() {
        try (var reader = createLookAheadReader("a", 5)) {
            assertThat(reader.canRead(), is(equalTo(true)));
            assertThat(reader.read(), is(equalTo('a')));
            assertThat(reader.canRead(), is(equalTo(false)));
        }
    }

    @Test
    public void shouldReadReadMultipleCharactersWithinBuffer() {
        try (var reader = createLookAheadReader("abc", 5)) {
            assertThat(reader.canRead(), is(equalTo(true)));
            assertThat(reader.read(), is(equalTo('a')));
            assertThat(reader.canRead(), is(equalTo(true)));
            assertThat(reader.read(), is(equalTo('b')));
            assertThat(reader.canRead(), is(equalTo(true)));
            assertThat(reader.read(), is(equalTo('c')));
            assertThat(reader.canRead(), is(equalTo(false)));
        }
    }

    @Test
    public void shouldPeekSingleCharacter() {
        try (var reader = createLookAheadReader("a", 5)) {
            assertThat(reader.peek(), is(equalTo('a')));
            assertThat(reader.canRead(), is(equalTo(true)));
        }
    }

    @Test
    public void shouldPeekMultipleCharacters() {
        try (var reader = createLookAheadReader("abc", 5)) {
            assertThat(reader.peek(), is(equalTo('a')));
            assertThat(reader.peek(2), is(equalTo('b')));
            assertThat(reader.peek(3), is(equalTo('c')));
        }
    }

    @Test
    public void shouldNotPeekPastEndOfInput() {
        try (var reader = createLookAheadReader("abc", 2)) {
            assertThrows(UnsupportedOperationException.class, () -> reader.peek(3));
        }
    }

    @Test
    public void shouldNotReadPastEndOfInput() {
        try (var reader = createLookAheadReader("abc", 5)) {
            reader.read();
            reader.read();
            reader.read();
            assertThrows(UnsupportedOperationException.class, reader::read);
        }
    }

    @Test
    public void shouldReadAndPeekRandomized() {
        var n = 1000;
        var lookahead = 10;
        var random = new Random();
        var text = generateRandomString(random, n);
        try (var reader = createLookAheadReader(text, lookahead)) {
            var i = 0;
            while (i < n) {
                var length = random.nextInt(lookahead);
                if (random.nextBoolean()) {
                    for (var j = 0; j < length && i < n; j++, i++) {
                        assertThat(reader.read(), is(equalTo(text.charAt(i))));
                    }
                } else {
                    for (var j = 0; j < length && i + j < n; j++) {
                        assertThat(reader.peek(j + 1), is(equalTo(text.charAt(i + j))));
                    }
                }
            }
            assertThat(reader.canRead(), is(equalTo(false)));
        }
    }

    private static String generateRandomString(Random random, int n) {
        var text = new StringBuilder(n);
        for (var i = 0; i < n; i++) {
            text.append((char) ('a' + random.nextInt(26)));
        }
        return text.toString();
    }

    private static LookAheadReader createLookAheadReader(String text, int lookahead) {
        return new LookAheadReader(new BufferedReader(new StringReader(text)), lookahead);
    }
}