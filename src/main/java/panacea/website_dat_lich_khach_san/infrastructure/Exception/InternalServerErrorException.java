package panacea.website_dat_lich_khach_san.infrastructure.Exception;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }
} 