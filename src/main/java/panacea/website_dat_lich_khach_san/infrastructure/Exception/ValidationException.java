package panacea.website_dat_lich_khach_san.infrastructure.Exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
} 