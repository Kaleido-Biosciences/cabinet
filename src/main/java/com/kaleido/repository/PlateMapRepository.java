package com.kaleido.repository;

import com.kaleido.domain.PlateMap;
import com.kaleido.service.dto.PlateMapDTO;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the PlateMap entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlateMapRepository extends JpaRepository<PlateMap, Long> {

	//@Query("SELECT pm.activityName, pm.checksum, pm.id, pm.lastModified, pm.status FROM PlateMap pm where pm.activityName = ?1") 
    List<PlateMapDTO> findAllByActivityName(String activityName);
}
