package com.sonet.dialog.configuration;

import com.sonet.dialog.model.Role;
import com.sonet.dialog.security.JwtTokenCreator;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    @Value("${app.admin-user-email}")
    private String adminUserName;

    @Value("${app.admin-user-uuid}")
    private String adminUserUuid;

    private final JwtTokenCreator jwtTokenCreator;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(jwtTokenCreator.getAuthorizationHeader(), jwtTokenCreator.createToken(adminUserName, Role.ADMIN, UUID.fromString(adminUserUuid)));
            requestTemplate.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        };
    }

}