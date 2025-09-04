package co.com.pragma.api.dto;

public record ClienteValidationRequest(
        String documentoIdentidad,
        String email
) {
}