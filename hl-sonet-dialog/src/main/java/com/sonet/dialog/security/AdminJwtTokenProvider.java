package com.sonet.dialog.security;

import com.sonet.dialog.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminJwtTokenProvider {

    @Value("${app.admin-user-email}")
    private String adminUserName;

    @Value("${app.admin-user-uuid}")
    private String adminUserUuid;

    private final JwtTokenCreator jwtTokenCreator;

    public Map<String, String> getAdminHeadersMap() {
        return Map.of(
                jwtTokenCreator.getAuthorizationHeader(),
                jwtTokenCreator.createToken(adminUserName, Role.ADMIN, UUID.fromString(adminUserUuid)));
    }
}
