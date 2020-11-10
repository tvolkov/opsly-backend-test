package com.opslycloud;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AggregatorService {

    private final SocialNetworkResponseProducer socialNetworkResponseProducer;
    private final List<String> socialMediaUrls = Arrays.asList("https://takehome.io/twitter", "https://takehome.io/facebook", "https://takehome.io/instagram");//todo make this externally configurable

    @Autowired
    public AggregatorService(SocialNetworkResponseProducer socialNetworkResponseProducer) {
        this.socialNetworkResponseProducer = socialNetworkResponseProducer;
    }

    public Mono<String> getResponse(){
        final List<Mono<JSONObject>> collect = socialMediaUrls.stream().map(socialNetworkResponseProducer::getResponse).collect(Collectors.toList());
        return Mono.zip(collect, this::combinator);
    }

    private String combinator(Object[] objects) {
        final List<JSONObject> collect = Arrays.stream(objects).map(object -> (JSONObject) object).collect(Collectors.toList());
        return new JSONArray(collect).toString();
    }
}
