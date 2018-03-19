package net.snofox.navi.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlaylistConfig implements IConfig {
    private int frameBufferLengthMillis;

    @JsonProperty("frameBufferLengthMillis")
    public int getFrameBufferLengthMillis() {
        return frameBufferLengthMillis;
    }
}
