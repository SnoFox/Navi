package net.snofox.navi.module.playlist;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.snofox.navi.config.IConfig;
import net.snofox.navi.config.PlaylistConfig;
import net.snofox.navi.module.NaviModule;
import net.snofox.navi.module.command.CommandHandler;
import net.snofox.navi.module.playlist.command.*;
import net.snofox.navi.util.MessageUtils;
import net.snofox.navi.util.NumberUtils;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.EmbedBuilder;

import java.util.List;

@NaviModule
public class Playlist implements IListener {
    private final MusicManager musicManager;

    public Playlist(IConfig config) {
        if(!(config instanceof PlaylistConfig)) config = new PlaylistConfig();
        musicManager = new MusicManager();
        musicManager.getPlayerManager().setFrameBufferDuration(((PlaylistConfig)config).getFrameBufferLengthMillis());
        registerCommands();
    }

    private void registerCommands() {
        CommandHandler.registerCommand("queue", new QueueCommand(this));
        CommandHandler.registerCommand("play", new PlayCommand(this));
        CommandHandler.registerCommand("debugmusic", new MusicDebug());
        CommandHandler.registerCommand("volume", new VolumeCommand(this));
        CommandHandler.registerCommand("skip", new SkipCommand(this));
        CommandHandler.registerCommand("clearqueue", new ClearQueueCommand(this));
        CommandHandler.registerCommand("np", new NowPlayingCommand(this));
    }

    @Override
    public void handle(Event event) {

    }

    public void queueSong(final IGuild guild, final String target, final IChannel notify) {
        musicManager.loadSong(guild, target, notify);
    }

    public void play(final IVoiceChannel channel) {
        channel.join();
        musicManager.play(channel.getGuild());
    }

    public void setSessionChannel(final IGuild guild, final IChannel channel) {
        musicManager.setSessionChannel(guild, channel);
    }

    public IChannel getSessionChannel(final IGuild guild) {
        return musicManager.getSessionChannel(guild);
    }

    public void setVolume(IGuild guild, final Integer volume) {
        musicManager.setVolume(guild, volume);
    }

    public Integer getVolume(IGuild guild) {
        return musicManager.getVolume(guild);
    }

    public void clearQueue(IGuild guild) {
        musicManager.clearQueue(guild);
    }

    public void skipSong(IGuild guild) {
        musicManager.skipSong(guild);
    }

    public AudioTrack getNowPlaying(IGuild guild) {
        return musicManager.nowPlaying(guild);
    }

    public void notifyNowPlaying(final IGuild guild, IChannel source_chat, final IUser source_user) {
        if(source_chat == null) source_chat = getSessionChannel(guild);
        EmbedBuilder builder = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();
        if(source_user != null) builder.withFooterText(source_user.getDisplayName(guild));
        final AudioTrack track = getNowPlaying(guild);
        if(track == null) {
            builder.appendField("Stopped", "Nothing is playing right now", false);
            source_chat.sendMessage(builder.build());
            return;
        }
        final AudioTrackInfo info = track.getInfo();
        builder.appendField("Now Playing", MessageUtils.tldr(info.title, 40), true);
        sb.append(MessageUtils.progressBar(track.getPosition(), track.getDuration()));
        sb.append("\n(");
        sb.append(NumberUtils.millisToTimestamp(track.getPosition()));
        sb.append('/');
        sb.append(NumberUtils.millisToTimestamp(track.getDuration()));
        sb.append(')');
        builder.appendField("Progress", sb.toString(), true);
        sb = new StringBuilder();
        final List<AudioTrack> queue = getQueue(guild);
        if(queue.size() > 0) {
            int x = 0;
            for(final AudioTrack nextTrack : queue) {
                sb.append('#');
                sb.append(++x);
                sb.append(": ");
                sb.append(nextTrack.getInfo().title);
                sb.append(" (");
                sb.append(NumberUtils.millisToTimestamp(nextTrack.getDuration()));
                sb.append(")\n");
                if(x >= 5)
                    break;
            }
            builder.appendField("On Deck", sb.toString(), false);
        }
        source_chat.sendMessage(builder.build());
    }

    public List<AudioTrack> getQueue(final IGuild guild) {
        return musicManager.getQueue(guild);
    }
}
