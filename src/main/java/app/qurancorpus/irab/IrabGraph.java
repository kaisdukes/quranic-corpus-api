package app.qurancorpus.irab;

import memseqdb.Graph;
import memseqdb.Seq2Seq;
import memseqdb.SeqRange;

import static java.lang.System.arraycopy;

public class IrabGraph implements Graph<SeqRange, String[]> {
    private final String[] irab;
    private final Seq2Seq tokenToAnalysis;

    public IrabGraph(String[] irab, Seq2Seq tokenToAnalysis) {
        this.irab = irab;
        this.tokenToAnalysis = tokenToAnalysis;
    }

    @Override
    public String[] query(SeqRange tokenSequenceRange) {
        var range = tokenToAnalysis.getRange(tokenSequenceRange);
        if (range == null) {
            throw new UnsupportedOperationException("Analysis not found.");
        }
        var analysisCount = range.max() - range.min() + 1;
        var irab = new String[analysisCount];
        arraycopy(this.irab, range.min() - 1, irab, 0, analysisCount);
        return irab;
    }
}