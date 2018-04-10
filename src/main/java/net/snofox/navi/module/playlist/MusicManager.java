package net.snofox.navi.module.playlist;

import com.google.common.collect.ImmutableList;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableList.of;

/***
 * PlayerManager responsible for global quality, configuration, and creating AudioTracks for players
 * Player = 1 unique audio stream
 * AudioLoadResultHandler for handling what happens when you load something
 * have an AudioEventListener (TrackScheduler) to listen for events from players
 * Have an IAudioProvider (LavaAudioProvider) to extract sound data from the Player for Discord
 */
public class MusicManager {
    static private MusicManager instance;
    final private AudioPlayerManager playerManager;
    final private Map<Long, PlaySession> sessions;
    final private TrackScheduler scheduler;

    /* pkg-private */ MusicManager() {
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        playerManager.enableGcMonitoring();
        this.sessions = new HashMap<>();
        this.scheduler = new TrackScheduler();
        instance = this;
    }

    static MusicManager getInstance() {
        return instance;
    }

    AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    private PlaySession newSession(final IGuild guild) {
        final long guildId = guild.getLongID();
        final PlaySession session = new PlaySession(this, guildId);
        sessions.put(guildId, session);
        return session;
    }

    PlaySession getSession(final IGuild guild) {
        final long guildId = guild.getLongID();
        if(sessions.containsKey(guildId)) return sessions.get(guildId);
        else return newSession(guild);
    }

    void setSessionChannel(final IGuild guild, final IChannel channel) {
        getSession(guild).setChannelId(channel.getLongID());
    }

    public IChannel getSessionChannel(IGuild guild) {
        return guild.getChannelByID(getSession(guild).getChannelId());
    }

    TrackScheduler getScheduler() {
        return scheduler;
    }

    private AudioPlayer getAudioPlayer(final IGuild guild) {
        return getSession(guild).getAudioDevice().getAudioPlayer();
    }

    void loadSong(final IGuild guild, final String target, final IChannel notify, final boolean queueFirst) {
        playerManager.loadItem(target, new AudioLoadResultHandlerImpl(guild, notify, queueFirst));
    }

    void queueSong(final IGuild guild, final AudioTrack track, final boolean first) {
        scheduler.queue(guild.getLongID(), track, first);
    }

    boolean play(final IGuild guild) {
        guild.getAudioManager().setAudioProvider(getSession(guild).getAudioDevice().getAudioProvider());
        AudioTrack track = scheduler.dequeue(guild.getLongID());
        if(track != null) {
            getAudioPlayer(guild).startTrack(track, false);
            return true;
        }
        return false;
    }

    public void setVolume(IGuild guild, Integer volume) {
        getSession(guild).getAudioDevice().getAudioPlayer().setVolume(volume);
    }

    public Integer getVolume(IGuild guild) {
        return getSession(guild).getAudioDevice().getAudioPlayer().getVolume();
    }

    public List<AudioTrack> getQueue(final IGuild guild) {
        return ImmutableList.copyOf(scheduler.getQueue(guild.getLongID()));
    }

    public void clearQueue(IGuild guild) {
        scheduler.clearQueue(guild.getLongID());
    }

    public void skipSong(IGuild guild) {
        getSession(guild).getAudioDevice().getAudioPlayer().stopTrack();
    }

    public AudioTrack nowPlaying(final IGuild guild) {
        return getSession(guild).getAudioDevice().getAudioPlayer().getPlayingTrack();
    }

    public void shuffleQueue(final IGuild guild) {
        scheduler.shuffleQueue(guild.getLongID());
    }

    /*
    create player/provider
    pruning
     */

}
