package net.snofox.navi.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoreConfig implements IConfig {
    private String apiToken;
    private String commandPrefix;

    @JsonProperty("apitoken")
    public String getApiToken() {
        return apiToken;
    }

    @JsonProperty("prefix")
    public String getCommandPrefix() {
        return commandPrefix;
    }
}
