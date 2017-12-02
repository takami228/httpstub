package org.hidetake.stubyaml.app;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Arrays;

@Slf4j
@Component
public class RequestResponseLogger {
    private static final String RECEIVED = ">";
    private static final String SENT = "<";

    public void logRequest(ServerRequest request, MultiValueMap<String, ?> map) {
        logRequestHeaders(request);
        map.forEach((key, values) -> values.forEach(value -> {
            if (value instanceof Part) {
                val part = (Part) value;
                part.headers().forEach((headerKey, headerValues) -> headerValues.forEach(headerValue ->
                    log.info("{} [{}] {}: {}", RECEIVED, key, headerKey, headerValue)));
            } else {
                log.info("{} {}={}", RECEIVED, key, value);
            }
        }));
    }

    public void logRequest(ServerRequest request, @Nullable String body) {
        logRequestHeaders(request);
        if (body != null) {
            Arrays.stream(body.split("\r\n|\r|\n")).forEach(line ->
                log.info("{} {}", RECEIVED, line));
        }
    }

    private void logRequestHeaders(ServerRequest request) {
        log.info("{} {}", RECEIVED, request);
        request.headers().asHttpHeaders().forEach((key, values) ->
            values.forEach(value -> log.info("{} {}: {}", RECEIVED, key, value)));
        log.info(RECEIVED);
    }

    public void logResponse(ServerResponse response, @Nullable String body) {
        val status = response.statusCode();
        log.info("{} {} {}", SENT, status.value(), status.getReasonPhrase());
        response.headers().forEach((key, values) ->
            values.forEach(value -> log.info("{} {}: {}", SENT, key, value)));
        log.info(SENT);
        if (body != null) {
            Arrays.stream(body.split("\r\n|\r|\n")).forEach(line ->
                log.info("{} {}", SENT, line));
        }
    }
}
