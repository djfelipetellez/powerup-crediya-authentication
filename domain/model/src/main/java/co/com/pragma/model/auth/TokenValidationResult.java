package co.com.pragma.model.auth;

public record TokenValidationResult(
        boolean valid,
        Integer userId,
        String email,
        String role,
        String documentoIdentidad,
        Long exp,
        String error
) {
    public static TokenValidationResult valid(Integer userId, String email, String role, String documentoIdentidad, Long exp) {
        return new TokenValidationResult(true, userId, email, role, documentoIdentidad, exp, null);
    }
    
    public static TokenValidationResult invalid(String error) {
        return new TokenValidationResult(false, null, null, null, null, null, error);
    }
}