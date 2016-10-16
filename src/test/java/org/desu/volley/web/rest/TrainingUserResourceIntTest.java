package org.desu.volley.web.rest;

import org.desu.volley.VolleyApp;
import org.desu.volley.domain.TrainingUser;
import org.desu.volley.repository.TrainingUserRepository;

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


/**
 * Test class for the TrainingUserResource REST controller.
 *
 * @see TrainingUserResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = VolleyApp.class)
@WebAppConfiguration
@IntegrationTest
public class TrainingUserResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final ZonedDateTime DEFAULT_REGISTER_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_REGISTER_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_REGISTER_DATE_STR = dateTimeFormatter.format(DEFAULT_REGISTER_DATE);

    @Inject
    private TrainingUserRepository trainingUserRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTrainingUserMockMvc;

    private TrainingUser trainingUser;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TrainingUserResource trainingUserResource = new TrainingUserResource();
        ReflectionTestUtils.setField(trainingUserResource, "trainingUserRepository", trainingUserRepository);
        this.restTrainingUserMockMvc = MockMvcBuilders.standaloneSetup(trainingUserResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        trainingUser = new TrainingUser();
        trainingUser.setRegisterDate(DEFAULT_REGISTER_DATE);
    }

    @Test
    @Transactional
    public void createTrainingUser() throws Exception {
        int databaseSizeBeforeCreate = trainingUserRepository.findAll().size();

        // Create the TrainingUser

        restTrainingUserMockMvc.perform(post("/api/training-users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(trainingUser)))
                .andExpect(status().isCreated());

        // Validate the TrainingUser in the database
        List<TrainingUser> trainingUsers = trainingUserRepository.findAll();
        assertThat(trainingUsers).hasSize(databaseSizeBeforeCreate + 1);
        TrainingUser testTrainingUser = trainingUsers.get(trainingUsers.size() - 1);
        assertThat(testTrainingUser.getRegisterDate()).isEqualTo(DEFAULT_REGISTER_DATE);
    }

    @Test
    @Transactional
    public void getAllTrainingUsers() throws Exception {
        // Initialize the database
        trainingUserRepository.saveAndFlush(trainingUser);

        // Get all the trainingUsers
        restTrainingUserMockMvc.perform(get("/api/training-users?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(trainingUser.getId().intValue())))
                .andExpect(jsonPath("$.[*].registerDate").value(hasItem(DEFAULT_REGISTER_DATE_STR)));
    }

    @Test
    @Transactional
    public void getTrainingUser() throws Exception {
        // Initialize the database
        trainingUserRepository.saveAndFlush(trainingUser);

        // Get the trainingUser
        restTrainingUserMockMvc.perform(get("/api/training-users/{id}", trainingUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(trainingUser.getId().intValue()))
            .andExpect(jsonPath("$.registerDate").value(DEFAULT_REGISTER_DATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingTrainingUser() throws Exception {
        // Get the trainingUser
        restTrainingUserMockMvc.perform(get("/api/training-users/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTrainingUser() throws Exception {
        // Initialize the database
        trainingUserRepository.saveAndFlush(trainingUser);
        int databaseSizeBeforeUpdate = trainingUserRepository.findAll().size();

        // Update the trainingUser
        TrainingUser updatedTrainingUser = new TrainingUser();
        updatedTrainingUser.setId(trainingUser.getId());
        updatedTrainingUser.setRegisterDate(UPDATED_REGISTER_DATE);

        restTrainingUserMockMvc.perform(put("/api/training-users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTrainingUser)))
                .andExpect(status().isOk());

        // Validate the TrainingUser in the database
        List<TrainingUser> trainingUsers = trainingUserRepository.findAll();
        assertThat(trainingUsers).hasSize(databaseSizeBeforeUpdate);
        TrainingUser testTrainingUser = trainingUsers.get(trainingUsers.size() - 1);
        assertThat(testTrainingUser.getRegisterDate()).isEqualTo(UPDATED_REGISTER_DATE);
    }

    @Test
    @Transactional
    public void deleteTrainingUser() throws Exception {
        // Initialize the database
        trainingUserRepository.saveAndFlush(trainingUser);
        int databaseSizeBeforeDelete = trainingUserRepository.findAll().size();

        // Get the trainingUser
        restTrainingUserMockMvc.perform(delete("/api/training-users/{id}", trainingUser.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<TrainingUser> trainingUsers = trainingUserRepository.findAll();
        assertThat(trainingUsers).hasSize(databaseSizeBeforeDelete - 1);
    }
}
