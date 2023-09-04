package app.qurancorpus.orthography;

import app.qurancorpus.morphology.SegmentResponse;

public record TokenResponse(
        int[] location,
        String translation,
        String phonetic,
        SegmentResponse[] segments) {
}