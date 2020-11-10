package com.opslycloud;

import org.springframework.stereotype.Component;

@Component
public class SocialNetworkNameExtractor {

    /**
     * The assumption I made here is that all the social network urls have the same format as in the README.md
     * so each social network name can be extracted as a substring which comes after the last slash.
     * But while this logic could be more sophisticated, I think that this should be enough for the sake of this task.
     * @param url
     * @return
     */
    public String extractSocialNetworkNameFromUrl(String url){
        return url.substring(url.lastIndexOf('/') + 1);
    }
}
