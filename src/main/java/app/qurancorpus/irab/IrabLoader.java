package app.qurancorpus.irab;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import memseqdb.GraphLoader;
import memseqdb.Seq2Seq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static app.qurancorpus.ResourceReader.readResource;
import static java.lang.Integer.parseInt;
import static java.lang.System.currentTimeMillis;

@Factory
public class IrabLoader implements GraphLoader<IrabGraph> {
    private static final Logger log = LoggerFactory.getLogger(IrabLoader.class);
    private static final int TOTAL_ANALYSIS_COUNT = 30486;

    @Override
    @Singleton
    @SneakyThrows
    public IrabGraph load() {
        try (var reader = readResource("/data/irab.tsv")) {
            var irab = new String[TOTAL_ANALYSIS_COUNT];
            var start = currentTimeMillis();
            var analysisSequenceNumber = 0;
            var tokenToAnalysis = new ArrayList<Integer>();

            String line;
            while ((line = reader.readLine()) != null) {
                var parts = line.split("\t");

                var tokenCount = parseInt(parts[0]);
                irab[analysisSequenceNumber++] = parts[1].replace("\\n", "\n");
                for (var i = 0; i < tokenCount; i++) {
                    tokenToAnalysis.add(analysisSequenceNumber);
                }
            }

            var irabGraph = new IrabGraph(irab, new Seq2Seq(tokenToAnalysis));
            var elapsed = currentTimeMillis() - start;
            log.info("Loaded {} analyses in {} ms", analysisSequenceNumber, elapsed);
            return irabGraph;
        }
    }
}