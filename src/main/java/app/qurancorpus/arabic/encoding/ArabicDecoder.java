package app.qurancorpus.arabic.encoding;

import app.qurancorpus.arabic.ArabicText;

public interface ArabicDecoder {

	ArabicText decode(String text);
}