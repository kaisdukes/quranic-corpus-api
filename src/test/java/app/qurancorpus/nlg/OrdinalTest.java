package app.qurancorpus.nlg;

import org.junit.jupiter.api.Test;

import static app.qurancorpus.nlg.Ordinal.getLongName;
import static app.qurancorpus.nlg.Ordinal.getShortName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class OrdinalTest {

    @Test
    public void testShortNames() {

        var ordinals = new String[]{
                "0th",
                "1st",
                "2nd",
                "3rd",
                "4th",
                "5th",
                "6th",
                "7th",
                "8th",
                "9th",
                "10th",
                "11th",
                "12th",
                "13th",
                "14th",
                "15th",
                "16th",
                "17th",
                "18th",
                "19th",
                "20th",
                "21st",
                "22nd",
                "23rd",
                "24th",
                "25th",
                "26th",
                "27th",
                "28th",
                "29th",
                "30th",
                "31st",
                "32nd",
                "33rd"
        };

        for (var i = 0; i < ordinals.length; i++) {
            assertThat(getShortName(i), is(equalTo(ordinals[i])));
        }

        assertThat(getShortName(100), is(equalTo("100th")));
        assertThat(getShortName(101), is(equalTo("101st")));
        assertThat(getShortName(102), is(equalTo("102nd")));
        assertThat(getShortName(103), is(equalTo("103rd")));
        assertThat(getShortName(104), is(equalTo("104th")));
        assertThat(getShortName(113), is(equalTo("113th")));
        assertThat(getShortName(123), is(equalTo("123rd")));
    }

    @Test
    public void testLongNames() {

        var ordinals = new String[]{
                "zeroth",
                "first",
                "second",
                "third",
                "fourth",
                "fifth",
                "sixth",
                "seventh",
                "eighth",
                "ninth",
                "tenth",
                "eleventh",
                "twelfth",
                "thirteenth",
                "fourteenth",
                "fifteenth",
                "sixteenth",
                "seventeenth",
                "eighteenth",
                "nineteenth",
                "twentieth",
                "21st",
                "22nd",
                "23rd",
                "24th"
        };

        for (var i = 0; i < ordinals.length; i++) {
            assertThat(getLongName(i), is(equalTo(ordinals[i])));
        }
    }
}