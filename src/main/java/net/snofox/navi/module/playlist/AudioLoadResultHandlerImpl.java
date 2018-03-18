package net.snofox.navi.module.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.snofox.navi.Navi;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

public class AudioLoadResultHandlerImpl implements AudioLoadResultHandler {
    final private Long guildId;
    final private Long channelId;

    AudioLoadResultHandlerImpl(final IGuild guild, final IChannel channel) {
        guildId = guild.getLongID();
        channelId = channel.getLongID();
    }

    void turnOffTyping() {
        getChannel().setTypingStatus(false);
    }

    private IGuild getGuild() {
        return Navi.getDiscordClient().getGuildByID(guildId);
    }

    private IChannel getChannel() {
        return getGuild().getChannelByID(channelId);
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        SessionMetadata meta = new SessionMetadata();
        meta.notifyChanId = getChannel().getLongID();
        meta.sessionId = getGuild().getLongID();
        audioTrack.setUserData(meta);
        MusicManager.getInstance().queueSong(getGuild(), audioTrack);
        AudioTrackInfo info = audioTrack.getInfo();
        getChannel().sendMessage("Queued up " + info.title + " (" + info.length + ")");
        turnOffTyping();
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        SessionMetadata meta = new SessionMetadata();
        meta.notifyChanId = getChannel().getLongID();
        meta.sessionId = getGuild().getLongID();
        audioPlaylist.getTracks().forEach((track) -> {
            track.setUserData(meta);
            MusicManager.getInstance().queueSong(getGuild(), track);
        });
        getChannel().sendMessage("Queued up " + audioPlaylist.getTracks().size() + " tracks");
        turnOffTyping();
    }

    @Override
    public void noMatches() {
        getChannel().sendMessage("Sorry, I couldn't play anything from that");
        turnOffTyping();
    }

    @Override
    public void loadFailed(FriendlyException e) {
        if(e.severity.equals(FriendlyException.Severity.COMMON)) {
            getChannel().sendMessage("That doesn't look to be available: " + e.getMessage());
        } else {
            getChannel().sendMessage("There was an internal problem while loading that track.");
            e.printStackTrace();
        }
        turnOffTyping();
    }
}
