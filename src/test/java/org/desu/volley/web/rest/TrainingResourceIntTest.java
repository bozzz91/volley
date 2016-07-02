package org.desu.volley.web.rest;

import org.desu.volley.VolleyApp;
import org.desu.volley.domain.Training;
import org.desu.volley.repository.TrainingRepository;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.desu.volley.domain.enumeration.TrainingState;

/**
 * Test class for the TrainingResource REST controller.
 *
 * @see TrainingResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = VolleyApp.class)
@WebAppConfiguration
@IntegrationTest
public class TrainingResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final ZonedDateTime DEFAULT_START_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_START_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_START_AT_STR = dateTimeFormatter.format(DEFAULT_START_AT);

    private static final ZonedDateTime DEFAULT_END_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_END_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_END_AT_STR = dateTimeFormatter.format(DEFAULT_END_AT);

    private static final Integer DEFAULT_PRICE = 0;
    private static final Integer UPDATED_PRICE = 1;

    private static final TrainingState DEFAULT_STATE = TrainingState.REGISTRATION;
    private static final TrainingState UPDATED_STATE = TrainingState.PROCESS;
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private TrainingRepository trainingRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTrainingMockMvc;

    private Training training;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TrainingResource trainingResource = new TrainingResource();
        ReflectionTestUtils.setField(trainingResource, "trainingRepository", trainingRepository);
        this.restTrainingMockMvc = MockMvcBuilders.standaloneSetup(trainingResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        training = new Training();
        training.setStartAt(DEFAULT_START_AT);
        training.setEndAt(DEFAULT_END_AT);
        training.setPrice(DEFAULT_PRICE);
        training.setState(DEFAULT_STATE);
        training.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createTraining() throws Exception {
        int databaseSizeBeforeCreate = trainingRepository.findAll().size();

        // Create the Training

        restTrainingMockMvc.perform(post("/api/trainings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(training)))
                .andExpect(status().isCreated());

        // Validate the Training in the database
        List<Training> trainings = trainingRepository.findAll();
        assertThat(trainings).hasSize(databaseSizeBeforeCreate + 1);
        Training testTraining = trainings.get(trainings.size() - 1);
        assertThat(testTraining.getStartAt()).isEqualTo(DEFAULT_START_AT);
        assertThat(testTraining.getEndAt()).isEqualTo(DEFAULT_END_AT);
        assertThat(testTraining.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testTraining.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testTraining.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void checkStartAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = trainingRepository.findAll().size();
        // set the field null
        training.setStartAt(null);

        // Create the Training, which fails.

        restTrainingMockMvc.perform(post("/api/trainings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(training)))
                .andExpect(status().isBadRequest());

        List<Training> trainings = trainingRepository.findAll();
        assertThat(trainings).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEndAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = trainingRepository.findAll().size();
        // set the field null
        training.setEndAt(null);

        // Create the Training, which fails.

        restTrainingMockMvc.perform(post("/api/trainings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(training)))
                .andExpect(status().isBadRequest());

        List<Training> trainings = trainingRepository.findAll();
        assertThat(trainings).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = trainingRepository.findAll().size();
        // set the field null
        training.setPrice(null);

        // Create the Training, which fails.

        restTrainingMockMvc.perform(post("/api/trainings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(training)))
                .andExpect(status().isBadRequest());

        List<Training> trainings = trainingRepository.findAll();
        assertThat(trainings).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStateIsRequired() throws Exception {
        int databaseSizeBeforeTest = trainingRepository.findAll().size();
        // set the field null
        training.setState(null);

        // Create the Training, which fails.

        restTrainingMockMvc.perform(post("/api/trainings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(training)))
                .andExpect(status().isBadRequest());

        List<Training> trainings = trainingRepository.findAll();
        assertThat(trainings).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTrainings() throws Exception {
        // Initialize the database
        trainingRepository.saveAndFlush(training);

        // Get all the trainings
        restTrainingMockMvc.perform(get("/api/trainings?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(training.getId().intValue())))
                .andExpect(jsonPath("$.[*].startAt").value(hasItem(DEFAULT_START_AT_STR)))
                .andExpect(jsonPath("$.[*].endAt").value(hasItem(DEFAULT_END_AT_STR)))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
                .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getTraining() throws Exception {
        // Initialize the database
        trainingRepository.saveAndFlush(training);

        // Get the training
        restTrainingMockMvc.perform(get("/api/trainings/{id}", training.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(training.getId().intValue()))
            .andExpect(jsonPath("$.startAt").value(DEFAULT_START_AT_STR))
            .andExpect(jsonPath("$.endAt").value(DEFAULT_END_AT_STR))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTraining() throws Exception {
        // Get the training
        restTrainingMockMvc.perform(get("/api/trainings/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTraining() throws Exception {
        // Initialize the database
        trainingRepository.saveAndFlush(training);
        int databaseSizeBeforeUpdate = trainingRepository.findAll().size();

        // Update the training
        Training updatedTraining = new Training();
        updatedTraining.setId(training.getId());
        updatedTraining.setStartAt(UPDATED_START_AT);
        updatedTraining.setEndAt(UPDATED_END_AT);
        updatedTraining.setPrice(UPDATED_PRICE);
        updatedTraining.setState(UPDATED_STATE);
        updatedTraining.setDescription(UPDATED_DESCRIPTION);

        restTrainingMockMvc.perform(put("/api/trainings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTraining)))
                .andExpect(status().isOk());

        // Validate the Training in the database
        List<Training> trainings = trainingRepository.findAll();
        assertThat(trainings).hasSize(databaseSizeBeforeUpdate);
        Training testTraining = trainings.get(trainings.size() - 1);
        assertThat(testTraining.getStartAt()).isEqualTo(UPDATED_START_AT);
        assertThat(testTraining.getEndAt()).isEqualTo(UPDATED_END_AT);
        assertThat(testTraining.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testTraining.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testTraining.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteTraining() throws Exception {
        // Initialize the database
        trainingRepository.saveAndFlush(training);
        int databaseSizeBeforeDelete = trainingRepository.findAll().size();

        // Get the training
        restTrainingMockMvc.perform(delete("/api/trainings/{id}", training.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Training> trainings = trainingRepository.findAll();
        assertThat(trainings).hasSize(databaseSizeBeforeDelete - 1);
    }
}
