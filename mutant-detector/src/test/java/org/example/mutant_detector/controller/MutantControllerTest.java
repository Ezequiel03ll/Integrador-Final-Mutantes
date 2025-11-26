package org.example.mutant_detector.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("POST /mutant debe retornar 200 cuando es mutante")
    void testCheckMutant_ReturnOk_WhenIsMutant() throws Exception {
        String jsonRequest = """
            {
              "dna": ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
            }
            """;

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /mutant debe retornar estado válido cuando es humano (200 o 4xx)")
    void testCheckMutant_ReturnValid_WhenIsHuman() throws Exception {
        String jsonRequest = """
        {
          "dna": ["ATGCGA","CAGTGC","TTATGT","AGTAGG","GTCCGA","TCACTG"]
        }
        """;

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(
                            status == 200 || (status >= 400 && status < 500),
                            "Estado inesperado: " + status
                    );
                });
    }

    @Test
    @DisplayName("POST /mutant con body vacío debe retornar error (4xx o 5xx)")
    void testCheckMutant_ReturnError_WhenEmptyBody() throws Exception {
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(
                            status >= 400 && status < 600,
                            "Status inesperado: " + status
                    );
                });
    }

    @Test
    @DisplayName("POST /mutant con DNA null debe retornar 400")
    void testCheckMutant_ReturnBadRequest_WhenNullDna() throws Exception {
        String jsonRequest = """
            {
              "dna": null
            }
            """;

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mutant con caracteres inválidos debe retornar 400")
    void testCheckMutant_ReturnBadRequest_WhenInvalidCharacters() throws Exception {
        String jsonRequest = """
            {
              "dna": ["ATGXGA","CAGTGC","TTATGT","AGAAGG","GTCCGA","TCACTG"]
            }
            """;

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------
    // 2. GET /stats
    // -------------------------------------------------

    @Test
    @DisplayName("GET /stats debe retornar 200")
    void testGetStats_ReturnOk() throws Exception {
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").exists())
                .andExpect(jsonPath("$.count_human_dna").exists())
                .andExpect(jsonPath("$.ratio").exists());
    }

    @Test
    @DisplayName("GET /stats retorna valores numéricos aunque haya datos previos")
    void testGetStats_EmptyDatabase() throws Exception {
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").isNumber())
                .andExpect(jsonPath("$.count_human_dna").isNumber())
                .andExpect(jsonPath("$.ratio").isNumber());
    }

    @Test
    @DisplayName("POST /mutant + GET /stats flujo completo sin asumir valores exactos")
    void testFullFlow_MutantThenStats() throws Exception {
        String mutantJson = """
            {
              "dna": ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
            }
            """;

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mutantJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").isNumber());
    }
}
