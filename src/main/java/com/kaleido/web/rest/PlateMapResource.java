package com.kaleido.web.rest;

import com.kaleido.domain.PlateMap;
import com.kaleido.domain.enumeration.Status;
import com.kaleido.repository.PlateMapRepository;
import com.kaleido.repository.search.PlateMapSearchRepository;
import com.kaleido.service.PlateMapService;
import com.kaleido.service.dto.PlateMapDTO;
import com.kaleido.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.kaleido.domain.PlateMap}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PlateMapResource {

    private final Logger log = LoggerFactory.getLogger(PlateMapResource.class);

    private static final String ENTITY_NAME = "plateMap";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlateMapRepository plateMapRepository;

    private final PlateMapSearchRepository plateMapSearchRepository;
    
    @Autowired
    private final PlateMapService plateMapService;

    public PlateMapResource(PlateMapRepository plateMapRepository, PlateMapSearchRepository plateMapSearchRepository, PlateMapService plateMapService) {
        this.plateMapService = plateMapService;
        this.plateMapRepository = plateMapRepository;
        this.plateMapSearchRepository = plateMapSearchRepository;
    }

    /**
     * {@code POST  /plate-maps} : Create a new plateMap.
     *
     * @param plateMap the plateMap to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new plateMap, or with status {@code 400 (Bad Request)} if the plateMap has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/plate-maps")
    public ResponseEntity<String> createPlateMap(@Valid @RequestBody PlateMap plateMap) throws URISyntaxException {
        log.debug("REST request to save PlateMap : {}", plateMap);
        if(plateMap.getId() != null) {
            throw new BadRequestAlertException("A new plateMap cannot already have an ID", ENTITY_NAME, "idexists");
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        String checksum = plateMapService.savePlateMap(plateMap);
        return new ResponseEntity<String>(checksum,responseHeaders,HttpStatus.CREATED);
    }

    /**
     * {@code PUT  /plate-maps} : Updates an existing plateMap.
     *
     * @param plateMap the plateMap to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated plateMap,
     * or with status {@code 400 (Bad Request)} if the plateMap is not valid,
     * or with status {@code 500 (Internal Server Error)} if the plateMap couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/plate-maps")
    public ResponseEntity<String> updatePlateMap(@Valid @RequestBody PlateMap plateMap) throws URISyntaxException {
        log.debug("REST request to update PlateMap : {}", plateMap);
        return plateMapService.updatePlateMap(plateMap);
    }

    /**
     * {@code GET  /plate-maps} : get all the plateMaps.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of plateMaps in body.
     */
    @GetMapping("/plate-maps")
    public List<PlateMap> getAllPlateMaps() {
        log.debug("REST request to get all PlateMaps");
        return plateMapRepository.findAll();
    }

    /**
     * {@code GET  /plate-maps/:id} : get the "id" plateMap.
     *
     * @param id the id of the plateMap to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the plateMap, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/plate-maps/{id}")
    public ResponseEntity<PlateMap> getPlateMap(@PathVariable Long id) {
        log.debug("REST request to get PlateMap : {}", id);
        Optional<PlateMap> plateMap = plateMapRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(plateMap);
    }

    /**
     * {@code DELETE  /plate-maps/:id} : delete the "id" plateMap.
     *
     * @param id the id of the plateMap to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/plate-maps/{id}")
    public ResponseEntity<Void> deletePlateMap(@PathVariable Long id) {
        log.debug("REST request to delete PlateMap : {}", id);
        plateMapRepository.deleteById(id);
        plateMapSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/plate-maps?query=:query} : search for the plateMap corresponding
     * to the query.
     *
     * @param query the query of the plateMap search.
     * @return the result of the search.
     */
    @GetMapping("/_search/plate-maps")
    public List<PlateMap> searchPlateMaps(@RequestParam String query) {
        log.debug("REST request to search PlateMaps for query {}", query);
        return StreamSupport
            .stream(plateMapSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
    
    @PostMapping("/plate-maps/details")
    public ResponseEntity<List<@Valid PlateMap>> getPlateMapByActivityName(@Valid @RequestBody PlateMap plateMap) {
        log.debug("REST request to get PlateMap : {}", plateMap);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<@Valid PlateMap> plateMapQuery = Example.of(plateMap, matcher);
        List<@Valid PlateMap> results = plateMapRepository.findAll(plateMapQuery);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/plate-maps/data/{checksum}")
    public ResponseEntity<@Valid PlateMap> getDraftPlateMapDataByActivityName(@PathVariable String checksum) {
        log.debug("REST request to get PlateMap draft data: {}", checksum);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        PlateMap plateMap = new PlateMap();
        plateMap.setChecksum(checksum);
        Example<@Valid PlateMap> plateMapQuery = Example.of(plateMap, matcher);
        Optional<@Valid PlateMap> results = plateMapRepository.findOne(plateMapQuery);
        return ResponseUtil.wrapOrNotFound(results);
    }
    
    /**
     * {@code GET  /plate-map-summary/{activityName} : get the plateMap summary based on activityName
     *
     * @param activityName the activityName to be searched.
     * @return the list of PlateMapDTO Objects.
     */
    @GetMapping("/plate-map-summary/{activityName}")
    public ResponseEntity<List<@Valid PlateMapDTO>> retrievePlateMapSummary(@PathVariable String activityName) {
        log.debug("REST request to search PlateMaps based on activityName: ", activityName);
        List<PlateMapDTO> plateMap = plateMapRepository.findAllByActivityName(activityName);
        return ResponseEntity.ok(plateMap);
    }
    
}
