package org.desu.volley.web.rest;

import org.desu.volley.VolleyApp;
import org.desu.volley.domain.Sms;
import org.desu.volley.repository.SmsRepository;
import org.desu.volley.service.SmsService;

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
 * Test class for the SmsResource REST controller.
 *
 * @see SmsResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = VolleyApp.class)
@WebAppConfiguration
@IntegrationTest
public class SmsResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_TEXT = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_SEND_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_SEND_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_SEND_DATE_STR = dateTimeFormatter.format(DEFAULT_SEND_DATE);

    @Inject
    private SmsRepository smsRepository;

    @Inject
    private SmsService smsService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSmsMockMvc;

    private Sms sms;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SmsResource smsResource = new SmsResource();
        ReflectionTestUtils.setField(smsResource, "smsService", smsService);
        this.restSmsMockMvc = MockMvcBuilders.standaloneSetup(smsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        sms = new Sms();
        sms.setText(DEFAULT_TEXT);
        sms.setSendDate(DEFAULT_SEND_DATE);
    }

    @Test
    @Transactional
    public void createSms() throws Exception {
        int databaseSizeBeforeCreate = smsRepository.findAll().size();

        // Create the Sms

        restSmsMockMvc.perform(post("/api/sms")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sms)))
                .andExpect(status().isCreated());

        // Validate the Sms in the database
        List<Sms> sms = smsRepository.findAll();
        assertThat(sms).hasSize(databaseSizeBeforeCreate + 1);
        Sms testSms = sms.get(sms.size() - 1);
        assertThat(testSms.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testSms.getSendDate()).isEqualTo(DEFAULT_SEND_DATE);
    }

    @Test
    @Transactional
    public void checkTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = smsRepository.findAll().size();
        // set the field null
        sms.setText(null);

        // Create the Sms, which fails.

        restSmsMockMvc.perform(post("/api/sms")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sms)))
                .andExpect(status().isBadRequest());

        List<Sms> sms = smsRepository.findAll();
        assertThat(sms).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSendDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = smsRepository.findAll().size();
        // set the field null
        sms.setSendDate(null);

        // Create the Sms, which fails.

        restSmsMockMvc.perform(post("/api/sms")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sms)))
                .andExpect(status().isBadRequest());

        List<Sms> sms = smsRepository.findAll();
        assertThat(sms).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSms() throws Exception {
        // Initialize the database
        smsRepository.saveAndFlush(sms);

        // Get all the sms
        restSmsMockMvc.perform(get("/api/sms?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(sms.getId().intValue())))
                .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())))
                .andExpect(jsonPath("$.[*].sendDate").value(hasItem(DEFAULT_SEND_DATE_STR)));
    }

    @Test
    @Transactional
    public void getSms() throws Exception {
        // Initialize the database
        smsRepository.saveAndFlush(sms);

        // Get the sms
        restSmsMockMvc.perform(get("/api/sms/{id}", sms.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(sms.getId().intValue()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()))
            .andExpect(jsonPath("$.sendDate").value(DEFAULT_SEND_DATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingSms() throws Exception {
        // Get the sms
        restSmsMockMvc.perform(get("/api/sms/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSms() throws Exception {
        // Initialize the database
        smsService.save(sms, false);

        int databaseSizeBeforeUpdate = smsRepository.findAll().size();

        // Update the sms
        Sms updatedSms = new Sms();
        updatedSms.setId(sms.getId());
        updatedSms.setText(UPDATED_TEXT);
        updatedSms.setSendDate(UPDATED_SEND_DATE);

        restSmsMockMvc.perform(put("/api/sms")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSms)))
                .andExpect(status().isOk());

        // Validate the Sms in the database
        List<Sms> sms = smsRepository.findAll();
        assertThat(sms).hasSize(databaseSizeBeforeUpdate);
        Sms testSms = sms.get(sms.size() - 1);
        assertThat(testSms.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testSms.getSendDate()).isEqualTo(UPDATED_SEND_DATE);
    }

    @Test
    @Transactional
    public void deleteSms() throws Exception {
        // Initialize the database
        smsService.save(sms, false);

        int databaseSizeBeforeDelete = smsRepository.findAll().size();

        // Get the sms
        restSmsMockMvc.perform(delete("/api/sms/{id}", sms.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Sms> sms = smsRepository.findAll();
        assertThat(sms).hasSize(databaseSizeBeforeDelete - 1);
    }
}
