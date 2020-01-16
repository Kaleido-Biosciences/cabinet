package com.kaleido.repository;

import com.kaleido.domain.PlateMap;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the PlateMap entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlateMapRepository extends JpaRepository<PlateMap, Long> {

}
