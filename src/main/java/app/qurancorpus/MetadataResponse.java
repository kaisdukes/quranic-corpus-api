package app.qurancorpus;

import app.qurancorpus.orthography.ChapterMetadata;
import app.qurancorpus.translation.TranslationMetadata;

public record MetadataResponse(
        ChapterMetadata[] chapters,
        TranslationMetadata[] translations) {
}