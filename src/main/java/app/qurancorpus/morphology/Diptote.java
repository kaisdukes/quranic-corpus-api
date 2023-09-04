package app.qurancorpus.morphology;

import java.util.HashSet;
import java.util.Set;

import static app.qurancorpus.morphology.CaseType.Genitive;
import static app.qurancorpus.morphology.NumberType.Plural;

public class Diptote {
    private static final Set<String> LEMMAS = new HashSet<>();

    static {
        LEMMAS.add("jahan~am");
        LEMMAS.add("<isoraA}iyl");
        LEMMAS.add("<iboraAhiym");
        LEMMAS.add("firoEawon");
        LEMMAS.add("<isoHaAq");
        LEMMAS.add("sulayoma`n");
        LEMMAS.add("vamuwd");
        LEMMAS.add("yuwsuf");
        LEMMAS.add("yaEoquwb");
        LEMMAS.add("maroyam");
        LEMMAS.add("daAwud");
        LEMMAS.add("madoyan");
        LEMMAS.add("A^dam");
        LEMMAS.add("ha`ruwn");
        LEMMAS.add("<isomaAEiyl");
        LEMMAS.add("qa`ruwn");
        LEMMAS.add(">ay~uwb");
        LEMMAS.add("yuwnus");
        LEMMAS.add("Eimora`n");
        LEMMAS.add("jiboriyl");
        LEMMAS.add("saqar");
        LEMMAS.add("jaAluwt");
        LEMMAS.add("miSor");
        LEMMAS.add("ma`ruwt");
        LEMMAS.add(">aHosan");
        LEMMAS.add("A^zar");
        LEMMAS.add("<iram");
        LEMMAS.add("<iloyaAs");
        LEMMAS.add("bak~ap");
        LEMMAS.add("<idoriys");
        LEMMAS.add(">aqorab");
        LEMMAS.add("sayonaA^'");
        LEMMAS.add(">a$ad~");
        LEMMAS.add("mak~ap");
        LEMMAS.add("baAbil");
        LEMMAS.add("siyniyn");
        LEMMAS.add("ramaDaAn");
        LEMMAS.add(">asofal");
        LEMMAS.add("miykaY`l");
        LEMMAS.add("luqoma`n");
        LEMMAS.add("ha`ruwt");
    }

    private Diptote() {
    }

    public static boolean isDiptoteWithGenitiveFatha(Segment segment) {
        return segment.getCase() == Genitive
                && segment.getArabicText().isFatha(segment.getArabicText().getLength() - 1)
                && segment.getNumber() != Plural
                && LEMMAS.contains(segment.getLemma().key());
    }
}