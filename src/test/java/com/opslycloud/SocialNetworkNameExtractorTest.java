package com.opslycloud;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class SocialNetworkNameExtractorTest {
    private final SocialNetworkNameExtractor socialNetworkNameExtractor= new SocialNetworkNameExtractor();


    @Test
    public void shouldExtractSocialNetworkNameFromGivenUrl() {
        //given
        String url = "https://takehome.io/twitter";

        //when
        String result = socialNetworkNameExtractor.extractSocialNetworkNameFromUrl(url);

        //then
        Assert.assertEquals("twitter", result);
    }

    //todo add more tests to cover cases when there is no path segments in the given url
}