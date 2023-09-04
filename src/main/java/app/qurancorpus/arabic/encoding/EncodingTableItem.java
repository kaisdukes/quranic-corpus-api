package app.qurancorpus.arabic.encoding;

import app.qurancorpus.arabic.CharacterType;
import app.qurancorpus.arabic.DiacriticType;

public record EncodingTableItem(
		CharacterType characterType,
		DiacriticType diacriticType) {
}