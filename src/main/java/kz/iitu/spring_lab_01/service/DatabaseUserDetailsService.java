package kz.iitu.spring_lab_01.service;

import kz.iitu.spring_lab_01.AppUser;
import kz.iitu.spring_lab_01.catalog.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final AppUserRepository users;
    private final LoginAttemptService loginAttemptService;

    public DatabaseUserDetailsService(AppUserRepository users, LoginAttemptService loginAttemptService) {
        this.users = users;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser u = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .authorities(u.getRoles().stream()
                        .map(r -> "ROLE_" + r)
                        .toArray(String[]::new))
                .disabled(!u.isEnabled())
                .accountLocked(loginAttemptService.isBlocked(username))
                .build();
    }
}
