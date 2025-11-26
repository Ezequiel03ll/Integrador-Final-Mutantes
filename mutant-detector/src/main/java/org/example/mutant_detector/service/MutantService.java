package org.example.mutant_detector.service;

import lombok.RequiredArgsConstructor;
import org.example.mutant_detector.entity.DnaRecord;
import org.example.mutant_detector.exception.DnaHashCalculationException;
import org.example.mutant_detector.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MutantService {

    private final DnaRecordRepository repository;
    private final MutantDetector detector;

    public boolean analyzeDna(String[] dna) {
        String dnaHash = calculateDnaHash(dna);

        Optional<DnaRecord> existing = repository.findByDnaHash(dnaHash);
        if (existing.isPresent()) {
            return existing.get().isMutant();
        }

        boolean isMutant = detector.isMutant(dna);

        DnaRecord record = new DnaRecord();
        record.setDnaHash(dnaHash);
        record.setMutant(isMutant);
        record.setCreatedAt(LocalDateTime.now());
        repository.save(record);

        return isMutant;
    }

    private String calculateDnaHash(String[] dna) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String dnaString = String.join("|", dna);
            byte[] hashBytes = md.digest(dnaString.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                String hexPart = Integer.toHexString(0xff & b);
                if (hexPart.length() == 1) hex.append('0');
                hex.append(hexPart);
            }
            return hex.toString();
        } catch (Exception e) {
            throw new DnaHashCalculationException("Error calculando hash del ADN", e);
        }
    }
}