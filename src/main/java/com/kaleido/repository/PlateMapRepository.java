package com.kaleido.repository;

import com.kaleido.domain.PlateMap;
import com.kaleido.domain.enumeration.Status;
import com.kaleido.service.dto.DataDTO;
import com.kaleido.service.dto.PlateMapDTO;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the PlateMap entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlateMapRepository extends JpaRepository<PlateMap, Long> {

    List<PlateMapDTO> findAllByActivityName(String activityName);
    
    Optional<@Valid PlateMap> findDataByActivityName(String activityName);

    Optional<PlateMap> findFirstByActivityNameEqualsAndStatusEquals(String activityName, Status draft);
}
