package com.sonet.core.security;

import com.sonet.core.model.entity.User;
import com.sonet.core.repository.UserReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSessionUtil {

    private final UserReadRepository userRepository;

    public User getAuthorizedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((org.springframework.security.core.userdetails.User)auth.getPrincipal()).getUsername();
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user by email " + email));
    }
}
