package app.qurancorpus.arabic;

import static app.qurancorpus.arabic.ArabicText.*;
import static java.lang.System.arraycopy;

public class ArabicTextBuilder {

    private static final int INITIAL_CAPACITY = 4;
    private byte[] buffer;
    private int characterCount;
    private int characterCapacity;

    public ArabicTextBuilder() {
        this(INITIAL_CAPACITY);
    }

    public ArabicTextBuilder(int characterCapacity) {
        this.characterCapacity = characterCapacity;
        buffer = new byte[characterCapacity * CHARACTER_WIDTH];
    }

    public void add(CharacterType characterType) {
        insert(characterCount, characterType);
    }

    public void add(DiacriticType diacriticType) {
        setDiacritic(characterCount - 1, diacriticType);
    }

    public void addWhitespace() {
        add((CharacterType) null);
    }

    public void setDiacritic(int index, DiacriticType diacriticType) {
        var offset = index * CHARACTER_WIDTH;
        var value = diacriticType.ordinal();
        buffer[offset + DIACRITIC_OFFSETS[value]] |= DIACRITIC_MASKS[value];
    }

    public void insert(int index, CharacterType characterType) {

        // Check the buffer.
        checkCapacity(1);

        // Move subsequent characters.
        var offset = index * CHARACTER_WIDTH;
        if (index < characterCount - 1) {
            arraycopy(buffer, offset, buffer, offset + CHARACTER_WIDTH, CHARACTER_WIDTH);
        }

        // Insert the character into the buffer.
        buffer[offset] = characterType != null ? (byte) characterType.ordinal() : WHITESPACE;
        buffer[offset + 1] = 0;
        buffer[offset + 2] = 0;

        // Increment character count.
        characterCount++;
    }

    public ArabicText toArabicText() {
        return new ArabicText(toByteArray());
    }

    private byte[] toByteArray() {
        var buffer = this.buffer;
        var byteCount = characterCount * CHARACTER_WIDTH;
        if (byteCount != buffer.length) {
            buffer = new byte[byteCount];
            arraycopy(this.buffer, 0, buffer, 0, byteCount);
        }
        return buffer;
    }

    private void checkCapacity(int addCharacterCount) {
        var expectedCapacity = characterCount + addCharacterCount;
        if (expectedCapacity > characterCapacity) {
            var newCapacity = Math.max(expectedCapacity, characterCapacity * 2);
            var newBuffer = new byte[newCapacity * CHARACTER_WIDTH];
            arraycopy(buffer, 0, newBuffer, 0, characterCapacity * CHARACTER_WIDTH);
            buffer = newBuffer;
            characterCapacity = newCapacity;
        }
    }
}