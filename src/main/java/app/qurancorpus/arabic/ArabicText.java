package app.qurancorpus.arabic;

import static app.qurancorpus.arabic.CharacterType.CHARACTER_TYPES;

public class ArabicText {
    public static final int[] DIACRITIC_OFFSETS = {1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2};
    public static final int[] DIACRITIC_MASKS = {1, 2, 4, 8, 16, 32, 64, 128, 1, 2, 4, 8, 16};
    public static final int CHARACTER_WIDTH = 3;
    public static final byte WHITESPACE = -1;

    private final byte[] buffer;
    private final int offset;
    private final int characterCount;

    public ArabicText(byte[] buffer) {
        this(buffer, 0, buffer.length / CHARACTER_WIDTH);
    }

    public ArabicText(byte[] buffer, int offset, int characterCount) {
        this.buffer = buffer;
        this.offset = offset;
        this.characterCount = characterCount;
    }

    public int getLength() {
        return characterCount;
    }

    public CharacterType getCharacterType(int index) {
        var value = buffer[getOffset(index)];
        return value != WHITESPACE ? CHARACTER_TYPES[value] : null;
    }

    public boolean isFatha(int index) {
        return (buffer[getOffset(index) + 1] & 1) != 0;
    }

    public boolean isDamma(int index) {
        return (buffer[getOffset(index) + 1] & 2) != 0;
    }

    public boolean isKasra(int index) {
        return (buffer[getOffset(index) + 1] & 4) != 0;
    }

    public boolean isFathatan(int index) {
        return (buffer[getOffset(index) + 1] & 8) != 0;
    }

    public boolean isDammatan(int index) {
        return (buffer[getOffset(index) + 1] & 16) != 0;
    }

    public boolean isKasratan(int index) {
        return (buffer[getOffset(index) + 1] & 32) != 0;
    }

    public boolean isShadda(int index) {
        return (buffer[getOffset(index) + 1] & 64) != 0;
    }

    public boolean isSukun(int index) {
        return (buffer[getOffset(index) + 1] & 128) != 0;
    }

    public boolean isMaddah(int index) {
        return (buffer[getOffset(index) + 2] & 1) != 0;
    }

    public boolean isHamzaAbove(int index) {
        return (buffer[getOffset(index) + 2] & 2) != 0;
    }

    public boolean isHamzaBelow(int index) {
        return (buffer[getOffset(index) + 2] & 4) != 0;
    }

    public boolean isHamzatWasl(int index) {
        return (buffer[getOffset(index) + 2] & 8) != 0;
    }

    public boolean isAlifKhanjareeya(int index) {
        return (buffer[getOffset(index) + 2] & 16) != 0;
    }

    public int getDiacriticCount(int index) {
        var count = 0;
        for (var i = 0; i < 13; i++) {
            if ((buffer[getOffset(index) + DIACRITIC_OFFSETS[i]] & DIACRITIC_MASKS[i]) != 0) {
                count++;
            }
        }
        return count;
    }

    public boolean isLetter(int index) {
        return buffer[getOffset(index)] <= CharacterType.Tatweel.ordinal();
    }

    public ArabicText substring(int start, int end) {
        return new ArabicText(buffer, offset + start * CHARACTER_WIDTH, end - start);
    }

    public ArabicText removeDiacritics() {
        var buffer = new byte[characterCount * CHARACTER_WIDTH];
        var offset1 = 0;
        var offset2 = offset;
        for (var i = 0; i < characterCount; i++) {
            buffer[offset1] = this.buffer[offset2];
            offset1 += CHARACTER_WIDTH;
            offset2 += CHARACTER_WIDTH;
        }
        return new ArabicText(buffer, 0, characterCount);
    }

    private int getOffset(int index) {
        if (index < 0 || index >= characterCount) {
            throw new UnsupportedOperationException(
                    "Arabic text index of bounds" + ": index=" + index + ", size=" + characterCount);
        }
        return offset + index * CHARACTER_WIDTH;
    }
}