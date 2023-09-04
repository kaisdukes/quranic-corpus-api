package app.qurancorpus.arabic.encoding.phonetic;

import app.qurancorpus.arabic.ArabicText;
import app.qurancorpus.arabic.CharacterType;
import app.qurancorpus.morphology.PartOfSpeech;

import static app.qurancorpus.morphology.Morphology.getStem;

public class PhoneticEncoder {
    private ArabicText arabicText;
    private int index;
    private int nextIndex;
    private int lastIndex;
    private StringBuilder phonetic;

    public static String toPhonetic(PhoneticContext context, ArabicText arabicText) {
        return new PhoneticEncoder().encode(context, arabicText);
    }

    public String encode(PhoneticContext context, ArabicText arabicText) {
        this.arabicText = arabicText;
        phonetic = new StringBuilder();
        var isLastDeterminer = false;

        // Encode each letter with diacritics.
        var characterCount = arabicText.getLength();
        for (index = 0; index < characterCount; index++) {

            // Whitespace is passed through.
            if (arabicText.getCharacterType(index) == null) {
                phonetic.append(' ');
                continue;
            }

            // Context.
            nextIndex = index < characterCount - 1 && arabicText.getCharacterType(index + 1) != null
                    ? index + 1
                    : -1;
            lastIndex = index > 0 && arabicText.getCharacterType(index - 1) != null
                    ? index - 1
                    : -1;

            // Encode special tokens.
            var token = context.token();
            if (token != null) {
                var morphologyGraph = context.morphologyGraph();
                var segments = morphologyGraph.query(token);
                var partOfSpeech = getStem(segments).getPartOfSpeech();
                var phonemes = encodeToken(partOfSpeech);
                if (phonemes != null) {
                    phonetic.append(phonemes);
                    continue;
                }

                // (Alif + Hamzat Wasl) + (Lam) -> Al (determiner)
                if (arabicText.getCharacterType(index) == CharacterType.Alif
                        && arabicText.isHamzatWasl(index)
                        && arabicText.getCharacterType(nextIndex) == CharacterType.Lam) {

                    if (partOfSpeech != PartOfSpeech.Demonstrative
                            && partOfSpeech != PartOfSpeech.Relative
                            && partOfSpeech != PartOfSpeech.Conditional) {

                        // al-la
                        if (arabicText.getCharacterType(nextIndex) == CharacterType.Lam && arabicText.isShadda(nextIndex)) {
                            addPhoneme("al-la");
                        }

                        // al
                        else if (token.location().tokenNumber() == 1 && lastIndex < 0) {
                            addPhoneme("al-");
                        }

                        // la
                        else {
                            addPhoneme("l-");
                        }

                        // l-
                        isLastDeterminer = true;
                        index++;
                        continue;
                    }
                }
            }

            // Encode letter.
            var phoneme = encodeLetter();
            if (phoneme != null) {
                addPhoneme(phoneme);

                // Stress is indicated by a double phoneme.
                if (arabicText.isShadda(index)
                        && phoneme.length() == 1
                        && lastIndex >= 0
                        && (arabicText.getCharacterType(lastIndex) != arabicText.getCharacterType(index)
                        || arabicText.getDiacriticCount(lastIndex) != 0)) {
                    if (!isLastDeterminer) {
                        addPhoneme(phoneme);
                    }
                }
            }

            // Encode diacritics.
            phoneme = encodeDiacritics();
            if (phoneme != null) {
                addPhoneme(phoneme);
            }
        }

        return phonetic.toString();
    }

    private void addPhoneme(String phoneme) {

        // First phoneme?
        var size = phonetic.length();
        if (size == 0) {
            phonetic.append(phoneme);
            return;
        }

        // Double vowel?
        if (isVowel(phonetic.charAt(size - 1)) && isVowel(phoneme.charAt(0))) {
            phonetic.append('-');
        }

        // Add phoneme.
        phonetic.append(phoneme);
    }

    private boolean isVowel(char ch) {
        return ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u';
    }

    private String encodeToken(PartOfSpeech partOfSpeech) {
        if (partOfSpeech != PartOfSpeech.Initials) {
            return null;
        }
        var length = arabicText.getLength();
        var phonetic = new StringBuilder();
        for (var i = 0; i < length; i++) {
            if (i > 0) {
                phonetic.append(' ');
            }
            phonetic.append(arabicText.getCharacterType(i).getPhoneticName());
        }
        index = length - 1;
        return phonetic.toString();
    }

