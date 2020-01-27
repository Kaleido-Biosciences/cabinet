package com.kaleido.web.rest;

import com.kaleido.CabinetApp;
import com.kaleido.domain.PlateMap;
import com.kaleido.repository.PlateMapRepository;
import com.kaleido.repository.search.PlateMapSearchRepository;
import com.kaleido.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static com.kaleido.web.rest.TestUtil.sameInstant;
import static com.kaleido.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kaleido.domain.enumeration.Status;
/**
 * Integration tests for the {@link PlateMapResource} REST controller.
 */
@SpringBootTest(classes = CabinetApp.class)
public class PlateMapResourceIT {

    private static final Status DEFAULT_STATUS = Status.DRAFT;
    private static final Status UPDATED_STATUS = Status.COMPLETED;

    private static final ZonedDateTime DEFAULT_LAST_MODIFIED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_MODIFIED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_CHECKSUM = "AAAAAAAAAA";
    private static final String UPDATED_CHECKSUM = "BBBBBBBBBB";

    private static final String DEFAULT_ACTIVITY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ACTIVITY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DATA = "AAAAAAAAAA";
    private static final String UPDATED_DATA = "BBBBBBBBBB";

    @Autowired
    private PlateMapRepository plateMapRepository;

    /**
     * This repository is mocked in the com.kaleido.repository.search test package.
     *
     * @see com.kaleido.repository.search.PlateMapSearchRepositoryMockConfiguration
     */
    @Autowired
    private PlateMapSearchRepository mockPlateMapSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restPlateMapMockMvc;

