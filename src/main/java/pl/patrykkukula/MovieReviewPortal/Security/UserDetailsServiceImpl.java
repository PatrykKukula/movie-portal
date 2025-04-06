package pl.patrykkukula.MovieReviewPortal.Security;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;
import java.util.List;

@RequiredArgsConstructor
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserEntityRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailWithRoles(email)
                .map(userEntity -> new User(userEntity.getUsername(), userEntity.getPassword(), mapRoleToAuthorities(userEntity))
                ).orElseThrow(() -> new UsernameNotFoundException("Account with email " + email + " not found"));
    }
    private List<SimpleGrantedAuthority> mapRoleToAuthorities(UserEntity userEntity) {
       return userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .toList();
    }
}
