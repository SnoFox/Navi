package net.snofox.navi.module.playlist;

import net.snofox.navi.module.command.CommandHandler;
import net.snofox.navi.module.playlist.command.*;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.obj.*;

public class Playlist implements IListener {
    private final MusicManager musicManager;

    public Playlist() {
        musicManager = new MusicManager();
        registerCommands();
    }

    private void registerCommands() {
        CommandHandler.registerCommand("queue", new QueueCommand(this));
        CommandHandler.registerCommand("play", new PlayCommand(this));
        CommandHandler.registerCommand("debugmusic", new MusicDebug());
        CommandHandler.registerCommand("volume", new VolumeCommand(this));
        CommandHandler.registerCommand("skip", new SkipCommand(this));
        CommandHandler.registerCommand("clearqueue", new ClearQueueCommand(this));
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
}
