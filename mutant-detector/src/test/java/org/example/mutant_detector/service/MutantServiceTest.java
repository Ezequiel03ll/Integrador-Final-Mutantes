package org.example.mutant_detector.service;

import org.example.mutant_detector.entity.DnaRecord;
import org.example.mutant_detector.repository.DnaRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)


class MutantServiceTest {
    @Mock
    private MutantDetector mutantDetector;

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private MutantService mutantService;

    private final String[] mutantDna = {
            "AAAA", "CCCC", "TTTT", "GGGG"
    };

    private final String[] humanDna = {
            "ATGC", "CAGT", "TTAT", "AGAC"
    };

    @Test
    @DisplayName("Debe analizar ADN mutante y guardarlo en DB")
    void testAnalyzeMutantDnaAndSave() {
        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty());
        when(mutantDetector.isMutant(mutantDna))
                .thenReturn(true);

        boolean result = mutantService.analyzeDna(mutantDna);

        assertTrue(result);
        verify(mutantDetector, times(1)).isMutant(mutantDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Debe analizar ADN humano y guardarlo en DB")
    void testAnalyzeHumanDnaAndSave() {
        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty());
        when(mutantDetector.isMutant(humanDna))
                .thenReturn(false);

        boolean result = mutantService.analyzeDna(humanDna);

        assertFalse(result);
        verify(mutantDetector, times(1)).isMutant(humanDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Debe retornar resultado cacheado si el ADN ya fue analizado")
    void testReturnCachedResultForAnalyzedDna() {

        DnaRecord cached = new DnaRecord();
        cached.setDnaHash("hash1");
        cached.setMutant(true);

        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.of(cached));

        boolean result = mutantService.analyzeDna(new String[]{
                "ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"
        });

        assertTrue(result);
        verify(mutantDetector, never()).isMutant(any());
        verify(dnaRecordRepository, never()).save(any());
    }


    @Test
    @DisplayName("Debe generar hash consistente para el mismo ADN")
    void testConsistentHashGeneration() {
        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty());
        when(mutantDetector.isMutant(any()))
                .thenReturn(true);

        mutantService.analyzeDna(mutantDna);
        mutantService.analyzeDna(mutantDna);

        verify(dnaRecordRepository, times(2)).findByDnaHash(anyString());
    }

    @Test
    @DisplayName("Debe guardar registro con hash correcto")
    void testSavesRecordWithCorrectHash() {
        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty());
        when(mutantDetector.isMutant(mutantDna))
                .thenReturn(true);

        mutantService.analyzeDna(mutantDna);

        verify(dnaRecordRepository).save(argThat(record ->
                record.getDnaHash() != null &&
                        record.getDnaHash().length() == 64 &&
                        record.isMutant()
        ));
    }
}
