package net.snofox.navi.sound;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import sx.blah.discord.handle.audio.AudioEncodingType;
import sx.blah.discord.handle.audio.IAudioProvider;

public class LavaAudioProviderImpl implements IAudioProvider {
    private final AudioPlayer player;
    private AudioFrame frame;
    private int underflows = 0;
    public LavaAudioProviderImpl(final AudioPlayer player) {
        this.player = player;
    }

    @Override
    public boolean isReady() {
        if(frame == null) frame = player.provide();
        return !(frame == null);
    }

    @Override
    public byte[] provide() {
        if(!isReady()) {
            ++underflows;
            return null;
        }
        byte[] frameData = frame.data;
        frame = null;
        return frameData;
    }

    @Override
    public int getChannels() {
        return 2;
    }

    @Override
    public AudioEncodingType getAudioEncodingType() {
        return AudioEncodingType.OPUS;
    }

    public int getUnderflows() {
        return underflows;
    }

    public void resetUnderflows() {
        underflows = 0;
    }
}
