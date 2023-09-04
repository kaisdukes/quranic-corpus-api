package app.qurancorpus.arabic.encoding;

import app.qurancorpus.arabic.ArabicText;
import app.qurancorpus.arabic.CharacterType;
import app.qurancorpus.arabic.encoding.unicode.UnicodeType;

public abstract class ArabicEncoderBase {
    private boolean isMaddah;
    private boolean isHamzaAbove;
    private final EncodingTableBase encodingTable;
    private EncodingOptions options;
    private final StringBuilder text = new StringBuilder();

    protected ArabicEncoderBase(EncodingTableBase encodingTable) {
        this.encodingTable = encodingTable;
    }

    public String encode(ArabicText arabicText, EncodingOptions options) {
        text.setLength(0);
        this.options = options;

        var characterCount = arabicText.getLength();
        for (var i = 0; i < characterCount; i++) {
            encodeCharacter(arabicText, i);
        }
        return text.toString();
    }

    private void encodeCharacter(ArabicText arabicText, int index) {
        if (arabicText.getCharacterType(index) == null) {
            text.append(' ');
        } else {
            text.append(getCharacter(arabicText, index));
            writeDiacritics(arabicText, index);
        }
    }

    private char getCharacter(ArabicText arabicText, int index) {
        var characterType = arabicText.getCharacterType(index);
        UnicodeType unicodeType = null;
        isMaddah = arabicText.isMaddah(index);
        isHamzaAbove = arabicText.isHamzaAbove(index);

        if (options == EncodingOptions.CombineAlifWithMaddah
                && characterType == CharacterType.Alif
                && arabicText.isMaddah(index)) {
            unicodeType = UnicodeType.AlifWithMaddah;
            isMaddah = false;
        } else if (characterType == CharacterType.Alif && isHamzaAbove
                && !arabicText.isAlifKhanjareeya(index)) {
            unicodeType = UnicodeType.AlifWithHamzaAbove;
            isHamzaAbove = false;
        } else if (characterType == CharacterType.Waw && isHamzaAbove) {
            unicodeType = UnicodeType.WawWithHamzaAbove;
            isHamzaAbove = false;
        } else if (characterType == CharacterType.Alif
                && arabicText.isHamzaBelow(index)) {
            unicodeType = UnicodeType.AlifWithHamzaBelow;
        } else if (characterType == CharacterType.Ya && isHamzaAbove) {
            unicodeType = UnicodeType.YaWithHamzaAbove;
            isHamzaAbove = false;
        } else if (characterType == CharacterType.Alif && arabicText.isAlifKhanjareeya(index)) {
            unicodeType = UnicodeType.AlifKhanjareeya;
        } else if (characterType == CharacterType.Alif && arabicText.isHamzatWasl(index)) {
            unicodeType = UnicodeType.AlifWithHamzatWasl;
        }

        return unicodeType != null
                ? encodingTable.getCharacter(unicodeType)
                : encodingTable.getCharacter(characterType);
    }

    private void writeDiacritics(ArabicText arabicText, int index) {
        if (isHamzaAbove) {
            text.append(encodingTable.getCharacter(UnicodeType.HamzaAbove));
        }

        if (arabicText.isShadda(index)) {
            text.append(encodingTable.getCharacter(UnicodeType.Shadda));
        }

        if (arabicText.isFathatan(index)) {
            text.append(encodingTable.getCharacter(UnicodeType.Fathatan));
        }

        if (arabicText.isDammatan(index)) {
            text.append(encodingTable.getCharacter(UnicodeType.Dammatan));
        }

        if (arabicText.isKasratan(index)) {
            text.append(encodingTable.getCharacter(UnicodeType.Kasratan));
        }

        if (arabicText.isFatha(index)) {
            text.append(encodingTable.getCharacter(UnicodeType.Fatha));
        }

        if (arabicText.isDamma(index)) {
            text.append(encodingTable.getCharacter(UnicodeType.Damma));
        }

        if (arabicText.isKasra(index)) {
            text.append(encodingTable.getCharacter(UnicodeType.Kasra));
        }

        if (arabicText.isSukun(index)) {
            text.append(encodingTable.getCharacter(UnicodeType.Sukun));
        }

        if (arabicText.getCharacterType(index) == CharacterType.AlifMaksura && arabicText.isAlifKhanjareeya(index)) {
            text.append(encodingTable.getCharacter(UnicodeType.AlifKhanjareeya));
        }

        if (isMaddah) {
            text.append(encodingTable.getCharacter(UnicodeType.Maddah));
        }
    }
}