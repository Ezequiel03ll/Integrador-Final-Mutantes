package org.example.mutant_detector.validation;



import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ValidDnaSequenceValidator implements ConstraintValidator<ValidDnaSequence, String[]> {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[ATCG]+$");

    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        if (dna == null || dna.length == 0) {
            buildMessage(context, "El array dna no puede ser nulo ni vacío");
            return false;
        }

        int n = dna.length;
        if (n < 4) {
            buildMessage(context, "El dna debe tener al menos 4 filas");
            return false;
        }

        for (String row : dna) {
            if (row == null || row.isEmpty()) {
                buildMessage(context, "Ninguna fila puede ser null o vacía");
                return false;
            }
            if (row.length() != n) {
                buildMessage(context, "El dna debe ser una matriz NxN");
                return false;
            }
            if (!VALID_PATTERN.matcher(row).matches()) {
                buildMessage(context, "Caracter inválido detectado. Solo A,T,C,G permitidos");
                return false;
            }
        }
        return true;
    }

    private void buildMessage(ConstraintValidatorContext context, String msg) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
    }
}