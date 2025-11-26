package org.example.mutant_detector.service;

import lombok.RequiredArgsConstructor;
import org.example.mutant_detector.dto.StatsResponse;
import org.example.mutant_detector.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final DnaRecordRepository repo;

    public StatsResponse getStats() {
        long mutants = repo.countByIsMutant(true);
        long humans = repo.countByIsMutant(false);
        double ratio = humans == 0 ? 0.0 : ((double) mutants / (double) humans);
        return new StatsResponse(mutants, humans, ratio);
    }
}