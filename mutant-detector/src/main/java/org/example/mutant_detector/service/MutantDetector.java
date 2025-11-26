package org.example.mutant_detector.service;

import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;
    private static final Set<Character> VALID_BASES = Set.of('A','T','C','G');

    public boolean isMutant(String[] dna) {
        if (!isValidDnaSimple(dna)) return false;

        int n = dna.length;
        char[][] m = new char[n][];
        for (int i = 0; i < n; i++) m[i] = dna[i].toCharArray();

        int sequenceCount = 0;

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {

                if (col <= n - SEQUENCE_LENGTH && checkHorizontal(m, row, col)) {
                    if (++sequenceCount > 1) return true;
                }

                if (row <= n - SEQUENCE_LENGTH && checkVertical(m, row, col)) {
                    if (++sequenceCount > 1) return true;
                }

                if (row <= n - SEQUENCE_LENGTH && col <= n - SEQUENCE_LENGTH && checkDiagDesc(m, row, col)) {
                    if (++sequenceCount > 1) return true;
                }

                if (row >= SEQUENCE_LENGTH - 1 && col <= n - SEQUENCE_LENGTH && checkDiagAsc(m, row, col)) {
                    if (++sequenceCount > 1) return true;
                }
            }
        }
        return false;
    }

    private boolean isValidDnaSimple(String[] dna) {
        if (dna == null || dna.length == 0) return false;
        int n = dna.length;
        for (String row : dna) {
            if (row == null || row.length() != n) return false;
            // further char check omitted (validator handles it)
        }
        return true;
    }

    private boolean checkHorizontal(char[][] m, int r, int c) {
        char b = m[r][c];
        return m[r][c+1] == b && m[r][c+2] == b && m[r][c+3] == b;
    }
    private boolean checkVertical(char[][] m, int r, int c) {
        char b = m[r][c];
        return m[r+1][c] == b && m[r+2][c] == b && m[r+3][c] == b;
    }
    private boolean checkDiagDesc(char[][] m, int r, int c) {
        char b = m[r][c];
        return m[r+1][c+1] == b && m[r+2][c+2] == b && m[r+3][c+3] == b;
    }
    private boolean checkDiagAsc(char[][] m, int r, int c) {
        char b = m[r][c];
        return m[r-1][c+1] == b && m[r-2][c+2] == b && m[r-3][c+3] == b;
    }
}