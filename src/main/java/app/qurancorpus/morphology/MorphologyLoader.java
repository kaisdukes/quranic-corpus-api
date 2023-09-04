package app.qurancorpus.morphology;

import app.qurancorpus.lexicography.LemmaService;
import app.qurancorpus.morphology.segmentation.Segmenter;
import app.qurancorpus.orthography.Document;
import app.qurancorpus.orthography.Location;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import memseqdb.GraphLoader;

import java.util.HashMap;

import static app.qurancorpus.ResourceReader.readResource;

@Factory
public class MorphologyLoader implements GraphLoader<MorphologyGraph> {
    private final Document document;
    private final LemmaService lemmaService;

    public MorphologyLoader(Document document, LemmaService lemmaService) {
        this.document = document;
        this.lemmaService = lemmaService;
    }

    @Override
    @Singleton
    @SneakyThrows
    public MorphologyGraph load() {
        var segmenter = new Segmenter(lemmaService);
        var segmentMap = new HashMap<Location, Segment[]>();
        try (var reader = readResource("/data/morphology.txt")) {
            for (var chapter : document.children()) {
                for (var verse : chapter.verses()) {
                    for (var token : verse.tokens()) {
                        var morphology = reader.readLine();
                        var segments = segmenter.getSegments(token, morphology);
                        segmentMap.put(token.location(), segments);
                    }
                }
            }
        }
        return new MorphologyGraph(segmentMap);
    }
}