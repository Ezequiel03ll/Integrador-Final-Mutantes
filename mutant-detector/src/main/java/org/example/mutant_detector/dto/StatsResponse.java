package org.example.mutant_detector.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@Schema(description = "Estadísticas de verificaciones de ADN")
public class StatsResponse {
    private long count_mutant_dna;
    private long count_human_dna;
    private double ratio;
}