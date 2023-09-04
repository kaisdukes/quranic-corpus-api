package app.qurancorpus.irab;

import app.qurancorpus.orthography.LocationService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import memseqdb.SeqRange;

import static app.qurancorpus.orthography.Location.parseLocation;
import static io.micronaut.http.HttpStatus.BAD_REQUEST;

@Controller("/irab")
public class IrabController {

    @Inject
    IrabGraph irabGraph;

    @Inject
    LocationService locationService;

    @Get
    public String[] getIrab(@QueryValue String from, @QueryValue String to) {
        var response = irabGraph.query(new SeqRange(getTokenSequenceNumber(from), getTokenSequenceNumber(to)));
        if (response.length > 20) {
            throw new HttpStatusException(BAD_REQUEST, "Request too long.");
        }
        return response;
    }

    private int getTokenSequenceNumber(String location) {
        return locationService.getTokenSequenceNumber(parseLocation(location));
    }
}