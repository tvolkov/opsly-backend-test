package com.opslycloud.service;

import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;

@Component
public class ResponseBodyProvider {
    @Value("${error.msg.server}")
    private String serverErrorMessage;

    @Value("${error.msg.client}")
    private String clientErrorMessage;


    String getResponseBody(String responseFromServer, ClientResponse clientResponse){
        if (clientResponse.statusCode().is5xxServerError()) {
            return new JSONObject(Map.of(serverErrorMessage, responseFromServer)).toString();
        } else if (clientResponse.statusCode().is4xxClientError()) {
            return new JSONObject(Map.of(clientErrorMessage, responseFromServer)).toString();
        }
        return responseFromServer;
    }
}
