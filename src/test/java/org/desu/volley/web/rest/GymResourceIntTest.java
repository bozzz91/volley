package org.desu.volley.web.rest;

import org.desu.volley.VolleyApp;
import org.desu.volley.domain.Gym;
import org.desu.volley.repository.GymRepository;

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


/**
 * Test class for the GymResource REST controller.
 *
 * @see GymResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = VolleyApp.class)
@WebAppConfiguration
@IntegrationTest
public class GymResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_LOCATION = "AAAAA";
    private static final String UPDATED_LOCATION = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private GymRepository gymRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restGymMockMvc;

    private Gym gym;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        GymResource gymResource = new GymResource();
        ReflectionTestUtils.setField(gymResource, "gymRepository", gymRepository);
        this.restGymMockMvc = MockMvcBuilders.standaloneSetup(gymResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        gym = new Gym();
        gym.setName(DEFAULT_NAME);
        gym.setLocation(DEFAULT_LOCATION);
        gym.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createGym() throws Exception {
        int databaseSizeBeforeCreate = gymRepository.findAll().size();

        // Create the Gym

        restGymMockMvc.perform(post("/api/gyms")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(gym)))
                .andExpect(status().isCreated());

        // Validate the Gym in the database
        List<Gym> gyms = gymRepository.findAll();
        assertThat(gyms).hasSize(databaseSizeBeforeCreate + 1);
        Gym testGym = gyms.get(gyms.size() - 1);
        assertThat(testGym.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testGym.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testGym.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = gymRepository.findAll().size();
        // set the field null
        gym.setName(null);

        // Create the Gym, which fails.

        restGymMockMvc.perform(post("/api/gyms")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(gym)))
                .andExpect(status().isBadRequest());

        List<Gym> gyms = gymRepository.findAll();
        assertThat(gyms).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllGyms() throws Exception {
        // Initialize the database
        gymRepository.saveAndFlush(gym);

        // Get all the gyms
        restGymMockMvc.perform(get("/api/gyms?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(gym.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getGym() throws Exception {
        // Initialize the database
        gymRepository.saveAndFlush(gym);

        // Get the gym
        restGymMockMvc.perform(get("/api/gyms/{id}", gym.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(gym.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingGym() throws Exception {
        // Get the gym
        restGymMockMvc.perform(get("/api/gyms/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGym() throws Exception {
        // Initialize the database
        gymRepository.saveAndFlush(gym);
        int databaseSizeBeforeUpdate = gymRepository.findAll().size();

        // Update the gym
        Gym updatedGym = new Gym();
        updatedGym.setId(gym.getId());
        updatedGym.setName(UPDATED_NAME);
        updatedGym.setLocation(UPDATED_LOCATION);
        updatedGym.setDescription(UPDATED_DESCRIPTION);

        restGymMockMvc.perform(put("/api/gyms")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedGym)))
                .andExpect(status().isOk());

        // Validate the Gym in the database
        List<Gym> gyms = gymRepository.findAll();
        assertThat(gyms).hasSize(databaseSizeBeforeUpdate);
        Gym testGym = gyms.get(gyms.size() - 1);
        assertThat(testGym.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testGym.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testGym.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteGym() throws Exception {
        // Initialize the database
        gymRepository.saveAndFlush(gym);
        int databaseSizeBeforeDelete = gymRepository.findAll().size();

        // Get the gym
        restGymMockMvc.perform(delete("/api/gyms/{id}", gym.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Gym> gyms = gymRepository.findAll();
        assertThat(gyms).hasSize(databaseSizeBeforeDelete - 1);
    }
}
