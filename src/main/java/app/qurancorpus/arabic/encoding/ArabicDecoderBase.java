package app.qurancorpus.arabic.encoding;

import app.qurancorpus.arabic.ArabicText;
import app.qurancorpus.arabic.ArabicTextBuilder;
import app.qurancorpus.arabic.CharacterType;
import app.qurancorpus.arabic.DiacriticType;

public abstract class ArabicDecoderBase implements ArabicDecoder {
    private final EncodingTableBase encodingTable;
    private final ArabicTextBuilder builder = new ArabicTextBuilder();
    private CharacterType lastCharacter;

    protected ArabicDecoderBase(EncodingTableBase encodingTable) {
        this.encodingTable = encodingTable;
    }

    public ArabicText decode(String text) {
        var size = text.length();
        for (var i = 0; i < size; i++) {
            decode(text.charAt(i));
        }
        return builder.toArabicText();
    }

    private void decode(char ch) {

        // Look up character type and diacritic type.
        var item = encodingTable.getItem(ch);
        if (item != null) {
            var characterType = item.characterType();
            var diacriticType = item.diacriticType();

            // If an Alif Khanjareeya is not attached to an Alif Maksura, then
            // promote the diacritic to a full letter.
            if (diacriticType == DiacriticType.AlifKhanjareeya && lastCharacter != CharacterType.AlifMaksura) {
                characterType = CharacterType.Alif;
            }

            // Add character.
            if (characterType != null) {
                builder.add(characterType);
                lastCharacter = characterType;
            }

            // Add diacritic.
            if (diacriticType != null) {
                builder.add(diacriticType);
            }

        } else {

            // Treat any unknown characters as whitespace.
            builder.addWhitespace();
        }
    }
}