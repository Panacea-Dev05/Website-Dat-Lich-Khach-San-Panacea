package panacea.website_dat_lich_khach_san.Auth;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    // Lấy role thực tế từ principal (authorities)
    public String getRoleFromPrincipal(OAuth2User principal) {
        if (principal == null) return "anonymous";
        return principal.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("anonymous");
    }
}
