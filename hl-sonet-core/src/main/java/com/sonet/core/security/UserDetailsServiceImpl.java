package com.sonet.core.security;

import com.sonet.core.model.entity.User;
import com.sonet.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsServiceImpol")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            throw new BadCredentialsException("Username cannot be empty");
        }
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found by name "+ username));
        return SecurityUser.fromUser(user);
    }
}
