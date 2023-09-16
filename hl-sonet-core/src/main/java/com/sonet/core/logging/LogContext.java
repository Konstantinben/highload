package com.sonet.core.logging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogContext {

    private String traceId;

    private String requestPath;

    private RequestMethod requestMethod;

    private String javaMethodSignature;

    private Pair<String, String> logPrefixes;

    private Pair<String, String> logDescriptions;

    private List<String> requestParams;

    private Map<String, List<String>> requestHeaders;

    private RequestDirection requestDirection;

}
