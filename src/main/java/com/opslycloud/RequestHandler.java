package com.opslycloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class RequestHandler {

    private final AggregatorService aggregatorService;

    @Autowired
    public RequestHandler(AggregatorService aggregatorService) {
        this.aggregatorService = aggregatorService;
    }

    public Mono<ServerResponse> handleRequest(ServerRequest serverRequest){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromProducer(aggregatorService.getResponse(), String.class));
    }
}
