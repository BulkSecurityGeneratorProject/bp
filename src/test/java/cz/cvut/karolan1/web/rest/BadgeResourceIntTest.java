package cz.cvut.karolan1.web.rest;

import cz.cvut.karolan1.Bp250App;
import cz.cvut.karolan1.domain.Badge;
import cz.cvut.karolan1.repository.BadgeRepository;

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
 * Test class for the BadgeResource REST controller.
 *
 * @see BadgeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Bp250App.class)
@WebAppConfiguration
@IntegrationTest
public class BadgeResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final ZonedDateTime DEFAULT_EARNED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_EARNED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_EARNED_AT_STR = dateTimeFormatter.format(DEFAULT_EARNED_AT);

    @Inject
    private BadgeRepository badgeRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restBadgeMockMvc;

    private Badge badge;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BadgeResource badgeResource = new BadgeResource();
        ReflectionTestUtils.setField(badgeResource, "badgeRepository", badgeRepository);
        this.restBadgeMockMvc = MockMvcBuilders.standaloneSetup(badgeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        badge = new Badge();
        badge.setEarnedAt(DEFAULT_EARNED_AT);
    }

    @Test
    @Transactional
    public void createBadge() throws Exception {
        int databaseSizeBeforeCreate = badgeRepository.findAll().size();

        // Create the Badge

        restBadgeMockMvc.perform(post("/api/badges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(badge)))
                .andExpect(status().isCreated());

        // Validate the Badge in the database
        List<Badge> badges = badgeRepository.findAll();
        assertThat(badges).hasSize(databaseSizeBeforeCreate + 1);
        Badge testBadge = badges.get(badges.size() - 1);
        assertThat(testBadge.getEarnedAt()).isEqualTo(DEFAULT_EARNED_AT);
    }

    @Test
    @Transactional
    public void getAllBadges() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badges
        restBadgeMockMvc.perform(get("/api/badges?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(badge.getId().intValue())))
                .andExpect(jsonPath("$.[*].earnedAt").value(hasItem(DEFAULT_EARNED_AT_STR)));
    }

    @Test
    @Transactional
    public void getBadge() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get the badge
        restBadgeMockMvc.perform(get("/api/badges/{id}", badge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(badge.getId().intValue()))
            .andExpect(jsonPath("$.earnedAt").value(DEFAULT_EARNED_AT_STR));
    }

    @Test
    @Transactional
    public void getNonExistingBadge() throws Exception {
        // Get the badge
        restBadgeMockMvc.perform(get("/api/badges/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBadge() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);
        int databaseSizeBeforeUpdate = badgeRepository.findAll().size();

        // Update the badge
        Badge updatedBadge = new Badge();
        updatedBadge.setId(badge.getId());
        updatedBadge.setEarnedAt(UPDATED_EARNED_AT);

        restBadgeMockMvc.perform(put("/api/badges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedBadge)))
                .andExpect(status().isOk());

        // Validate the Badge in the database
        List<Badge> badges = badgeRepository.findAll();
        assertThat(badges).hasSize(databaseSizeBeforeUpdate);
        Badge testBadge = badges.get(badges.size() - 1);
        assertThat(testBadge.getEarnedAt()).isEqualTo(UPDATED_EARNED_AT);
    }

    @Test
    @Transactional
    public void deleteBadge() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);
        int databaseSizeBeforeDelete = badgeRepository.findAll().size();

        // Get the badge
        restBadgeMockMvc.perform(delete("/api/badges/{id}", badge.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Badge> badges = badgeRepository.findAll();
        assertThat(badges).hasSize(databaseSizeBeforeDelete - 1);
    }
}
