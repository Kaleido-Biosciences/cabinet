package com.kaleido.web.rest;

import com.kaleido.domain.PlateMap;
import com.kaleido.domain.enumeration.Status;
import com.kaleido.repository.PlateMapRepository;
import com.kaleido.repository.search.PlateMapSearchRepository;
import com.kaleido.service.dto.DataDTO;
import com.kaleido.service.dto.PlateMapDTO;
import com.kaleido.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public PlateMapResource(PlateMapRepository plateMapRepository, PlateMapSearchRepository plateMapSearchRepository) {
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
        HttpHeaders responseHeaders = new HttpHeaders();
        if (plateMap.getId() != null) {
            throw new BadRequestAlertException("A new plateMap cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ZonedDateTime currentTime = ZonedDateTime.now();
        plateMap.setLastModified(currentTime);
        String checksum = DigestUtils.md5Hex(plateMap.prepareStringForChecksum());
        plateMap.setChecksum(checksum);
        PlateMap result = plateMapRepository.save(plateMap);
        plateMapSearchRepository.save(result);
        
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
        HttpHeaders responseHeaders = new HttpHeaders();
        if (plateMap.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        else {
        	//Retrieve data from database based on id, activity name, and checksum to check for latest value
            PlateMap check = new PlateMap();
            check.setId(plateMap.getId());
            check.setActivityName(plateMap.getActivityName());
            check.setChecksum(plateMap.getChecksum());
            
            ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
            Example<@Valid PlateMap> plateMapQuery = Example.of(check, matcher);
            List<@Valid PlateMap> results = plateMapRepository.findAll(plateMapQuery);
            if(!results.isEmpty()) {
            	ZonedDateTime currentTime = ZonedDateTime.now();
            	
                if(plateMap.getStatus() == Status.COMPLETED) {
                    //Updates the draft data to latest checksum and data, but leave DRAFT status
                    plateMap.setStatus(Status.DRAFT);
                    plateMap.setLastModified(currentTime);
                    String draftChecksum = DigestUtils.md5Hex(plateMap.prepareStringForChecksum());
                    plateMap.setChecksum(draftChecksum);
                
                    // Create new platemap with the same data but COMPLETED status
                    PlateMap completedPlateMap = new PlateMap();
                    completedPlateMap.setActivityName(plateMap.getActivityName());
            	    completedPlateMap.setData(plateMap.getData());
            	    completedPlateMap.setLastModified(currentTime);
            	    completedPlateMap.setStatus(Status.COMPLETED);
            	    String completedChecksum = DigestUtils.md5Hex(completedPlateMap.prepareStringForChecksum());
            	    completedPlateMap.setChecksum(completedChecksum);

        	        List<PlateMap> plateMapList = Arrays.asList(plateMap,completedPlateMap);
        	        List<PlateMap> result = plateMapRepository.saveAll(plateMapList);
                    plateMapSearchRepository.saveAll(result);
                
                    return new ResponseEntity<String>(draftChecksum,responseHeaders,HttpStatus.OK);
                }
                else {
                	plateMap.setLastModified(currentTime);
                    String draftChecksum = DigestUtils.md5Hex(plateMap.prepareStringForChecksum());
                    plateMap.setChecksum(draftChecksum);
                    
                    PlateMap result = plateMapRepository.save(plateMap);
                    plateMapSearchRepository.save(result);
                    
                    return new ResponseEntity<String>(draftChecksum,responseHeaders,HttpStatus.OK);
                }
            }
            else {
            	return new ResponseEntity<String>("Checksum is not the most recent",responseHeaders,HttpStatus.CONFLICT);
            }
        }
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
    
    //This api can consolidate the next 2 api into one
    @PostMapping("/plate-maps/details")
    public ResponseEntity<List<@Valid PlateMap>> getPlateMapByActivityName(@Valid @RequestBody PlateMap plateMap) {
        log.debug("REST request to get PlateMap : {}", plateMap);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<@Valid PlateMap> plateMapQuery = Example.of(plateMap, matcher);
        List<@Valid PlateMap> results = plateMapRepository.findAll(plateMapQuery);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/plate-maps/data/draft/{activityName}")
    public ResponseEntity<@Valid DataDTO> getDraftPlateMapDataByActivityName(@PathVariable String activityName) {
        log.debug("REST request to get PlateMap draft data: {}", activityName);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        PlateMap plateMap = new PlateMap();
        plateMap.setActivityName(activityName);
        plateMap.setStatus(Status.DRAFT);
        Example<@Valid PlateMap> plateMapQuery = Example.of(plateMap, matcher);
        //List<@Valid PlateMap> results = plateMapRepository.findAll(plateMapQuery);
        Optional<@Valid DataDTO> results = plateMapRepository.findDataByActivityName(activityName);
        //return ResponseEntity.ok(results);
        return ResponseUtil.wrapOrNotFound(results);
    }
    
    @PostMapping("/plate-maps/data/completed")
    public ResponseEntity<List<@Valid PlateMap>> getDraftPlateMapDataByActivityNameAndTimestamp(@Valid @RequestBody PlateMap plateMap) {
    	log.debug("REST request to get PlateMap completed data: {}", plateMap);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        plateMap.setStatus(Status.COMPLETED);
        Example<@Valid PlateMap> plateMapQuery = Example.of(plateMap, matcher);
        List<@Valid PlateMap> results = plateMapRepository.findAll(plateMapQuery);
        return ResponseEntity.ok(results);
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
