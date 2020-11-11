package com.opslycloud;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@AutoConfigureWebTestClient
@ContextConfiguration(initializers = {WireMockInitializer.class})
public class SocialNetworkAggregatorTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WireMockServer wireMockServer;

    @LocalServerPort
    private Integer port;

    @Value("#{'${social.network.paths}'.split(',')}")
    private List<String> socialNetworkPaths;

    @Value("${error.msg.server}")
    private String serverErrorMessage;

    @Value("${error.msg.client}")
    private String clientErrorMessage;

    @AfterEach
    public void afterEach(){
        this.wireMockServer.resetAll();
    }

    private Map<String, String> socialNetworkResponseStubs = Map.of("twitter", "[{\"username\":\"@GuyEndoreKaiser\",\"tweet\":\"If you live to be 100, you should make up some fake reason why, just to mess with people... like claim you ate a pinecone every single day.\"},{\"username\":\"@mikeleffingwell\",\"tweet\":\"STOP TELLING ME YOUR NEWBORN'S WEIGHT AND LENGTH I DON'T KNOW WHAT TO DO WITH THAT INFORMATION.\"}]",
            "facebook", "[{\"name\":\"Some Friend\",\"status\":\"Here's some photos of my holiday. Look how much more fun I'm having than you are!\"},{\"name\":\"Drama Pig\",\"status\":\"I am in a hospital. I will not tell you anything about why I am here.\"}]",
            "instagram", "[{\"name\":\"Some Friend\",\"status\":\"Here's some photos of my holiday. Look how much more fun I'm having than you are!\"},{\"name\":\"Drama Pig\",\"status\":\"I am in a hospital. I will not tell you anything about why I am here.\"}]");

    @Test
    public void shouldAggregateResposnesFromAllSocialNetworks() {
        stubAllSuccessfulResponses();
        webTestClient
                .get()
                .uri("http://localhost:" + port + "/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(buildSuccessfulBody());
    }

    @Test
    public void shouldAggregateResponsesWhenNotAllAreSuccessful() {
        stubOneUnsuccesfullResponse();
        webTestClient
                .get()
                .uri("http://localhost:" + port + "/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(buildOneSuccessfulBody());
    }

    @Test
    public void shouldReturn404ErrorIfUrlIsIncorrect(){
        stubAllSuccessfulResponses();
        webTestClient
                .get()
                .uri("http://localhost:" + port + "/incorrectpath")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void shouldReturnErrorMessageIfSocialNetworksTimeOut(){
        stubWithTimeouts();
        webTestClient
                .get()
                .uri("http://localhost:" + port + "/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(buildTimeoutBody());
    }

    private void stubWithTimeouts() {
        createSuccessStub("twitter");
        createTimeoutStub("facebook");
        createTimeoutStub("instagram");
    }

    private void createTimeoutStub(String path) {
        this.wireMockServer.stubFor(
                WireMock.get("/" + path)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(5000)
                )
        );
    }

    private void stubOneUnsuccesfullResponse() {
        createSuccessStub("twitter");
        create500ErrorStub("facebook");
        create404ErrorStub("instagram");
    }

    private void stubAllSuccessfulResponses() {
        this.socialNetworkPaths.forEach(this::createSuccessStub);
    }

    private void createSuccessStub(String path){
        this.wireMockServer.stubFor(
                WireMock.get("/" + path)
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(socialNetworkResponseStubs.get(path))
                )
        );
    }

    private void create404ErrorStub(String path){
        this.wireMockServer.stubFor(
                WireMock.get("/" + path)
                        .willReturn(aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("404 page not found")
                        )
        );
    }

    private void create500ErrorStub(String path){
        this.wireMockServer.stubFor(
                WireMock.get("/" + path)
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("I am trapped in a social media factory send help")
                        )
        );
    }

    private String buildSuccessfulBody(){
        return new JSONObject(socialNetworkResponseStubs).toString();
    }

    private String buildOneSuccessfulBody() {
        return new JSONObject(Map.of("twitter", "[{\"username\":\"@GuyEndoreKaiser\",\"tweet\":\"If you live to be 100, you should make up some fake reason why, just to mess with people... like claim you ate a pinecone every single day.\"},{\"username\":\"@mikeleffingwell\",\"tweet\":\"STOP TELLING ME YOUR NEWBORN'S WEIGHT AND LENGTH I DON'T KNOW WHAT TO DO WITH THAT INFORMATION.\"}]",
                "facebook", "{\"" + serverErrorMessage + "\":\"I am trapped in a social media factory send help\"}",
                "instagram", "{\"" + clientErrorMessage + "\":\"404 page not found\"}")).toString();
    }
    private String buildTimeoutBody(){
        return new JSONObject(Map.of("twitter", "[{\"username\":\"@GuyEndoreKaiser\",\"tweet\":\"If you live to be 100, you should make up some fake reason why, just to mess with people... like claim you ate a pinecone every single day.\"},{\"username\":\"@mikeleffingwell\",\"tweet\":\"STOP TELLING ME YOUR NEWBORN'S WEIGHT AND LENGTH I DON'T KNOW WHAT TO DO WITH THAT INFORMATION.\"}]",
                "facebook", "unable to get response because server is unavailable",
                "instagram", "unable to get response because server is unavailable")).toString();
    }
}
