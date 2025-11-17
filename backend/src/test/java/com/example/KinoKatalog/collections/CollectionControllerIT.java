package com.example.KinoKatalog.collections;

import com.example.KinoKatalog.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CollectionControllerIT extends TestContainersConfig {

    @Autowired
    private MockMvc mockMvc;


    private String basicAuth(String username, String password) {
        String pair = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(pair.getBytes());
    }


    @Test
    void createCollection_shouldReturn201() throws Exception {
        String body = """
            {
              "name": "Favorites",
              "description": "My cool test list",
              "userId": 1
            }
        """;

        mockMvc.perform(post("/api/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", basicAuth("admin", "adminpass"))
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Favorites"));
    }

    @Test
    void getCollectionById_shouldReturn200() throws Exception {
        // first create
        String body = """
            {
              "name": "WatchLater",
              "description": "Stuff I will watch",
              "userId": 1
            }
        """;

        var result = mockMvc.perform(post("/api/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", basicAuth("admin", "adminpass"))
                        .content(body))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String id = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(get("/api/collections/" + id)
                        .header("Authorization", basicAuth("admin", "adminpass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("WatchLater"));
    }
}
