package app.qurancorpus;

import app.qurancorpus.orthography.OrthographyService;
import app.qurancorpus.translation.TranslationService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;

@Controller("/metadata")
public class MetadataController {

    @Inject
    OrthographyService orthographyService;

    @Inject
    TranslationService translationService;

    @Get
    public MetadataResponse getMetadata() {
        return new MetadataResponse(
                orthographyService.getChapters(),
                translationService.getMetadata());
    }
}