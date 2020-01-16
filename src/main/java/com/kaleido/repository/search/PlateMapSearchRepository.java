package com.kaleido.repository.search;

import com.kaleido.domain.PlateMap;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link PlateMap} entity.
 */
public interface PlateMapSearchRepository extends ElasticsearchRepository<PlateMap, Long> {
}
