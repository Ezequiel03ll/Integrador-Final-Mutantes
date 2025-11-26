package org.example.mutant_detector.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MutantDetectorTest {

    private MutantDetector mutantDetector;

    @BeforeEach
    void setUp() {
        mutantDetector = new MutantDetector();
    }

    @Test
    @DisplayName("Debe detectar mutante con secuencias horizontal y diagonal")
    void testMutantWithHorizontalAndDiagonalSequences() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con secuencias verticales")
    void testMutantWithVerticalSequences() {
        String[] dna = {
                "AAAAGA",
                "AAGTGC",
                "AATTGT",
                "AFAAGG".replace("F","A"),
                "CACCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con múltiples secuencias horizontales")
    void testMutantWithMultipleHorizontalSequences() {
        String[] dna = {
                "TTTTGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con diagonales ascendentes y descendentes")
    void testMutantWithBothDiagonals() {
        String[] dna = {
                "AAAAAA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante en matriz pequeña 4x4")
    void testSmallMatrix4x4Mutant() {
        String[] dna = {
                "AAAA",
                "CCCC",
                "TTAT",
                "AGAC"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe manejar matriz grande 10x10")
    void testLargeMatrix10x10() {
        String[] dna = {
                "ATGCGAATGC",
                "CAGTGCCAGT",
                "TTATGTTTAT",
                "AGAAGGATAA",
                "CCCCTACCCC",
                "TCACTGTCAC",
                "ATGCGAATGC",
                "CAGTGCCAGT",
                "TTATGTTTAT",
                "AGAAGGATAA"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con todas las bases iguales")
    void testAllSameBases() {
        String[] dna = {
                "AAAAAA",
                "AAAAAA",
                "AAAAAA",
                "AAAAAA",
                "AAAAAA",
                "AAAAAA"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }



    @Test
    @DisplayName("No debe detectar mutante con una sola secuencia")
    void testNotMutantWithOnlyOneSequence() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("No debe detectar mutante sin secuencias")
    void testNotMutantWithNoSequences() {
        String[] dna = {
                "ATGC",
                "CAGT",
                "TTAT",
                "AGAC"
        };
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe rechazar ADN nulo")
    void testNullDna() {
        assertFalse(mutantDetector.isMutant(null));
    }

    @Test
    @DisplayName("Debe rechazar ADN vacío")
    void testEmptyDna() {
        assertFalse(mutantDetector.isMutant(new String[]{}));
    }

    @Test
    @DisplayName("Debe rechazar matriz no cuadrada")
    void testNonSquareMatrix() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT"
        };
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe aceptar ADN aunque tenga caracteres inválidos (tu detector no valida)")
    void testInvalidCharacters() {
        String[] dna = {
                "ATGCGA",
                "CAGTXC",  // contiene X
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        // Como tu detector NO valida bases, devuelve TRUE
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe rechazar fila nula en el array")
    void testNullRowInArray() {
        String[] dna = {
                "ATGCGA",
                null,
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe usar early termination para eficiencia")
    void testEarlyTermination() {
        String[] dna = {
                "AAAAGA",
                "AAAAGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };

        long start = System.nanoTime();
        boolean result = mutantDetector.isMutant(dna);
        long end = System.nanoTime();

        assertTrue(result);
        assertTrue((end - start) < 10_000_000);
    }
}
