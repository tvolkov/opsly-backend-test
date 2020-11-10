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

@Component
public class SocialNetworkResponseProducer {

    private final SocialNetworkNameExtractor socialNetworkNameExtractor;
    private final WebClient webClient;

    @Autowired
    public SocialNetworkResponseProducer(SocialNetworkNameExtractor socialNetworkNameExtractor,
            @Value("${social.network.base.url}") String baseUrl,
            WebClient.Builder webClientBuilder) {
        this.socialNetworkNameExtractor = socialNetworkNameExtractor;
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<JSONObject> getResponse(final String path){
        return this.webClient
                .get()
                .uri("/{path}", path)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(clientResponse -> handleResponse(clientResponse, path))
                .timeout(Duration.ofSeconds(3));//todo make configurable
    }

    private Mono<JSONObject> handleResponse(ClientResponse clientResponse, String path){
        if (clientResponse.statusCode().is5xxServerError()) {
            return Mono.just(new JSONObject(Map.of(path, "unable to get response from")));
        }

        return clientResponse.bodyToMono(String.class).map(response -> new JSONObject(Map.of(path, response)));
    }

}
