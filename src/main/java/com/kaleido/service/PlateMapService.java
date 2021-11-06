package com.kaleido.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaleido.config.Constants;
import com.kaleido.domain.PlateMap;
import com.kaleido.domain.enumeration.Status;
import com.kaleido.repository.PlateMapRepository;
import com.kaleido.repository.search.PlateMapSearchRepository;
import com.kaleido.service.amazonaws.s3.CabinetS3Client;
import com.kaleido.service.amazonaws.s3.CabinetS3Exception;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
public class PlateMapService {

    private final Logger log = LoggerFactory.getLogger(PlateMapService.class);

    private static final String ENTITY_NAME = "plateMap";

    @Autowired
    private final PlateMapRepository plateMapRepository;

    @Autowired
    private final PlateMapSearchRepository plateMapSearchRepository;

    @Autowired
    private final CabinetS3Client cabinetS3Client;

    public PlateMapService(PlateMapRepository plateMapRepository, PlateMapSearchRepository plateMapSearchRepository, CabinetS3Client cabinetS3Client) {
    	this.plateMapRepository = plateMapRepository;
    	this.plateMapSearchRepository = plateMapSearchRepository;
    	this.cabinetS3Client = cabinetS3Client;
    }

    /**
     * Get one plateMap by id.
     *
     * @param activityName the activity name of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<PlateMap> findDraftByActivityName(String activityName) {
        log.debug("Request to get Batch : {}", activityName);
        return plateMapRepository.findFirstByActivityNameEqualsAndStatusEquals(activityName, Status.DRAFT);
    }

    /**
     * Save an instance of a platemap
     * @param plateMap
     * @return
     */
    public PlateMap savePlateMap(PlateMap plateMap) throws IOException {
        boolean release = false;
        if (plateMap.getStatus().equals(Status.COMPLETED)){
            release = true;
        }
        final PlateMap currentPlateMap = findDraftByActivityName(plateMap.getActivityName()).orElse(null);
        if (currentPlateMap != null){
            plateMap.setId(currentPlateMap.getId());
        }
        ZonedDateTime currentTime = ZonedDateTime.now();
        plateMap.setLastModified(currentTime);
        //plateMap.setNumPlates(DataUtillity.getPlatesCount(plateMap.getData()));
        PlateMap result = plateMapRepository.save(plateMap.status(Status.DRAFT));
        plateMapSearchRepository.save(result);
        if(release) {
            exportPlate(new PlateMap(result).status(Status.COMPLETED));
        }
        return result;
    }

    private void exportPlate(PlateMap plateToRelease) {
        //Save as a new entry
        CompletableFuture.runAsync(() -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                cabinetS3Client.writeToS3(plateToRelease.getActivityName()+".json", mapper.writeValueAsString(plateToRelease));
            } catch (CabinetS3Exception e) {
                log.error("Error occurred in putting platemap to s3 "+e.getMessage());
                plateToRelease.setStatus(Status.ERROR);
            } catch (JsonProcessingException e) {
                log.error("Error occurred in converting platemap object to json"+e.getMessage());
                plateToRelease.setStatus(Status.ERROR);
            }
        });
        PlateMap result = plateMapRepository.save(plateToRelease);
        plateMapSearchRepository.save(result);
    }
}
