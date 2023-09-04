package app.qurancorpus.morphology.segmentation;

import app.qurancorpus.arabic.ArabicText;
import app.qurancorpus.arabic.CharacterType;
import app.qurancorpus.morphology.AspectType;
import app.qurancorpus.morphology.GenderType;
import app.qurancorpus.morphology.MoodType;
import app.qurancorpus.morphology.NumberType;
import app.qurancorpus.morphology.PartOfSpeech;
import app.qurancorpus.morphology.PersonType;
import app.qurancorpus.morphology.Segment;
import app.qurancorpus.orthography.Token;

import static app.qurancorpus.arabic.encoding.buckwalter.BuckwalterEncoder.toBuckwalter;

public class PronounReader {
    private Token token;
    private ArabicText arabicText;

    public int readObjectPronoun(
            Token token,
            Segment stem,
            Segment segment,
            int suffixIndex,
            boolean isFirstObject,
            boolean isEmphaticSuffix) {

        this.token = token;
        this.arabicText = token.arabicText();

        // Person, gender and number.
        var person = segment.getPerson();
        var gender = segment.getGender();
        var number = segment.getNumber();

        // 1S
        if (person == PersonType.First && number == NumberType.Singular) {

            // POS:V
            if (stem.getPartOfSpeech() == PartOfSpeech.Verb) {

                // Noon + Ya
                if (isFirstObject
                        && arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Noon
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Ya) {
                    return 2;
                }

                // Noon + Alif Maksura
                if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Noon
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.AlifMaksura) {
                    return isEmphaticSuffix ? 1 : 2;
                }

                // Noon
                if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Noon) {
                    return 1;
                }

                fail("PRON:1S");
            }

            // not POS:V
            else {

                if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.AlifMaksura
                        && !arabicText.isShadda(suffixIndex - 1)) {

                    // POS:ACC + Noon
                    if (stem.getPartOfSpeech() == PartOfSpeech.Accusative
                            && arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Noon
                            && !arabicText.isShadda(suffixIndex - 2)) {
                        return 2;
                    }
                    return 1;
                }

