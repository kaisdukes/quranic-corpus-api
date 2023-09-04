package app.qurancorpus.orthography.tanzil;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class TanzilHandler extends DefaultHandler {
    private final List<TanzilChapter> chapters = new ArrayList<>();
    private final List<String> verses = new ArrayList<>();

    public List<TanzilChapter> getChapters() {
        return chapters;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) {

        if (qName.equals("aya")) {
            verses.add(attributes.getValue("text"));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("sura")) {
            chapters.add(new TanzilChapter(new ArrayList<>(verses)));
            verses.clear();
        }
    }
}