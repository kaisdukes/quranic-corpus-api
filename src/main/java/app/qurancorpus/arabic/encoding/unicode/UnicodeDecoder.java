package app.qurancorpus.arabic.encoding.unicode;

import app.qurancorpus.arabic.ArabicText;
import app.qurancorpus.arabic.encoding.ArabicDecoderBase;

import static app.qurancorpus.arabic.encoding.unicode.UnicodeTable.UNICODE_TABLE;

public class UnicodeDecoder extends ArabicDecoderBase {

	public UnicodeDecoder() {
        super(UNICODE_TABLE);
	}

	public static ArabicText fromUnicode(String text) {
		return new UnicodeDecoder().decode(text);
	}
}