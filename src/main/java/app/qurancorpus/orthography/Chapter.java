package app.qurancorpus.orthography;

import memseqdb.SeqGraphNode;

public record Chapter(int chapterNumber, Verse[] verses) implements SeqGraphNode<Verse> {

    @Override
    public Verse getChild(int verseNumber) {
        return verses[verseNumber - 1];
    }

    @Override
    public Verse[] children() {
        return verses;
    }
}