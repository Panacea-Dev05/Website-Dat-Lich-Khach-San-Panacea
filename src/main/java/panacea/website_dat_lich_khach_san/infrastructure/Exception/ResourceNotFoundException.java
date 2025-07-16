package panacea.website_dat_lich_khach_san.infrastructure.Exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}