package com.kaleido.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.kaleido.domain.PlateMap;
import com.kaleido.domain.enumeration.Status;
import com.kaleido.repository.PlateMapRepository;
import com.kaleido.repository.search.PlateMapSearchRepository;
import com.kaleido.service.dto.PlateMapDTO;
import com.kaleido.util.DataUtillity;

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

@Slf4j
@Service
public class PlateMapService {
	
    private final Logger log = LoggerFactory.getLogger(PlateMapService.class);

    private static final String ENTITY_NAME = "plateMap";
    
    @Autowired
    private final PlateMapRepository plateMapRepository;

    @Autowired
    private final PlateMapSearchRepository plateMapSearchRepository;
    
    public PlateMapService(PlateMapRepository plateMapRepository, PlateMapSearchRepository plateMapSearchRepository) {
    	this.plateMapRepository = plateMapRepository;
    	this.plateMapSearchRepository = plateMapSearchRepository;
    }
    
    public ResponseEntity<String> updatePlateMap(PlateMap plateMap) {
    	log.debug("Platemap data is ", plateMap);
    	
        HttpHeaders responseHeaders = new HttpHeaders();
        if (plateMap.getId() == null) {
            return new ResponseEntity<String>("Invalid ID",responseHeaders,HttpStatus.BAD_REQUEST);
            //throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
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
                    plateMap.setNumPlates(DataUtillity.getPlatesCount(plateMap.getData()));
                
                    // Create new platemap with the same data but COMPLETED status
                    PlateMap completedPlateMap = new PlateMap();
                    completedPlateMap.setActivityName(plateMap.getActivityName());
                    completedPlateMap.setData(plateMap.getData());
                    completedPlateMap.setLastModified(currentTime);
                    completedPlateMap.setStatus(Status.COMPLETED);
                    completedPlateMap.setNumPlates(plateMap.getNumPlates());
                    String completedChecksum = DigestUtils.md5Hex(completedPlateMap.prepareStringForChecksum());
                    completedPlateMap.setChecksum(completedChecksum);

                    List<PlateMap> plateMapList = Arrays.asList(plateMap,completedPlateMap);
                    List<PlateMap> result = plateMapRepository.saveAll(plateMapList);
                    plateMapSearchRepository.saveAll(result);

                    return new ResponseEntity<String>(draftChecksum,responseHeaders,HttpStatus.OK);
                }
                else if(plateMap.getStatus() == Status.DRAFT) {
                    plateMap.setLastModified(currentTime);
                    String draftChecksum = DigestUtils.md5Hex(plateMap.prepareStringForChecksum());
                    plateMap.setChecksum(draftChecksum);
                    plateMap.setNumPlates(DataUtillity.getPlatesCount(plateMap.getData()));
                    PlateMap result = plateMapRepository.save(plateMap);
                    plateMapSearchRepository.save(result);
                    
                    return new ResponseEntity<String>(draftChecksum,responseHeaders,HttpStatus.OK);
                }
                else {
                	return new ResponseEntity<String>("No status is sent",responseHeaders,HttpStatus.BAD_REQUEST);
                }
            }
            else {
                return new ResponseEntity<String>("Checksum is not the most recent",responseHeaders,HttpStatus.CONFLICT);
            }
        }
    }
    
    public String savePlateMap(PlateMap plateMap) {
        ZonedDateTime currentTime = ZonedDateTime.now();
        plateMap.setLastModified(currentTime);
        String checksum = DigestUtils.md5Hex(plateMap.prepareStringForChecksum());
        plateMap.setChecksum(checksum);
        plateMap.setNumPlates(DataUtillity.getPlatesCount(plateMap.getData()));
        PlateMap result = plateMapRepository.save(plateMap);
        plateMapSearchRepository.save(result);
        return checksum;
    }
}
