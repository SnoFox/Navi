package net.snofox.navi.module.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import sx.blah.discord.handle.audio.IAudioProvider;

/***
 * Groups the various aspects of playing audio in one class
 * Handles: Loading, decoding, seeking (AudioPlayer)
 * Providing the audio to Discord (IAudioProvider)
 */
class AudioDevice {
    final private AudioPlayer audioPlayer;
    final private IAudioProvider audioProvider;
    AudioDevice(final AudioPlayer audioPlayer, final IAudioProvider audioProvider) {
        this.audioPlayer = audioPlayer;
        this.audioProvider = audioProvider;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public IAudioProvider getAudioProvider() {
        return audioProvider;
    }
}
