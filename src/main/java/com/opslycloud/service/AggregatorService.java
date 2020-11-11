package com.opslycloud.service;

import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AggregatorService {

    private final SocialNetworkResponseProducer socialNetworkResponseProducer;

    @Value("#{'${social.network.paths}'.split(',')}")
    private List<String> socialNetworkPaths;

    @Autowired
    public AggregatorService(SocialNetworkResponseProducer socialNetworkResponseProducer) {
        this.socialNetworkResponseProducer = socialNetworkResponseProducer;
    }

    public Mono<String> getResponse() {
        return Mono.zip(collectResponses(), objects -> new JSONArray(objects).toString());
    }

    private List<Mono<JSONObject>> collectResponses() {
        return socialNetworkPaths.stream().map(socialNetworkResponseProducer::getResponse)
                .collect(Collectors.toList());
    }
}
