package org.example.mutant_detector.dto;

import org.example.mutant_detector.validation.ValidDnaSequence;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para verificar si un ADN es mutante")
public class DnaRequest {

    @Schema(
            description = "Secuencia de ADN representada como matriz NxN",
            example = "[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]",
            required = true
    )
    @NotNull(message = "DNA no puede ser null")
    @NotEmpty(message = "DNA no puede estar vacío")
    @ValidDnaSequence
    private String[] dna;
}