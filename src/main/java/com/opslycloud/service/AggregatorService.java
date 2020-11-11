package com.opslycloud.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
        return Mono.zip(collectResponses(), this::combineResponses);
    }

    private String combineResponses(Object[] objects){
        JSONArray jsonArray = new JSONArray(objects);
        Map<String, Object> map = new HashMap<>();
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject)obj;
            map.putAll(jsonObject.toMap());
        }
        return new JSONObject(map).toString();
    }

    private List<Mono<JSONObject>> collectResponses() {
        return socialNetworkPaths.stream().map(socialNetworkResponseProducer::getResponse)
                .collect(Collectors.toList());
    }
}
