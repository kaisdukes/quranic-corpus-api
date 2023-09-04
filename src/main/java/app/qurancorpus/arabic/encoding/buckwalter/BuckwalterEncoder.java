package app.qurancorpus.arabic.encoding.buckwalter;

import app.qurancorpus.arabic.ArabicText;
import app.qurancorpus.arabic.encoding.ArabicEncoderBase;

import static app.qurancorpus.arabic.encoding.buckwalter.BuckwalterTable.BUCKWALTER_TABLE;

public class BuckwalterEncoder extends ArabicEncoderBase {

	public BuckwalterEncoder() {
        super(BUCKWALTER_TABLE);
	}

	public static String toBuckwalter(ArabicText arabicText) {
		return new BuckwalterEncoder().encode(arabicText, null);
	}
}