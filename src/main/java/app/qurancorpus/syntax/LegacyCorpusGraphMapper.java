package app.qurancorpus.syntax;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class LegacyCorpusGraphMapper {
    private static final int GAP_CHAPTER_NUMBER_START = 9;
    private static final int GAP_CHAPTER_NUMBER_END = 58;

    @Inject
    SyntaxService syntaxService;

    public int getLegacyCorpusGraphNumber(int graphSequenceNumber) {
        var graph = syntaxService.getGraphs().get(graphSequenceNumber - 1);
        var chapterNumber = graph.getFirstToken().location().chapterNumber();

        if (chapterNumber < GAP_CHAPTER_NUMBER_START) {
            return graphSequenceNumber;
        }
        if (chapterNumber > GAP_CHAPTER_NUMBER_END) {
            return graphSequenceNumber - getNumberOfGapGraphs(graphSequenceNumber);
        }
        return 0;
    }

    private int getNumberOfGapGraphs(int graphSequenceNumber) {
        var graphs = syntaxService.getGraphs();
        var gapGraphs = 0;

        for (var i = 0; i < graphSequenceNumber; i++) {
            var chapterNumber = graphs.get(i).getFirstToken().location().chapterNumber();
            if (chapterNumber >= GAP_CHAPTER_NUMBER_START && chapterNumber <= GAP_CHAPTER_NUMBER_END) {
                gapGraphs++;
            }
        }

        return gapGraphs;
    }
}