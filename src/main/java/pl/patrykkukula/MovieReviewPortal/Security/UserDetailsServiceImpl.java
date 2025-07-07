package pl.patrykkukula.MovieReviewPortal.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserEntityRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailWithRoles(email)
                .map(userEntity -> new User(userEntity.getEmail(), userEntity.getPassword(), mapRoleToAuthorities(userEntity))
                ).orElseThrow(() -> new UsernameNotFoundException("Account with email " + email + " not found"));
    }
    public UserDetails getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        }
        return null;
    }
    public Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetails userDetails) {
            Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(userDetails.getUsername());
            if (optionalUserEntity.isPresent()) {
                return optionalUserEntity.get().getUserId();
            }
        }
        return null;
    }
    public UserEntity getUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new UsernameNotFoundException("Log in to add rate");
        }
        User user = (User) auth.getPrincipal();
        return userRepository.findByEmail(user.getUsername()).orElseThrow(() -> new ResourceNotFoundException("Account", "email", user.getUsername()));
    }
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetails principal) {
            return principal.getAuthorities().
                    stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> authority.contains("ROLE_ADMIN"));
        }
        return false;
}
    private List<SimpleGrantedAuthority> mapRoleToAuthorities(UserEntity userEntity) {
       return userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .toList();
    }
}
