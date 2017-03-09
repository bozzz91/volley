package org.desu.volley.web.rest;

import org.desu.volley.VolleyApp;
import org.desu.volley.domain.Faq;
import org.desu.volley.repository.FaqRepository;

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
 * Test class for the FaqResource REST controller.
 *
 * @see FaqResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = VolleyApp.class)
@WebAppConfiguration
@IntegrationTest
public class FaqResourceIntTest {

    private static final Integer DEFAULT_INDEX = 0;
    private static final Integer UPDATED_INDEX = 1;
    private static final String DEFAULT_QUESTION = "AAAAA";
    private static final String UPDATED_QUESTION = "BBBBB";
    private static final String DEFAULT_ANSWER = "AAAAA";
    private static final String UPDATED_ANSWER = "BBBBB";

    @Inject
    private FaqRepository faqRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restFaqMockMvc;

    private Faq faq;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FaqResource faqResource = new FaqResource();
        ReflectionTestUtils.setField(faqResource, "faqRepository", faqRepository);
        this.restFaqMockMvc = MockMvcBuilders.standaloneSetup(faqResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        faq = new Faq();
        faq.setIndex(DEFAULT_INDEX);
        faq.setQuestion(DEFAULT_QUESTION);
        faq.setAnswer(DEFAULT_ANSWER);
    }

    @Test
    @Transactional
    public void createFaq() throws Exception {
        int databaseSizeBeforeCreate = faqRepository.findAll().size();

        // Create the Faq

        restFaqMockMvc.perform(post("/api/faqs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(faq)))
                .andExpect(status().isCreated());

        // Validate the Faq in the database
        List<Faq> faqs = faqRepository.findAll();
        assertThat(faqs).hasSize(databaseSizeBeforeCreate + 1);
        Faq testFaq = faqs.get(faqs.size() - 1);
        assertThat(testFaq.getIndex()).isEqualTo(DEFAULT_INDEX);
        assertThat(testFaq.getQuestion()).isEqualTo(DEFAULT_QUESTION);
        assertThat(testFaq.getAnswer()).isEqualTo(DEFAULT_ANSWER);
    }

    @Test
    @Transactional
    public void checkIndexIsRequired() throws Exception {
        int databaseSizeBeforeTest = faqRepository.findAll().size();
        // set the field null
        faq.setIndex(null);

        // Create the Faq, which fails.

        restFaqMockMvc.perform(post("/api/faqs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(faq)))
                .andExpect(status().isBadRequest());

        List<Faq> faqs = faqRepository.findAll();
        assertThat(faqs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkQuestionIsRequired() throws Exception {
        int databaseSizeBeforeTest = faqRepository.findAll().size();
        // set the field null
        faq.setQuestion(null);

        // Create the Faq, which fails.

        restFaqMockMvc.perform(post("/api/faqs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(faq)))
                .andExpect(status().isBadRequest());

        List<Faq> faqs = faqRepository.findAll();
        assertThat(faqs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAnswerIsRequired() throws Exception {
        int databaseSizeBeforeTest = faqRepository.findAll().size();
        // set the field null
        faq.setAnswer(null);

        // Create the Faq, which fails.

        restFaqMockMvc.perform(post("/api/faqs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(faq)))
                .andExpect(status().isBadRequest());

        List<Faq> faqs = faqRepository.findAll();
        assertThat(faqs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFaqs() throws Exception {
        // Initialize the database
        faqRepository.saveAndFlush(faq);

        // Get all the faqs
        restFaqMockMvc.perform(get("/api/faqs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(faq.getId().intValue())))
                .andExpect(jsonPath("$.[*].index").value(hasItem(DEFAULT_INDEX)))
                .andExpect(jsonPath("$.[*].question").value(hasItem(DEFAULT_QUESTION)))
                .andExpect(jsonPath("$.[*].answer").value(hasItem(DEFAULT_ANSWER)));
    }

    @Test
    @Transactional
    public void getFaq() throws Exception {
        // Initialize the database
        faqRepository.saveAndFlush(faq);

        // Get the faq
        restFaqMockMvc.perform(get("/api/faqs/{id}", faq.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(faq.getId().intValue()))
            .andExpect(jsonPath("$.index").value(DEFAULT_INDEX))
            .andExpect(jsonPath("$.question").value(DEFAULT_QUESTION))
            .andExpect(jsonPath("$.answer").value(DEFAULT_ANSWER));
    }

    @Test
    @Transactional
    public void getNonExistingFaq() throws Exception {
        // Get the faq
        restFaqMockMvc.perform(get("/api/faqs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFaq() throws Exception {
        // Initialize the database
        faqRepository.saveAndFlush(faq);
        int databaseSizeBeforeUpdate = faqRepository.findAll().size();

        // Update the faq
        Faq updatedFaq = new Faq();
        updatedFaq.setId(faq.getId());
        updatedFaq.setIndex(UPDATED_INDEX);
        updatedFaq.setQuestion(UPDATED_QUESTION);
        updatedFaq.setAnswer(UPDATED_ANSWER);

        restFaqMockMvc.perform(put("/api/faqs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedFaq)))
                .andExpect(status().isOk());

        // Validate the Faq in the database
        List<Faq> faqs = faqRepository.findAll();
        assertThat(faqs).hasSize(databaseSizeBeforeUpdate);
        Faq testFaq = faqs.get(faqs.size() - 1);
        assertThat(testFaq.getIndex()).isEqualTo(UPDATED_INDEX);
        assertThat(testFaq.getQuestion()).isEqualTo(UPDATED_QUESTION);
        assertThat(testFaq.getAnswer()).isEqualTo(UPDATED_ANSWER);
    }

    @Test
    @Transactional
    public void deleteFaq() throws Exception {
        // Initialize the database
        faqRepository.saveAndFlush(faq);
        int databaseSizeBeforeDelete = faqRepository.findAll().size();

        // Get the faq
        restFaqMockMvc.perform(delete("/api/faqs/{id}", faq.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Faq> faqs = faqRepository.findAll();
        assertThat(faqs).hasSize(databaseSizeBeforeDelete - 1);
    }
}
