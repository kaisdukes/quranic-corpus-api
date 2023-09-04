package app.qurancorpus.morphology;

public record SegmentResponse(
        String arabic,
        String posTag,
        String pronounType,
        String morphology) {
}