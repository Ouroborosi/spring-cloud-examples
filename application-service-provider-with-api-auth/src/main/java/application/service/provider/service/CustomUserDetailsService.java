package application.service.provider.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Mock two accounts:
     * 1. Username: user, Password: password1, Role: user-role
     * 2. Username: admin, Password: password2, Role: admin-role
     *
     * Because here provides the hard-coded plain text password.
     * The password needs to be encoded by the password encoder, or it can't be match with the encoded result.
     *
     * @param username use user name to load user details
     * @return UserDetails
     * @throws UsernameNotFoundException if the user could not be found, or the user has no GrantedAuthority.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("user".equals(username)) {
            return new SecurityUser("user", passwordEncoder.encode("password1"), "user-role");
        } else if ("admin".equals(username)) {
            return new SecurityUser("admin", passwordEncoder.encode("password2"), "admin-role");
        } else {
            return null;
        }
    }

    @Data
    @NoArgsConstructor
    class SecurityUser implements UserDetails {
        private static final long serialVersionUID = 1L;

        private Long id;
        private String username;
        private String password;
        private String role;

        /*
         * Lombok's @AllArgsConstructor will not call super(). That's why the constructor has to be created manually.
         */
        public SecurityUser(String username, String password, String role) {
            super();
            this.username = username;
            this.password = password;
            this.role = role;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(this.role);
            authorities.add(authority);
            return authorities;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getPassword() {
            return this.password;
        }

        @Override
        public String getUsername() {
            return this.username;
        }
    }
}
