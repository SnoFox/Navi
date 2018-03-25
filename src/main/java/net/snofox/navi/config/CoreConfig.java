package net.snofox.navi.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CoreConfig implements IConfig {
    private String apiToken;
    private String commandPrefix;
    private String logbackConfig;

    @JsonProperty("apitoken")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public String getApiToken() {
        return apiToken;
    }

    @JsonProperty("prefix")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public String getCommandPrefix() {
        return commandPrefix;
    }

    @JsonProperty("logbackConfig")
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    public String getLogbackConfig() {
        return logbackConfig;
    }
}