    private String encodeLetter() {
        String phoneme = null;
        var characterType = arabicText.getCharacterType(index);

        // Encode letter.
        switch (characterType) {
            case Ba -> phoneme = Phoneme.B;
            case Ta, TaMarbuta -> phoneme = Phoneme.T;
            case TTa -> phoneme = Phoneme.TTa;
            case Tha -> phoneme = Phoneme.TH;
            case Thal -> phoneme = Phoneme.DH;
            case DTha -> phoneme = Phoneme.DTha;
            case Jeem -> phoneme = Phoneme.J;
            case HHa -> phoneme = Phoneme.HHa;
            case Ha -> phoneme = Phoneme.H;
            case Kha -> phoneme = Phoneme.KH;
            case Dal -> phoneme = Phoneme.D;
            case DDad -> phoneme = Phoneme.DDa;
            case Ra -> phoneme = Phoneme.R;
            case Zain -> phoneme = Phoneme.Z;
            case Seen -> phoneme = Phoneme.S;
            case Sad -> phoneme = Phoneme.Sad;
            case Sheen -> phoneme = Phoneme.SH;
            case Ain -> phoneme = Phoneme.AA;
            case Ghain -> phoneme = Phoneme.GH;
            case Fa -> phoneme = Phoneme.F;
            case Qaf -> phoneme = Phoneme.Q;
            case Kaf -> phoneme = Phoneme.K;
            case Lam -> {
                if (lastIndex < 0 && arabicText.getCharacterType(nextIndex) == CharacterType.Tatweel) {

                    // (26:176:3), (38:13:5)
                    phoneme = Phoneme.FATHA + Phoneme.L;

                } else {
                    phoneme = Phoneme.L;
                }
            }
            case Meem -> phoneme = Phoneme.M;
            case Noon, SmallHighNoon -> phoneme = Phoneme.N;
            case SmallYa -> {
                if ((nextIndex >= 0 && arabicText.getCharacterType(nextIndex) == CharacterType.AlifMaksura)
                        || arabicText.isFatha(index)) {
                    phoneme = Phoneme.Y;
                }
            }
            case EmptyCentreLowStop -> phoneme = Phoneme.FATHA;
            case Alif -> {
                if (arabicText.isHamzatWasl(index)) {
                    phoneme = encodeHamzatWasl();
                }
            }
            case Waw -> {
                if (!arabicText.isHamzaAbove(index)) {
                    phoneme = encodeWaw();
                }
            }
            case Ya -> {
                if (!arabicText.isHamzaAbove(index)
                        && (nextIndex < 0 || arabicText.getCharacterType(nextIndex) != CharacterType.SmallHighRoundedZero)) {
                    phoneme = encodeYa();
                }
            }
            case AlifMaksura -> phoneme = encodeAlifMaksura();
            case Hamza -> {

                // (27:25:6)
                if (arabicText.isFatha(index) && lastIndex >= 0 && arabicText.isSukun(lastIndex) && nextIndex < 0) {
                    phoneme = Phoneme.FATHA;
                }
            }
        }

        // Return letter.
        return phoneme;
    }

    private String encodeHamzatWasl() {
        String phoneme;

        // (49:11:30)
        if (index >= 2 && arabicText.isHamzatWasl(index - 2)) {
            phoneme = null;
        }

        // Alif + Lam -> Al
        else if (nextIndex >= 0 && arabicText.getCharacterType(nextIndex) == CharacterType.Lam) {
            phoneme = Phoneme.FATHA;
        }

        // Damma if third letter of verb has a damma.
        else if (arabicText.isDamma(index + 2)
                && (index + 3 >= arabicText.getLength()
                || arabicText.getCharacterType(index + 3) != CharacterType.Waw)) {
            phoneme = Phoneme.DAMMA;
        }

        // Otherwise default is kasra.
        else {
            phoneme = Phoneme.KASRA;
        }

        // Return hamzat wasl.
        return phoneme;
    }

