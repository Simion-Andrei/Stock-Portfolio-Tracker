package com.andrei.stockportfoliobackend.security;

import com.andrei.stockportfoliobackend.entity.User;
import com.andrei.stockportfoliobackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

/**
 * Service implementation needed by Spring Security to load user-specific data.
 * It bridges the gap between our database {@link User} entity and Spring's internal {@link UserDetails} interface.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their username from the database.
     *
     * @param username The username identifying the user whose data is required.
     * @return A fully populated UserDetails object (used for authentication).
     * @throws UsernameNotFoundException if the user cannot be found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // We return a Spring Security User object with empty authorities (roles) for now.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.emptyList()
        );
    }
}