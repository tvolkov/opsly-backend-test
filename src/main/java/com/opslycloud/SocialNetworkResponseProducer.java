package com.opslycloud;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class SocialNetworkResponseProducer {

    private final SocialNetworkNameExtractor socialNetworkNameExtractor;

    @Autowired
    public SocialNetworkResponseProducer(SocialNetworkNameExtractor socialNetworkNameExtractor) {
        this.socialNetworkNameExtractor = socialNetworkNameExtractor;
    }

    public Mono<JSONObject> getResponse(final String url){
        WebClient webClient = WebClient.create(url);
        return Mono.just(new JSONObject("test"));
    }
}
