package com.opslycloud;

import java.util.Arrays;
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
    private List<String> socialMediaPaths;

    @Autowired
    public AggregatorService(SocialNetworkResponseProducer socialNetworkResponseProducer) {
        this.socialNetworkResponseProducer = socialNetworkResponseProducer;
    }

    public Mono<String> getResponse() {
        final List<Mono<JSONObject>> collect = socialMediaPaths.stream().map(socialNetworkResponseProducer::getResponse)
                .collect(Collectors.toList());
        return Mono.zip(collect, this::combinator);
    }

    private String combinator(Object[] objects) {
        final List<JSONObject> collect = Arrays.stream(objects).map(object -> (JSONObject) object).collect(Collectors.toList());
        return new JSONArray(collect).toString();
    }
}
