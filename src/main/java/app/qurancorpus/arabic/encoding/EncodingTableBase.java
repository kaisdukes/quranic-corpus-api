package app.qurancorpus.arabic.encoding;

import java.util.HashMap;
import java.util.Map;

import app.qurancorpus.arabic.CharacterType;
import app.qurancorpus.arabic.DiacriticType;
import app.qurancorpus.arabic.encoding.unicode.UnicodeType;

import static app.qurancorpus.arabic.CharacterType.CHARACTER_TYPES;
import static app.qurancorpus.arabic.encoding.unicode.UnicodeType.UNICODE_TYPES;

public abstract class EncodingTableBase {
    private final Map<Character, EncodingTableItem> unicodeMap = new HashMap<>();
    private final char[] characterList = new char[CHARACTER_TYPES.length];
    private final char[] unicodeList = new char[UNICODE_TYPES.length];

    protected EncodingTableBase() {
    }

    public EncodingTableItem getItem(char unicode) {
        return unicodeMap.get(unicode);
    }

    public char getCharacter(CharacterType characterType) {
        return characterList[characterType.ordinal()];
    }

    public char getCharacter(UnicodeType unicodeType) {
        return unicodeList[unicodeType.ordinal()];
    }

    protected void addItem(UnicodeType unicodeType, char ch, CharacterType characterType) {
        addItem(unicodeType, ch, characterType, null);
    }

    protected void addItem(UnicodeType unicodeType, char ch, DiacriticType diacriticType) {
        addItem(unicodeType, ch, null, diacriticType);
    }

    protected void addItem(
            UnicodeType unicodeType,
            char ch,
            CharacterType characterType,
            DiacriticType diacriticType) {

        var item = new EncodingTableItem(characterType, diacriticType);
        unicodeMap.put(ch, item);

        if (characterType != null && diacriticType == null) {
            characterList[characterType.ordinal()] = ch;
        }

        if (unicodeType != null) {
            unicodeList[unicodeType.ordinal()] = ch;
        }
    }
}