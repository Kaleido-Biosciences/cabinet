package com.kaleido.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link PlateMapSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PlateMapSearchRepositoryMockConfiguration {

    @MockBean
    private PlateMapSearchRepository mockPlateMapSearchRepository;

}
