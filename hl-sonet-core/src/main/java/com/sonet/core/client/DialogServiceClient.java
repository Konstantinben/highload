package com.sonet.core.client;

import com.sonet.core.model.dto.DialogMessageDto;
import com.sonet.core.model.dto.MessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(value = "DialogServiceClient", url = "${app.dialog-service-uri}")
public interface DialogServiceClient {

    @PostMapping("/api/v1/dialog/{toUuid}/send")
    DialogMessageDto add(@PathVariable("toUuid") UUID toUserUuid, @RequestBody MessageDto messageDto, @RequestHeader Map<String, String> headerMap);

    @GetMapping("/api/v1/dialog/{toUuid}/get")
    List<DialogMessageDto> getByUuid(@PathVariable("toUuid") String toUuid, @RequestHeader Map<String, String> headerMap);
}
