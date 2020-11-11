package com.opslycloud;

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
    public SocialNetworkResponseProducer(@Value("${social.network.base.url}") String baseUrl,
            WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<JSONObject> getResponse(final String path){
        return this.webClient
                .get()
                .uri("/{path}", path)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(clientResponse -> handleResponse(clientResponse, path))
                .timeout(Duration.ofSeconds(responseTimeout))
                .subscribeOn(Schedulers.elastic());
    }

    private Mono<JSONObject> handleResponse(ClientResponse clientResponse, String path){
        if (clientResponse.statusCode().is5xxServerError()) {
            return Mono.just(new JSONObject(Map.of(path, "unable to get response from")));
        }

        return clientResponse.bodyToMono(String.class).map(response -> new JSONObject(Map.of(path, response)));
    }

}
