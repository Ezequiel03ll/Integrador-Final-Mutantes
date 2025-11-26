package org.example.mutant_detector.repository;

import org.example.mutant_detector.entity.DnaRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DnaRecordRepository extends JpaRepository<DnaRecord, Long> {
    Optional<DnaRecord> findByDnaHash(String dnaHash);
    long countByIsMutant(boolean isMutant);
}