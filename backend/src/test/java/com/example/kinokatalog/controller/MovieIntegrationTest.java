package com.example.kinokatalog.controller;

import com.example.kinokatalog.dto.MovieDTO;
import com.example.kinokatalog.persistence.sql.entity.MovieEntity;
import com.example.kinokatalog.persistence.sql.repository.MovieSqlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MovieIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MovieSqlRepository movieRepo;

    private Integer movieId;

    @BeforeEach
    void setup() {
        // seed one movie for GET /id, GET all, search, PUT, DELETE tests
        MovieEntity m = new MovieEntity();
        m.setTitle("Inception");
        m.setOverview("Dream heist.");
        m.setRuntime(148);
        m.setPosterUrl("http://poster.com/x.jpg");
        movieRepo.save(m);
        movieId = m.getId();
    }

    private String movieJson(String title, String overview, int runtime, String url) {
        return """
        {
            "title": "%s",
            "overview": "%s",
            "runtime": %d,
            "posterUrl": "%s"
        }
        """.formatted(title, overview, runtime, url);
    }

    // ---------------------------------------------------------
    // GET ALL MOVIES
    // ---------------------------------------------------------
    @Test
    void getAllMovies_returnsList() throws Exception {
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Inception"));
    }

    // ---------------------------------------------------------
    // GET MOVIE BY ID
    // ---------------------------------------------------------
    @Test
    void getMovieById_returnsMovie() throws Exception {
        mockMvc.perform(get("/api/movies/" + movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void getMovieById_notFound() throws Exception {
        mockMvc.perform(get("/api/movies/99999"))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    // CREATE MOVIE (ADMIN ONLY)
    // ---------------------------------------------------------
    /*
    @Test
    void createMovie_unauthenticated_forbidden() throws Exception {
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson("Test", "Desc", 100, "http://img"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
*/

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createMovie_success() throws Exception {
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson("Matrix", "Neo saves world", 130, "http://matrix"))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Matrix"));

        assertThat(movieRepo.findByTitleContainingIgnoreCase("matrix")).isNotEmpty();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createMovie_htmlEscapingApplied() throws Exception {
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson("<b>Bad</b>", "<script>x</script>", 99, "<img>"))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("&lt;b&gt;Bad&lt;/b&gt;"))
                .andExpect(jsonPath("$.overview").value("&lt;script&gt;x&lt;/script&gt;"))
                .andExpect(jsonPath("$.posterUrl").value("&lt;img&gt;"));
    }

    // ---------------------------------------------------------
    // UPDATE MOVIE (ADMIN ONLY)
    // ---------------------------------------------------------
    /*
    @Test
    void updateMovie_unauthenticated_forbidden() throws Exception {
        mockMvc.perform(put("/api/movies/" + movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson("Updated", "Updated", 120, "url"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }*/

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateMovie_success() throws Exception {
        mockMvc.perform(put("/api/movies/" + movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson("Updated Title", "New Overview", 150, "url"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));

        MovieEntity saved = movieRepo.findById(movieId).orElseThrow();
        assertThat(saved.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateMovie_notFound() throws Exception {
        mockMvc.perform(put("/api/movies/987654")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson("X", "Y", 50, "url")))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    // DELETE MOVIE (ADMIN ONLY)
    // ---------------------------------------------------------
    /*
    @Test
    void deleteMovie_unauthenticated_forbidden() throws Exception {
        mockMvc.perform(delete("/api/movies/" + movieId).with(csrf()))
                .andExpect(status().isForbidden());
    }*/

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteMovie_success() throws Exception {
        mockMvc.perform(delete("/api/movies/" + movieId).with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(movieRepo.existsById(movieId)).isFalse();
    }

    // ---------------------------------------------------------
    // SEARCH ENDPOINT
    // ---------------------------------------------------------
    @Test
    void searchMovies_returnsMatches() throws Exception {
        mockMvc.perform(get("/api/movies/search?q=incep"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Inception"));
    }
}
