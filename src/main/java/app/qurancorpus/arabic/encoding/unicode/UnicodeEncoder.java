package app.qurancorpus.arabic.encoding.unicode;

import app.qurancorpus.arabic.ArabicText;
import app.qurancorpus.arabic.encoding.ArabicEncoderBase;
import app.qurancorpus.arabic.encoding.EncodingOptions;

import static app.qurancorpus.arabic.encoding.unicode.UnicodeTable.UNICODE_TABLE;

public class UnicodeEncoder extends ArabicEncoderBase {

    public UnicodeEncoder() {
        super(UNICODE_TABLE);
    }

    public static String toUnicode(ArabicText arabicText) {
        return toUnicode(arabicText, null);
    }

    public static String toUnicode(ArabicText arabicText, EncodingOptions options) {
        return new UnicodeEncoder().encode(arabicText, options);
    }
}