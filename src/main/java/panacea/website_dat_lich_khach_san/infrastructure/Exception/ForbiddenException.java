package panacea.website_dat_lich_khach_san.infrastructure.Exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
} 