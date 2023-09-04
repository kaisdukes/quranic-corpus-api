package app.qurancorpus.arabic.encoding.buckwalter;

import app.qurancorpus.arabic.ArabicText;
import app.qurancorpus.arabic.encoding.ArabicDecoderBase;

import static app.qurancorpus.arabic.encoding.buckwalter.BuckwalterTable.BUCKWALTER_TABLE;

public class BuckwalterDecoder extends ArabicDecoderBase {

	public BuckwalterDecoder() {
        super(BUCKWALTER_TABLE);
	}

	public static ArabicText fromBuckwalter(String text) {
		return new BuckwalterDecoder().decode(text);
	}
}