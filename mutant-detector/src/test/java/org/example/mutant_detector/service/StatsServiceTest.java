package org.example.mutant_detector.service;
import org.example.mutant_detector.dto.StatsResponse;
import org.example.mutant_detector.repository.DnaRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class StatsServiceTest {

    @Mock
    private DnaRecordRepository repository;

    @InjectMocks
    private StatsService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debe calcular estadísticas correctamente")
    void testGetStats() {
        when(repository.countByIsMutant(true)).thenReturn(10L);
        when(repository.countByIsMutant(false)).thenReturn(5L);

        StatsResponse response = service.getStats();

        assertEquals(10L, response.getCount_mutant_dna());
        assertEquals(5L, response.getCount_human_dna());
        assertEquals(2.0, response.getRatio());
    }

    @Test
    @DisplayName("Ratio debe ser 0 cuando no hay humanos")
    void testGetStats_NoHumans() {
        when(repository.countByIsMutant(true)).thenReturn(10L);
        when(repository.countByIsMutant(false)).thenReturn(0L);

        StatsResponse response = service.getStats();

        assertEquals(0.0, response.getRatio());
    }

    @Test
    @DisplayName("Ratio debe ser 0 cuando no hay datos")
    void testGetStats_NoData() {
        when(repository.countByIsMutant(true)).thenReturn(0L);
        when(repository.countByIsMutant(false)).thenReturn(0L);

        StatsResponse response = service.getStats();

        assertEquals(0.0, response.getRatio());
    }

    @Test
    @DisplayName("Solo humanos")
    void testGetStats_OnlyHumans() {
        when(repository.countByIsMutant(true)).thenReturn(0L);
        when(repository.countByIsMutant(false)).thenReturn(5L);

        StatsResponse response = service.getStats();

        assertEquals(0.0, response.getRatio());
    }

    @Test
    @DisplayName("Solo mutantes")
    void testGetStats_OnlyMutants() {
        when(repository.countByIsMutant(true)).thenReturn(5L);
        when(repository.countByIsMutant(false)).thenReturn(1L);

        StatsResponse response = service.getStats();

        assertEquals(5.0, response.getRatio());
    }

    @Test
    @DisplayName("Verifica conteos exactos")
    void testStatsCounts() {
        when(repository.countByIsMutant(true)).thenReturn(3L);
        when(repository.countByIsMutant(false)).thenReturn(7L);

        StatsResponse response = service.getStats();

        assertEquals(3L, response.getCount_mutant_dna());
        assertEquals(7L, response.getCount_human_dna());
    }
}

