package net.snofox.navi.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.util.HashMap;

public class VoiceLogConfig implements IConfig {
    final private HashMap<Long, Long> guildToChans = new HashMap<>();
    @JsonProperty("guildToChans")
    public HashMap<Long, Long> getMappings() {
        return guildToChans;
    }

    public void addMapping(IGuild guild, IChannel voice_chan) {
        guildToChans.put(guild.getLongID(), voice_chan.getLongID());
    }

    public Long getMapping(IGuild guild) {
        return guildToChans.get(guild.getLongID());
    }
}