    private String encodeWaw() {

        // Default is no phoneme.
        String phoneme = null;

        // Short Damma.
        if (nextIndex >= 0 && arabicText.getCharacterType(nextIndex) == CharacterType.SmallHighRoundedZero) {
            phoneme = Phoneme.DAMMA;
        }

        // W phoneme.
        else if (!isLongWaw(index)
                || (lastIndex >= 0 && arabicText.isHamzaAbove(lastIndex) && arabicText.isFatha(lastIndex))) {
            phoneme = Phoneme.W;
        }

        // Long Damma.
        else if (!arabicText.isFatha(lastIndex)) {
            phoneme = Phoneme.LONG_DAMMA;
        }

        // Waw + Alif + SmallHighRoundedZero -> aw
        else if (index < arabicText.getLength() - 2
                && arabicText.getCharacterType(nextIndex) == CharacterType.Alif
                && arabicText.getCharacterType(index + 2) == CharacterType.SmallHighRoundedZero) {
            phoneme = Phoneme.W;
        }

        // Return waw.
        return phoneme;
    }

    private String encodeYa() {
        return isLongYa(index)
                && (nextIndex < 0 || arabicText.getCharacterType(nextIndex) != CharacterType.Ya)
                ? Phoneme.LONG_KASRA
                : Phoneme.Y;
    }

    private String encodeAlifMaksura() {

        // Default is no phoneme.
        String phoneme = null;

        // If we have diacritics, this is a Y.
        if (arabicText.isFatha(index)
                || arabicText.isKasra(index)
                || arabicText.isDamma(index)
                || arabicText.isKasratan(index)
                || arabicText.isDammatan(index)
                || arabicText.isSukun(index)) {
            phoneme = Phoneme.Y;
        }

        // Kasra attached to an Alif Maksura indicates a long Kasra phoneme,
        // except at (6:34:21) where we have a small high rounded zero.
        else if (lastIndex >= 0
                && arabicText.isKasra(lastIndex)
                && (nextIndex < 0 || arabicText.getCharacterType(nextIndex) != CharacterType.SmallHighRoundedZero)) {
            phoneme = Phoneme.LONG_KASRA;
        }

        // Return Alif Maksura.
        return phoneme;
    }

    private String encodeDiacritics() {

        // Long fatha.
        if (isLongFatha(lastIndex, index)) {
            return Phoneme.LONG_FATHA;
        }

        // Fatha.
        String phoneme = null;
        if (arabicText.isFatha(index) // (41:44:9)
                || (arabicText.getCharacterType(index) == CharacterType.Alif
                && nextIndex >= 0
                && arabicText.getCharacterType(nextIndex) == CharacterType.RoundedHighStopWithFilledCentre)) {
            if (!isLongFatha(index, nextIndex)) {
                phoneme = Phoneme.FATHA;
            }
        }

        // Damma.
        else if (arabicText.isDamma(index) && !isLongWaw(nextIndex)) {
            phoneme = encodeDamma();
        }

        // Kasra.
        else if (arabicText.isKasra(index) && !isLongYa(nextIndex)) {
            phoneme = encodeKasra();
        }

        // Fathatan.
        else if (arabicText.isFathatan(index)) {
            phoneme = Phoneme.FATHATAN;
        }

        // Dammatan.
        else if (arabicText.isDammatan(index)) {
            phoneme = encodeDammatan();
        }

        // Kasratan.
        else if (arabicText.isKasratan(index)) {
            phoneme = Phoneme.KASRATAN;
        }

        // Sukun.
        else if (arabicText.isSukun(index)
                && (lastIndex < 0 || !arabicText.isFatha(lastIndex))
                && nextIndex >= 0) {
            phoneme = Phoneme.SUKUN;
        }

        // Return diacritrics.
        return phoneme;
    }

    private boolean isLongFatha(int lastIndex, int index) {

        // Null
        if (index < 0) {
            return false;
        }

        // First maddah.
        if (lastIndex < 0 && arabicText.isMaddah(index)) {
            return true;
        }

        // Fathatan.
        if (lastIndex < 0 || arabicText.isFathatan(lastIndex)) {
            return false;
        }

        // Fatha.
        if (!arabicText.isFatha(lastIndex)) {
            return false;
        }

        // Alif or Alif maksura.
        if (arabicText.getCharacterType(index) == CharacterType.Alif
                || arabicText.getCharacterType(index) == CharacterType.AlifMaksura) {
            if (arabicText.getDiacriticCount(index) == 0 || arabicText.isMaddah(index)) {
                return true;
            }

            // Alif Khanjareeya.
            if (arabicText.isAlifKhanjareeya(index)) {
                return arabicText.getDiacriticCount(index) == 1 || arabicText.isMaddah(index);
            }
        }

        return false;
    }

