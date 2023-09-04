package app.qurancorpus.orthography.tanzil;

import lombok.SneakyThrows;

import javax.xml.parsers.SAXParserFactory;
import java.util.List;

import static app.qurancorpus.ResourceReader.resourceStream;

public class TanzilReader {

    @SneakyThrows
    public List<TanzilChapter> readChapters() {
        var handler = new TanzilHandler();
        SAXParserFactory
                .newInstance()
                .newSAXParser()
                .parse(
                        resourceStream("/data/quran-uthmani.xml"),
                        handler);

        return handler.getChapters();
    }
}