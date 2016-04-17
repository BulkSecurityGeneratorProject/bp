package cz.cvut.karolan1.web.rest;

import com.codahale.metrics.annotation.Timed;
import cz.cvut.karolan1.domain.Flat;
import cz.cvut.karolan1.repository.FlatRepository;
import cz.cvut.karolan1.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Flat.
 */
@RestController
@RequestMapping("/api")
public class FlatResource {

    private final Logger log = LoggerFactory.getLogger(FlatResource.class);
        
    @Inject
    private FlatRepository flatRepository;
    
    /**
     * POST  /flats : Create a new flat.
     *
     * @param flat the flat to create
     * @return the ResponseEntity with status 201 (Created) and with body the new flat, or with status 400 (Bad Request) if the flat has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/flats",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Flat> createFlat(@Valid @RequestBody Flat flat) throws URISyntaxException {
        log.debug("REST request to save Flat : {}", flat);
        if (flat.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("flat", "idexists", "A new flat cannot already have an ID")).body(null);
        }
        Flat result = flatRepository.save(flat);
        return ResponseEntity.created(new URI("/api/flats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("flat", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /flats : Updates an existing flat.
     *
     * @param flat the flat to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated flat,
     * or with status 400 (Bad Request) if the flat is not valid,
     * or with status 500 (Internal Server Error) if the flat couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/flats",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Flat> updateFlat(@Valid @RequestBody Flat flat) throws URISyntaxException {
        log.debug("REST request to update Flat : {}", flat);
        if (flat.getId() == null) {
            return createFlat(flat);
        }
        Flat result = flatRepository.save(flat);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("flat", flat.getId().toString()))
            .body(result);
    }

    /**
     * GET  /flats : get all the flats.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of flats in body
     */
    @RequestMapping(value = "/flats",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Flat> getAllFlats() {
        log.debug("REST request to get all Flats");
        List<Flat> flats = flatRepository.findAll();
        return flats;
    }

    /**
     * GET  /flats/:id : get the "id" flat.
     *
     * @param id the id of the flat to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the flat, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/flats/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Flat> getFlat(@PathVariable Long id) {
        log.debug("REST request to get Flat : {}", id);
        Flat flat = flatRepository.findOne(id);
        return Optional.ofNullable(flat)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /flats/:id : delete the "id" flat.
     *
     * @param id the id of the flat to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/flats/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteFlat(@PathVariable Long id) {
        log.debug("REST request to delete Flat : {}", id);
        flatRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("flat", id.toString())).build();
    }

}
