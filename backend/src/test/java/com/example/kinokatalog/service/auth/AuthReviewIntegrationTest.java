package com.example.kinokatalog.service.auth;

import com.example.kinokatalog.persistence.sql.entity.MovieEntity;
import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import com.example.kinokatalog.persistence.sql.repository.MovieSqlRepository;
import com.example.kinokatalog.persistence.sql.repository.ReviewSqlRepository;
import com.example.kinokatalog.persistence.sql.repository.UserSqlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ReviewControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserSqlRepository userRepo;
    @Autowired MovieSqlRepository movieRepo;
    @Autowired ReviewSqlRepository reviewRepo;

    private UserEntity user;
    private MovieEntity movie;

    @BeforeEach
    void setup() {
        user = new UserEntity(null, "reviewUser", "review@mail.com", "pw", null, "USER", null, "salt");
        user = userRepo.save(user);

        movie = new MovieEntity();
        movie.setTitle("Test Movie");
        movie.setTmdbId(1001);
        movie = movieRepo.save(movie);
    }

    private String reviewBody(int rating, String text, String username) {
        return """
                {
                  "rating": %d,
                  "reviewText": "%s",
                  "username": "%s"
                }
                """.formatted(rating, text, username);
    }

    // ------------------------------------------------------------
    // SUCCESS: create review
    // ------------------------------------------------------------
    @Test
    void createReview_successful() throws Exception {
        mockMvc.perform(
                        post("/api/reviews/movie/" + movie.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reviewBody(8, "Great!", user.getUsername()))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(8))
                .andExpect(jsonPath("$.reviewText").value("Great!"))
                .andExpect(jsonPath("$.movieId").value(movie.getId()));

        var reviews = reviewRepo.findByMovieEntity_Id(movie.getId());
        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getRating()).isEqualTo(8);
    }

    // ------------------------------------------------------------
    // MISSING USERNAME (InvalidDataException)
    // ------------------------------------------------------------
    @Test
    void createReview_missingUsername_returns403() throws Exception {
        mockMvc.perform(
                        post("/api/reviews/movie/" + movie.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reviewBody(7, "Missing username", null))
                )
                .andExpect(status().isForbidden());
    }

    // ------------------------------------------------------------
    // MOVIE NOT FOUND
    // ------------------------------------------------------------
    @Test
    void createReview_nonexistentMovie_returns404() throws Exception {
        int nonExistingId = 999999;

        mockMvc.perform(
                        post("/api/reviews/movie/" + nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reviewBody(7, "Test", user.getUsername()))
                )
                .andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------
    // FETCH REVIEWS
    // ------------------------------------------------------------
    @Test
    void getReviewsByMovie_returnsList() throws Exception {

        // create a review
        mockMvc.perform(
                        post("/api/reviews/movie/" + movie.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reviewBody(9, "Amazing", user.getUsername()))
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        get("/api/reviews/movie/" + movie.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(9))
                .andExpect(jsonPath("$[0].reviewText").value("Amazing"));
    }
}
