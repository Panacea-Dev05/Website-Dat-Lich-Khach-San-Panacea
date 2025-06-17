package panacea.website_dat_lich_khach_san.infrastructure.Exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
} 