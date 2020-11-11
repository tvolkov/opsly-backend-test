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

    @Value("${social.network.response.timeout}")
    private long responseTimeout;

    @Autowired
    public SocialNetworkResponseProducer(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<JSONObject> getResponse(final String path){
        return this.webClient
                .get()
                .uri("/{path}", path)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(clientResponse -> handleResponse(clientResponse, path))
                .timeout(Duration.ofSeconds(responseTimeout), Mono.just(new JSONObject(Map.of(path, "unable to get response because server is unavailable"))))
                .subscribeOn(Schedulers.elastic());
    }

    private Mono<JSONObject> handleResponse(ClientResponse clientResponse, String path){
        if (clientResponse.statusCode().is5xxServerError()) {
            return Mono.just(new JSONObject(Map.of(path, "unable to get response due to server error")));
        } else if (clientResponse.statusCode().is4xxClientError()) {
            return Mono.just(new JSONObject(Map.of(path, "unable to get response as request is incorrect")));
        }

        return clientResponse.bodyToMono(String.class).map(response -> new JSONObject(Map.of(path, response)));
    }

}