                // If we only have a diacritic, produce an empty segment.
                if (arabicText.isKasra(suffixIndex - 1)) {
                    return 0;
                }
                if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.AlifMaksura
                        && arabicText.isShadda(suffixIndex - 1)) {
                    return 0;
                }

                // (20:94:2) yabona&um~a
                // (7:150:25) >um~a
                if (token.location().equals(20, 94, 2)
                        || token.location().equals(7, 150, 25)) {
                    return 0;
                }

                // (69:19:9)
                // (69:20:5)
                // (69:26:4)
                // (69:28:4)
                // (69:29:3)
                if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Ya
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Ha) {
                    return 2;
                }

                fail("PRON:1S");
            }
        }

        // 1P
        if (person == PersonType.First && number == NumberType.Plural) {
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Noon
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                if (arabicText.isShadda(suffixIndex - 2)) {
                    return 1;
                }
                return 2;
            }
            fail("PRON:1P");
        }

        // 2D
        if (person == PersonType.Second && number == NumberType.Dual) {
            if (arabicText.getCharacterType(suffixIndex - 3) == CharacterType.Kaf
                    && arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Meem
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                return 3;
            }
            fail("PRON:2D");
        }

        // 2MP
        if (person == PersonType.Second && gender == GenderType.Masculine
                && number == NumberType.Plural) {
            if (isFirstObject
                    && arabicText.getCharacterType(suffixIndex - 3) == CharacterType.Kaf
                    && arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Meem
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Waw) {
                return 3;
            } else if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Kaf
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Meem) {
                return 2;
            }
            fail("PRON:2MP");
        }

        // 2MS
        if (person == PersonType.Second && gender == GenderType.Masculine
                && number == NumberType.Singular) {
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Kaf) {
                return 1;
            }
            fail("PRON:2MS");
        }

        // 2FS
        if (person == PersonType.Second && gender == GenderType.Feminine
                && number == NumberType.Singular) {
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Kaf) {
                return 1;
            }
            fail("PRON:2FS");
        }

        // 2FP
        if (person == PersonType.Second && gender == GenderType.Feminine
                && number == NumberType.Plural) {
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Kaf
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Noon) {
                return 2;
            }
            fail("PRON:2FP");
        }

        // 3D
        if (person == PersonType.Third && number == NumberType.Dual) {
            if (arabicText.getCharacterType(suffixIndex - 3) == CharacterType.Ha
                    && arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Meem
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                return 3;
            }
            fail("PRON:3D");
        }

        // 3MS
        if (person == PersonType.Third && gender == GenderType.Masculine
                && number == NumberType.Singular) {
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Ha) {
                return 1;
            }
            fail("PRON:3MS");
        }

        // 3MP
        if (person == PersonType.Third && gender == GenderType.Masculine
                && number == NumberType.Plural) {
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Ha
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Meem) {
                return 2;
            }
            fail("PRON:3MP");
        }

        // 3FS
        if (person == PersonType.Third && gender == GenderType.Feminine
                && number == NumberType.Singular) {
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Ha
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                return 2;
            }

            // (24:31:75) Ha + Fatha
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Ha
                    && arabicText.isFatha(suffixIndex - 1)) {
                return 1;
            }

            fail("PRON:3FS");
        }

        // 3FP
        if (person == PersonType.Third && gender == GenderType.Feminine
                && number == NumberType.Plural) {
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Ha
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Noon) {
                return 2;
            }
            fail("PRON:3FP");
        }

        // Unknown pronoun.
        fail("PRON");
        return 0;
    }

    public int readSubjectPronoun(
            Token token,
            Segment stem,
            int suffixIndex,
            boolean isObjectAttached) {

        // POS:V
        if (stem.getPartOfSpeech() != PartOfSpeech.Verb) {
            return 0;
        }

        // POS:V PERF
        AspectType aspect = stem.getAspect();
        if (aspect == AspectType.Perfect) {
            return readPerfectVerbSubject(token, stem, suffixIndex,
                    isObjectAttached);
        }

        // POS:V IMPF
        if (aspect == AspectType.Imperfect) {
            return readImperfectVerbSubject(token, stem, suffixIndex,
                    isObjectAttached);
        }

        // POS:V IMPV
        if (aspect == AspectType.Imperative) {
            return readImperativeVerbSubject(token, stem, suffixIndex,
                    isObjectAttached);
        }

        // No match.
        return 0;
    }

    private int readPerfectVerbSubject(
            Token token,
            Segment stem,
            int suffixIndex,
            boolean isObjectAttached) {

        this.token = token;
        this.arabicText = token.arabicText();

        // Person, gender and number.
        var person = stem.getPerson();
        var gender = stem.getGender();
        var number = stem.getNumber();

        // 1S
        if (person == PersonType.First && number == NumberType.Singular) {

            // -tu
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Ta
                    && arabicText.isDamma(suffixIndex - 1)) {
                return 1;
            }
            fail("1S");
        }

        // 1P
        if (person == PersonType.First && number == NumberType.Plural) {

            // -naA
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Noon
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                return 2;
            }
            fail("1P");
        }

        // 2MS
        if (person == PersonType.Second && gender == GenderType.Masculine
                && number == NumberType.Singular) {

            // -ta
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Ta
                    && arabicText.isFatha(suffixIndex - 1)) {
                return 1;
            }
            fail("2MS");
        }

        // 2FS
        if (person == PersonType.Second && gender == GenderType.Feminine
                && number == NumberType.Singular) {

            // -ti
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Ta
                    && arabicText.isKasra(suffixIndex - 1)) {
                return 1;
            }
            fail("2FS");
        }

        // 2D
        if (person == PersonType.Second && number == NumberType.Dual) {

            // -tumaA
            if (arabicText.getCharacterType(suffixIndex - 3) == CharacterType.Ta
                    && arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Meem
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                return 3;
            }
            fail("2D");
        }

        // 2MP
        if (person == PersonType.Second && gender == GenderType.Masculine
                && number == NumberType.Plural) {

            if (isObjectAttached) {

                // -tumuw
                if (arabicText.getCharacterType(suffixIndex - 3) == CharacterType.Ta
                        && arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Meem
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Waw) {
                    return 3;
                }
                fail("2MP");

            } else {

                // -tum
                if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Ta
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Meem) {
                    return 2;
                }
                fail("2MP");
            }
        }

        // 2FP
        if (person == PersonType.Second && gender == GenderType.Feminine
                && number == NumberType.Plural) {

            // -tunna
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Ta
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Noon) {
                return 2;
            }
            fail("2FP");
        }

        // 3MD
        if (person == PersonType.Third && gender == GenderType.Masculine
                && number == NumberType.Dual) {

            // -A
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                return 1;
            }
            fail("3MD");
        }

        // 3FD
        if (person == PersonType.Third && gender == GenderType.Feminine
                && number == NumberType.Dual) {

            // -taA
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Ta
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                return 2;
            }
            fail("3FD");
        }

        // 3MP
        if (person == PersonType.Third && gender == GenderType.Masculine
                && number == NumberType.Plural) {

            // -wA
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Waw
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                return 2;
            }

            // -w
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Waw) {
                return 1;
            }
            fail("3MP");
        }

        // 3FP
        if (person == PersonType.Third && gender == GenderType.Feminine
                && number == NumberType.Plural) {

            // -na
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Noon) {
                return 1;
            }
            fail("3FP");
        }

        // No attached subject pronoun.
        return 0;
    }

    private int readImperfectVerbSubject(
            Token token,
            Segment stem,
            int suffixIndex,
            boolean isObjectAttached) {

        this.token = token;
        this.arabicText = token.arabicText();

        // Person, gender and number.
        var person = stem.getPerson();
        var gender = stem.getGender();
        var number = stem.getNumber();

        // Verb.
        MoodType mood = stem.getMood();

        // 2D, 3D
        if ((person == PersonType.Second || person == PersonType.Third)
                && number == NumberType.Dual) {

            // -A
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                return 1;
            }

            // -An
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Alif
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Noon) {
                return 2;
            }
            fail(person == PersonType.Second ? "2D" : "3D");
        }

        // 2MP, 3MP
        if ((person == PersonType.Second || person == PersonType.Third)
                && gender == GenderType.Masculine
                && number == NumberType.Plural) {

            // MOOD:IND
            if (mood == null || mood == MoodType.Indicative) {

                // -wna
                if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Waw
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Noon) {
                    return 2;
                }

                // -w,na
                if (arabicText.getCharacterType(suffixIndex - 3) == CharacterType.Waw
                        && arabicText.getCharacterType(suffixIndex - 2) == CharacterType.SmallWaw
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Noon) {
                    return 3;
                }

                // -w
                if (isObjectAttached
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Waw) {
                    return 1;
                }
                fail(person == PersonType.Second ? "2MP" : "3MP");
            }

            // MOOD:SUBJ
            if (mood == MoodType.Subjunctive) {

                // -wA@
                if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Waw
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                    return 2;
                }

                // -w,A@
                if (arabicText.getCharacterType(suffixIndex - 3) == CharacterType.Waw
                        && arabicText.getCharacterType(suffixIndex - 2) == CharacterType.SmallWaw
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                    return 2;
                }

                // -w
                if (isObjectAttached
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Waw) {
                    return 1;
                }
                fail(person == PersonType.Second ? "2MP" : "3MP");
            }

            // MOOD:JUS
            if (mood == MoodType.Jussive) {

                // -wna
                if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Waw
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Noon) {
                    return 2;
                }

                // -wA@
                if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Waw
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                    return 2;
                }

                // -w,A@
                if (arabicText.getCharacterType(suffixIndex - 3) == CharacterType.Waw
                        && arabicText.getCharacterType(suffixIndex - 2) == CharacterType.SmallWaw
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                    return 2;
                }

                // -w
                if (isObjectAttached
                        && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Waw) {
                    return 1;
                }
                fail(person == PersonType.Second ? "2MP" : "3MP");
            }
        }

        // 3FP
        if (person == PersonType.Third && gender == GenderType.Feminine
                && number == NumberType.Plural) {

            // -na
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Noon) {
                return 1;
            }
            fail("3FP");
        }

        // 2FP
        if (person == PersonType.Second && gender == GenderType.Feminine
                && number == NumberType.Plural) {

            // -na
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Noon) {
                return 1;
            }
            fail("2FP");
        }

        // No attached subject pronoun.
        return 0;
    }

    private int readImperativeVerbSubject(
            Token token,
            Segment stem,
            int suffixIndex,
            boolean isObjectAttached) {

        this.token = token;
        this.arabicText = token.arabicText();

        // Person, gender and number.
        var person = stem.getPerson();
        var gender = stem.getGender();
        var number = stem.getNumber();

        // 2D
        if (person == PersonType.Second && number == NumberType.Dual) {

            // -A
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                return 1;
            }
            fail("2D");
        }

        // 2FS
        if (person == PersonType.Second && gender == GenderType.Feminine
                && number == NumberType.Singular) {

            // -Y
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.AlifMaksura
                    || arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Ya) {
                return 1;
            }
            fail("2FS");
        }

        // 2MP
        if (person == PersonType.Second && gender == GenderType.Masculine
                && number == NumberType.Plural) {

            // -wA@
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Waw
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                return 2;
            }

            // -w
            if (isObjectAttached
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Waw) {
                return 1;
            }

            // ,^A@
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.SmallWaw
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Alif) {
                return 2;
            }

            // 'uw
            if (arabicText.getCharacterType(suffixIndex - 2) == CharacterType.Hamza
                    && arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Waw) {
                return 2;
            }

            // halum~a
            if (toBuckwalter(token.arabicText()).equals("halum~a")) {
                return 0;
            }

            // Fail.
            fail("2MP");
        }

        // 2FP
        if (person == PersonType.Second && gender == GenderType.Feminine
                && number == NumberType.Plural) {

            // -na
            if (arabicText.getCharacterType(suffixIndex - 1) == CharacterType.Noon) {
                return 1;
            }

            // Fail.
            fail("3FP");
        }

        // No attached subject pronoun.
        return 0;
    }

    private void fail(String feature) {
        throw new UnsupportedOperationException(
                "Failed to produce segments for token: " + token.location()
                        + ' ' + toBuckwalter(token.arabicText())
                        + ", feature: " + feature);
    }
}