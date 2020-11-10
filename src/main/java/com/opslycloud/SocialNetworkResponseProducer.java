package com.opslycloud;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class SocialNetworkResponseProducer {

    private final SocialNetworkNameExtractor socialNetworkNameExtractor;
    @Value("social.network.base.url")
    private String baseUrl;

    private final WebClient webClient;

    @Autowired
    public SocialNetworkResponseProducer(SocialNetworkNameExtractor socialNetworkNameExtractor, WebClient.Builder webClientBuilder) {
        this.socialNetworkNameExtractor = socialNetworkNameExtractor;
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<JSONObject> getResponse(final String url){
        return Mono.just(new JSONObject("{\"test\": 123}"));
    }
}
