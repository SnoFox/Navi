package net.snofox.navi.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoreConfig implements IConfig {
    private String apiToken;

    @JsonProperty("apitoken")
    public String getApiToken() {
        return apiToken;
    }
}
