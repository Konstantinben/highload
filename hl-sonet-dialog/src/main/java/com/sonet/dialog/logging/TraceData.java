package com.sonet.dialog.logging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TraceData {

    private final String traceId;
    private final String spanId;
    private final Boolean sampled;

    @JsonCreator
    public TraceData(@JsonProperty("traceId") String traceId, @JsonProperty("spanId") String spanId,
                     @JsonProperty("sampled") Boolean sampled) {
        this.traceId = traceId;
        this.spanId = spanId;
        this.sampled = sampled;
    }

}
