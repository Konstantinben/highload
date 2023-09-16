package com.sonet.dialog.client;

import com.sonet.dialog.model.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
import java.util.UUID;

@Service
@FeignClient(value = "AuthServiceClient", url = "${app.auth-service-uri}")
public interface AuthServiceClient {

    @GetMapping("/auth/user/uuid/{uuid}")
    User getUserById(@PathVariable("uuid") UUID userId, @RequestHeader Map<String, String> headerMap);


    @GetMapping("/auth/user/email/{email}")
    @Operation(summary = "Посмотреть профиль")
    User getUserByEmail(@PathVariable("email") String email, @RequestHeader Map<String, String> headerMap);
}
