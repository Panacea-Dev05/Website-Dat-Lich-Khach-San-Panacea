package panacea.website_dat_lich_khach_san.infrastructure.Exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
} 