    private String encodeDamma() {
        String phoneme;

        // (81:8:2)
        if (arabicText.getCharacterType(index) == CharacterType.Hamza && nextIndex >= 0
                && arabicText.getCharacterType(nextIndex) == CharacterType.SmallWaw) {
            phoneme = Phoneme.LONG_DAMMA;
        }

        // (17:7:12)
        else if (nextIndex >= 0 && arabicText.getCharacterType(nextIndex) == CharacterType.SmallWaw
                && index + 2 < arabicText.getLength()
                && arabicText.getCharacterType(index + 2) == CharacterType.Tatweel) {
            phoneme = Phoneme.LONG_DAMMA;
        }

        // Short damma.
        else if (arabicText.getCharacterType(index) == CharacterType.Alif
                || arabicText.getCharacterType(index) == CharacterType.Hamza
                || arabicText.isHamzaAbove(index)) {
            phoneme = Phoneme.DAMMA;
        }

        // Long damma.
        else if (arabicText.getCharacterType(index) == CharacterType.Waw
                && nextIndex >= 0
                && (arabicText.getCharacterType(nextIndex) == CharacterType.SmallWaw
                || arabicText.getCharacterType(nextIndex) == CharacterType.Alif)) {
            phoneme = Phoneme.LONG_DAMMA;
        }

        // Damma.
        else {
            phoneme = Phoneme.DAMMA;
        }

        // Return Damma.
        return phoneme;
    }

    private String encodeKasra() {
        String phoneme;

        // (106:2:1)
        if (arabicText.isHamzaBelow(index)
                && nextIndex >= 0
                && arabicText.getCharacterType(nextIndex) == CharacterType.SmallYa) {
            phoneme = Phoneme.LONG_KASRA;
        }

        // (89:23:1)
        else if (nextIndex >= 0
                && index + 3 < arabicText.getLength()
                && arabicText.getCharacterType(index + 2) == CharacterType.SmallHighRoundedZero
                && arabicText.getCharacterType(index + 3) == CharacterType.AlifMaksura) {
            phoneme = Phoneme.LONG_KASRA;
        }

        // lamuhyee, nuhyee, tuhyee, waohyee, wayuhyee, yuhyee
        else if (arabicText.getCharacterType(index) == CharacterType.AlifMaksura
                && arabicText.getCharacterType(lastIndex) == CharacterType.HHa
                && arabicText.isSukun(lastIndex)
                && !arabicText.isFatha(index - 2)) {
            phoneme = Phoneme.LONG_KASRA;
        }

        // Small Ya indicates a long Kasra.
        else if (nextIndex >= 0
                && arabicText.getCharacterType(nextIndex) == CharacterType.SmallYa
                && (arabicText.getCharacterType(index) == CharacterType.Ya
                || arabicText.getCharacterType(index) == CharacterType.AlifMaksura
                || (arabicText.getCharacterType(index) == CharacterType.Ha
                && index + 2 < arabicText.getLength() && arabicText
                .getCharacterType(index + 2) == CharacterType.Meem))) {
            phoneme = Phoneme.LONG_KASRA;
        }

        // Kasra.
        else {
            phoneme = Phoneme.KASRA;
        }

        // Return Kasra.
        return phoneme;
    }

    private String encodeDammatan() {
        String phoneme;

        // Hamza indicates short Dammatan.
        if (arabicText.getCharacterType(index) == CharacterType.Hamza
                || arabicText.isHamzaAbove(index)) {
            phoneme = Phoneme.SHORT_DAMMATAN;
        }

        // Dammatan.
        else {
            phoneme = Phoneme.DAMMATAN;
        }

        // Return Dammatan.
        return phoneme;
    }

    private boolean isLongYa(int index) {
        return index >= 0
                && (arabicText.getCharacterType(index) == CharacterType.Ya
                || arabicText.getCharacterType(index) == CharacterType.AlifMaksura)
                && (arabicText.getDiacriticCount(index) == 0 || arabicText.isMaddah(index))
                && !(this.index + 2 < arabicText.getLength()
                && arabicText.getCharacterType(this.index + 2) == CharacterType.SmallHighRoundedZero);
    }

    private boolean isLongWaw(int index) {
        return index >= 0 && arabicText.getCharacterType(index) == CharacterType.Waw
                && (arabicText.getDiacriticCount(index) == 0 || arabicText.isMaddah(index));
    }
}