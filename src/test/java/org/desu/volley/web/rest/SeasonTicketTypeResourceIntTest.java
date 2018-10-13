package org.desu.volley.web.rest;

import org.desu.volley.VolleyApp;
import org.desu.volley.domain.SeasonTicketType;
import org.desu.volley.repository.SeasonTicketTypeRepository;
import org.desu.volley.service.SeasonTicketTypeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.desu.volley.domain.enumeration.AttendingType;

/**
 * Test class for the SeasonTicketTypeResource REST controller.
 *
 * @see SeasonTicketTypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = VolleyApp.class)
@WebAppConfiguration
@IntegrationTest
public class SeasonTicketTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_PRICE = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_PRICE = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final Integer DEFAULT_DURATION = 1;
    private static final Integer UPDATED_DURATION = 2;

    private static final Integer DEFAULT_CAPACITY = 0;
    private static final Integer UPDATED_CAPACITY = 1;

    private static final Integer DEFAULT_INDEX = 0;
    private static final Integer UPDATED_INDEX = 1;

    private static final AttendingType DEFAULT_ATTENDING_TYPE = AttendingType.CAPACITY;
    private static final AttendingType UPDATED_ATTENDING_TYPE = AttendingType.DURATION;

    @Inject
    private SeasonTicketTypeRepository seasonTicketTypeRepository;

    @Inject
    private SeasonTicketTypeService seasonTicketTypeService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSeasonTicketTypeMockMvc;

    private SeasonTicketType seasonTicketType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SeasonTicketTypeResource seasonTicketTypeResource = new SeasonTicketTypeResource();
        ReflectionTestUtils.setField(seasonTicketTypeResource, "seasonTicketTypeService", seasonTicketTypeService);
        this.restSeasonTicketTypeMockMvc = MockMvcBuilders.standaloneSetup(seasonTicketTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        seasonTicketType = new SeasonTicketType();
        seasonTicketType.setName(DEFAULT_NAME);
        seasonTicketType.setPrice(DEFAULT_PRICE);
        seasonTicketType.setDuration(DEFAULT_DURATION);
        seasonTicketType.setCapacity(DEFAULT_CAPACITY);
        seasonTicketType.setIndex(DEFAULT_INDEX);
        seasonTicketType.setAttendingType(DEFAULT_ATTENDING_TYPE);
    }

    @Test
    @Transactional
    public void createSeasonTicketType() throws Exception {
        int databaseSizeBeforeCreate = seasonTicketTypeRepository.findAll().size();

        // Create the SeasonTicketType

        restSeasonTicketTypeMockMvc.perform(post("/api/season-ticket-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(seasonTicketType)))
                .andExpect(status().isCreated());

        // Validate the SeasonTicketType in the database
        List<SeasonTicketType> seasonTicketTypes = seasonTicketTypeRepository.findAll();
        assertThat(seasonTicketTypes).hasSize(databaseSizeBeforeCreate + 1);
        SeasonTicketType testSeasonTicketType = seasonTicketTypes.get(seasonTicketTypes.size() - 1);
        assertThat(testSeasonTicketType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSeasonTicketType.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testSeasonTicketType.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testSeasonTicketType.getCapacity()).isEqualTo(DEFAULT_CAPACITY);
        assertThat(testSeasonTicketType.getIndex()).isEqualTo(DEFAULT_INDEX);
        assertThat(testSeasonTicketType.getAttendingType()).isEqualTo(DEFAULT_ATTENDING_TYPE);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = seasonTicketTypeRepository.findAll().size();
        // set the field null
        seasonTicketType.setName(null);

        // Create the SeasonTicketType, which fails.

        restSeasonTicketTypeMockMvc.perform(post("/api/season-ticket-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(seasonTicketType)))
                .andExpect(status().isBadRequest());

        List<SeasonTicketType> seasonTicketTypes = seasonTicketTypeRepository.findAll();
        assertThat(seasonTicketTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = seasonTicketTypeRepository.findAll().size();
        // set the field null
        seasonTicketType.setPrice(null);

        // Create the SeasonTicketType, which fails.

        restSeasonTicketTypeMockMvc.perform(post("/api/season-ticket-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(seasonTicketType)))
                .andExpect(status().isBadRequest());

        List<SeasonTicketType> seasonTicketTypes = seasonTicketTypeRepository.findAll();
        assertThat(seasonTicketTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDurationIsRequired() throws Exception {
        int databaseSizeBeforeTest = seasonTicketTypeRepository.findAll().size();
        // set the field null
        seasonTicketType.setDuration(null);

        // Create the SeasonTicketType, which fails.

        restSeasonTicketTypeMockMvc.perform(post("/api/season-ticket-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(seasonTicketType)))
                .andExpect(status().isBadRequest());

        List<SeasonTicketType> seasonTicketTypes = seasonTicketTypeRepository.findAll();
        assertThat(seasonTicketTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCapacityIsRequired() throws Exception {
        int databaseSizeBeforeTest = seasonTicketTypeRepository.findAll().size();
        // set the field null
        seasonTicketType.setCapacity(null);

        // Create the SeasonTicketType, which fails.

        restSeasonTicketTypeMockMvc.perform(post("/api/season-ticket-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(seasonTicketType)))
                .andExpect(status().isBadRequest());

        List<SeasonTicketType> seasonTicketTypes = seasonTicketTypeRepository.findAll();
        assertThat(seasonTicketTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIndexIsRequired() throws Exception {
        int databaseSizeBeforeTest = seasonTicketTypeRepository.findAll().size();
        // set the field null
        seasonTicketType.setIndex(null);

        // Create the SeasonTicketType, which fails.

        restSeasonTicketTypeMockMvc.perform(post("/api/season-ticket-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(seasonTicketType)))
                .andExpect(status().isBadRequest());

        List<SeasonTicketType> seasonTicketTypes = seasonTicketTypeRepository.findAll();
        assertThat(seasonTicketTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAttendingTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = seasonTicketTypeRepository.findAll().size();
        // set the field null
        seasonTicketType.setAttendingType(null);

        // Create the SeasonTicketType, which fails.

        restSeasonTicketTypeMockMvc.perform(post("/api/season-ticket-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(seasonTicketType)))
                .andExpect(status().isBadRequest());

        List<SeasonTicketType> seasonTicketTypes = seasonTicketTypeRepository.findAll();
        assertThat(seasonTicketTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSeasonTicketTypes() throws Exception {
        // Initialize the database
        seasonTicketTypeRepository.saveAndFlush(seasonTicketType);

        // Get all the seasonTicketTypes
        restSeasonTicketTypeMockMvc.perform(get("/api/season-ticket-types?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(seasonTicketType.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.toString())))
                .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
                .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY)))
                .andExpect(jsonPath("$.[*].index").value(hasItem(DEFAULT_INDEX)))
                .andExpect(jsonPath("$.[*].attendingType").value(hasItem(DEFAULT_ATTENDING_TYPE.toString())));
    }

    @Test
    @Transactional
    public void getSeasonTicketType() throws Exception {
        // Initialize the database
        seasonTicketTypeRepository.saveAndFlush(seasonTicketType);

        // Get the seasonTicketType
        restSeasonTicketTypeMockMvc.perform(get("/api/season-ticket-types/{id}", seasonTicketType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(seasonTicketType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.toString()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION))
            .andExpect(jsonPath("$.capacity").value(DEFAULT_CAPACITY))
            .andExpect(jsonPath("$.index").value(DEFAULT_INDEX))
            .andExpect(jsonPath("$.attendingType").value(DEFAULT_ATTENDING_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSeasonTicketType() throws Exception {
        // Get the seasonTicketType
        restSeasonTicketTypeMockMvc.perform(get("/api/season-ticket-types/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSeasonTicketType() throws Exception {
        // Initialize the database
        seasonTicketTypeService.save(seasonTicketType);

        int databaseSizeBeforeUpdate = seasonTicketTypeRepository.findAll().size();

        // Update the seasonTicketType
        SeasonTicketType updatedSeasonTicketType = new SeasonTicketType();
        updatedSeasonTicketType.setId(seasonTicketType.getId());
        updatedSeasonTicketType.setName(UPDATED_NAME);
        updatedSeasonTicketType.setPrice(UPDATED_PRICE);
        updatedSeasonTicketType.setDuration(UPDATED_DURATION);
        updatedSeasonTicketType.setCapacity(UPDATED_CAPACITY);
        updatedSeasonTicketType.setIndex(UPDATED_INDEX);
        updatedSeasonTicketType.setAttendingType(UPDATED_ATTENDING_TYPE);

        restSeasonTicketTypeMockMvc.perform(put("/api/season-ticket-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSeasonTicketType)))
                .andExpect(status().isOk());

        // Validate the SeasonTicketType in the database
        List<SeasonTicketType> seasonTicketTypes = seasonTicketTypeRepository.findAll();
        assertThat(seasonTicketTypes).hasSize(databaseSizeBeforeUpdate);
        SeasonTicketType testSeasonTicketType = seasonTicketTypes.get(seasonTicketTypes.size() - 1);
        assertThat(testSeasonTicketType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSeasonTicketType.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testSeasonTicketType.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testSeasonTicketType.getCapacity()).isEqualTo(UPDATED_CAPACITY);
        assertThat(testSeasonTicketType.getIndex()).isEqualTo(UPDATED_INDEX);
        assertThat(testSeasonTicketType.getAttendingType()).isEqualTo(UPDATED_ATTENDING_TYPE);
    }

    @Test
    @Transactional
    public void deleteSeasonTicketType() throws Exception {
        // Initialize the database
        seasonTicketTypeService.save(seasonTicketType);

        int databaseSizeBeforeDelete = seasonTicketTypeRepository.findAll().size();

        // Get the seasonTicketType
        restSeasonTicketTypeMockMvc.perform(delete("/api/season-ticket-types/{id}", seasonTicketType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<SeasonTicketType> seasonTicketTypes = seasonTicketTypeRepository.findAll();
        assertThat(seasonTicketTypes).hasSize(databaseSizeBeforeDelete - 1);
    }
}