    private PlateMap plateMap;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PlateMapResource plateMapResource = new PlateMapResource(plateMapRepository, mockPlateMapSearchRepository);
        this.restPlateMapMockMvc = MockMvcBuilders.standaloneSetup(plateMapResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlateMap createEntity(EntityManager em) {
        PlateMap plateMap = new PlateMap()
            .status(DEFAULT_STATUS)
            .lastModified(DEFAULT_LAST_MODIFIED)
            .checksum(DEFAULT_CHECKSUM)
            .activityName(DEFAULT_ACTIVITY_NAME)
            .data(DEFAULT_DATA);
        return plateMap;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlateMap createUpdatedEntity(EntityManager em) {
        PlateMap plateMap = new PlateMap()
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED)
            .checksum(UPDATED_CHECKSUM)
            .activityName(UPDATED_ACTIVITY_NAME)
            .data(UPDATED_DATA);
        return plateMap;
    }

    @BeforeEach
    public void initTest() {
        plateMap = createEntity(em);
    }

    @Test
    @Transactional
    public void createPlateMap() throws Exception {
        int databaseSizeBeforeCreate = plateMapRepository.findAll().size();

        // Create the PlateMap
        restPlateMapMockMvc.perform(post("/api/plate-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(plateMap)))
            .andExpect(status().isCreated());

        // Validate the PlateMap in the database
        List<PlateMap> plateMapList = plateMapRepository.findAll();
        assertThat(plateMapList).hasSize(databaseSizeBeforeCreate + 1);
        PlateMap testPlateMap = plateMapList.get(plateMapList.size() - 1);
        assertThat(testPlateMap.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPlateMap.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testPlateMap.getChecksum()).isEqualTo(DEFAULT_CHECKSUM);
        assertThat(testPlateMap.getActivityName()).isEqualTo(DEFAULT_ACTIVITY_NAME);
        assertThat(testPlateMap.getData()).isEqualTo(DEFAULT_DATA);

        // Validate the PlateMap in Elasticsearch
        verify(mockPlateMapSearchRepository, times(1)).save(testPlateMap);
    }

    @Test
    @Transactional
    public void createPlateMapWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = plateMapRepository.findAll().size();

        // Create the PlateMap with an existing ID
        plateMap.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlateMapMockMvc.perform(post("/api/plate-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(plateMap)))
            .andExpect(status().isBadRequest());

        // Validate the PlateMap in the database
        List<PlateMap> plateMapList = plateMapRepository.findAll();
        assertThat(plateMapList).hasSize(databaseSizeBeforeCreate);

        // Validate the PlateMap in Elasticsearch
        verify(mockPlateMapSearchRepository, times(0)).save(plateMap);
    }


    @Test
    @Transactional
    public void getAllPlateMaps() throws Exception {
        // Initialize the database
        plateMapRepository.saveAndFlush(plateMap);

        // Get all the plateMapList
        restPlateMapMockMvc.perform(get("/api/plate-maps?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(plateMap.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED))))
            .andExpect(jsonPath("$.[*].checksum").value(hasItem(DEFAULT_CHECKSUM)))
            .andExpect(jsonPath("$.[*].activityName").value(hasItem(DEFAULT_ACTIVITY_NAME)))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA)));
    }
    
    @Test
    @Transactional
    public void getPlateMap() throws Exception {
        // Initialize the database
        plateMapRepository.saveAndFlush(plateMap);

        // Get the plateMap
        restPlateMapMockMvc.perform(get("/api/plate-maps/{id}", plateMap.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(plateMap.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastModified").value(sameInstant(DEFAULT_LAST_MODIFIED)))
            .andExpect(jsonPath("$.checksum").value(DEFAULT_CHECKSUM))
            .andExpect(jsonPath("$.activityName").value(DEFAULT_ACTIVITY_NAME))
            .andExpect(jsonPath("$.data").value(DEFAULT_DATA));
    }

    @Test
    @Transactional
    public void getNonExistingPlateMap() throws Exception {
        // Get the plateMap
        restPlateMapMockMvc.perform(get("/api/plate-maps/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePlateMap() throws Exception {
        // Initialize the database
        plateMapRepository.saveAndFlush(plateMap);

        int databaseSizeBeforeUpdate = plateMapRepository.findAll().size();

        // Update the plateMap
        PlateMap updatedPlateMap = plateMapRepository.findById(plateMap.getId()).get();
        // Disconnect from session so that the updates on updatedPlateMap are not directly saved in db
        em.detach(updatedPlateMap);
        updatedPlateMap
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED)
            .checksum(UPDATED_CHECKSUM)
            .activityName(UPDATED_ACTIVITY_NAME)
            .data(UPDATED_DATA);

        restPlateMapMockMvc.perform(put("/api/plate-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPlateMap)))
            .andExpect(status().isOk());

        // Validate the PlateMap in the database
        List<PlateMap> plateMapList = plateMapRepository.findAll();
        assertThat(plateMapList).hasSize(databaseSizeBeforeUpdate);
        PlateMap testPlateMap = plateMapList.get(plateMapList.size() - 1);
        assertThat(testPlateMap.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPlateMap.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testPlateMap.getChecksum()).isEqualTo(UPDATED_CHECKSUM);
        assertThat(testPlateMap.getActivityName()).isEqualTo(UPDATED_ACTIVITY_NAME);
        assertThat(testPlateMap.getData()).isEqualTo(UPDATED_DATA);

        // Validate the PlateMap in Elasticsearch
        verify(mockPlateMapSearchRepository, times(1)).save(testPlateMap);
    }

    @Test
    @Transactional
    public void updateNonExistingPlateMap() throws Exception {
        int databaseSizeBeforeUpdate = plateMapRepository.findAll().size();

        // Create the PlateMap

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlateMapMockMvc.perform(put("/api/plate-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(plateMap)))
            .andExpect(status().isBadRequest());

        // Validate the PlateMap in the database
        List<PlateMap> plateMapList = plateMapRepository.findAll();
        assertThat(plateMapList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PlateMap in Elasticsearch
        verify(mockPlateMapSearchRepository, times(0)).save(plateMap);
    }

    @Test
    @Transactional
    public void deletePlateMap() throws Exception {
        // Initialize the database
        plateMapRepository.saveAndFlush(plateMap);

        int databaseSizeBeforeDelete = plateMapRepository.findAll().size();

        // Delete the plateMap
        restPlateMapMockMvc.perform(delete("/api/plate-maps/{id}", plateMap.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PlateMap> plateMapList = plateMapRepository.findAll();
        assertThat(plateMapList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PlateMap in Elasticsearch
        verify(mockPlateMapSearchRepository, times(1)).deleteById(plateMap.getId());
    }

    @Test
    @Transactional
    public void searchPlateMap() throws Exception {
        // Initialize the database
        plateMapRepository.saveAndFlush(plateMap);
        when(mockPlateMapSearchRepository.search(queryStringQuery("id:" + plateMap.getId())))
            .thenReturn(Collections.singletonList(plateMap));
        // Search the plateMap
        restPlateMapMockMvc.perform(get("/api/_search/plate-maps?query=id:" + plateMap.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(plateMap.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED))))
            .andExpect(jsonPath("$.[*].checksum").value(hasItem(DEFAULT_CHECKSUM)))
            .andExpect(jsonPath("$.[*].activityName").value(hasItem(DEFAULT_ACTIVITY_NAME)))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA)));
    }
}
