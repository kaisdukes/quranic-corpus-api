package app.qurancorpus;

import app.qurancorpus.morphology.WordMorphologyResponse;
import app.qurancorpus.orthography.Location;
import app.qurancorpus.orthography.VerseResponse;
import app.qurancorpus.syntax.GraphResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Client("/")
public interface CorpusClient {

    @Get("metadata")
    MetadataResponse getMetadata();

    @Get("morphology")
    VerseResponse[] getMorphology(
            @QueryValue @JsonFormat(shape = STRING) Location location,
            @QueryValue("n") int count,
            @Nullable @QueryValue("translation") String translationQuery,
            @Nullable @QueryValue Boolean features);

    @Get("morphology/word")
    WordMorphologyResponse getWordMorphology(@QueryValue @JsonFormat(shape = STRING) Location location);

    @Get("syntax")
    GraphResponse getSyntax(
            @QueryValue @JsonFormat(shape = STRING) Location location,
            @QueryValue("graph") int graphNumber);

    @Get("irab")
    String[] getIrab(
            @QueryValue @JsonFormat(shape = STRING) Location from,
            @QueryValue @JsonFormat(shape = STRING) Location to);
}