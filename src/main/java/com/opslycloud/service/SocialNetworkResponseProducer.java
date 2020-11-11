package com.opslycloud.service;

import java.time.Duration;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class SocialNetworkResponseProducer {

    private final WebClient webClient;
    private final ResponseBodyProvider responseBodyProvider;

    @Value("${social.network.response.timeout}")
    private long responseTimeout;

    @Value("${social.network.response.timeout.msg}")
    private String timeoutMsg;

    @Autowired
    public SocialNetworkResponseProducer(WebClient webClient, ResponseBodyProvider responseBodyProvider) {
        this.webClient = webClient;
        this.responseBodyProvider = responseBodyProvider;
    }

    public Mono<JSONObject> getResponse(final String path){
        return this.webClient
                .get()
                .uri("/{path}", path)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(clientResponse -> handleResponse(clientResponse, path))
                .timeout(Duration.ofSeconds(responseTimeout), Mono.just(new JSONObject(Map.of(path, timeoutMsg))))
                .subscribeOn(Schedulers.elastic());
    }

    private Mono<JSONObject> handleResponse(ClientResponse clientResponse, String path){
        return clientResponse.bodyToMono(String.class).map(response -> new JSONObject(Map.of(path, responseBodyProvider.getResponseBody(response, clientResponse))));
    }
}
