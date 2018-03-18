package net.snofox.navi.module.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.snofox.navi.sound.LavaAudioProviderImpl;

public class PlaySession {
    final private Long guildId;
    final private AudioDevice audioDevice;
    private Long channelId;

    PlaySession(final MusicManager manager, final Long guildId) {
        this.guildId = guildId;
        final AudioPlayer player = manager.getPlayerManager().createPlayer();
        this.audioDevice = new AudioDevice(player, new LavaAudioProviderImpl(player));
        player.addListener(manager.getScheduler());
    }

    public Long getGuild() {
        return guildId;
    }

    public AudioDevice getAudioDevice() {
        return audioDevice;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
