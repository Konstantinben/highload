package com.sonet.dialog.logging;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TracerService {

    private final Tracer tracer;

    public String traceId() {
        return context().traceId();
    }

    public TraceContext context() {
        Span span = tracer.currentSpan();
        if (span != null) {
            return span.context();
        }

        return tracer.nextSpan().context();
    }

    public TraceData traceData() {
        TraceContext context = context();
        return new TraceData(context.traceId(), context.spanId(), context.sampled());
    }

    public Tracer.SpanInScope startInScope(TraceData data) {
        TraceContext context = tracer.traceContextBuilder()
                .traceId(data.getTraceId()).spanId(data.getSpanId()).sampled(data.getSampled())
                .build();

        return startInScope(context);
    }

    public Tracer.SpanInScope startInScope(TraceContext context) {
        Span span = tracer.spanBuilder().setParent(context).start();
        return tracer.withSpan(span);
    }
}
