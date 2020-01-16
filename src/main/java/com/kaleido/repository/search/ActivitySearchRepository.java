package com.kaleido.repository.search;

import com.kaleido.domain.Activity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Activity} entity.
 */
public interface ActivitySearchRepository extends ElasticsearchRepository<Activity, Long> {
}
