package net.snofox.navi.module.playlist.command;

import net.snofox.navi.module.command.CommandRequires;
import net.snofox.navi.module.command.ICommand;
import net.snofox.navi.module.playlist.Playlist;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

@CommandRequires
public class NowPlayingCommand implements ICommand {
    final private Playlist module;

    public NowPlayingCommand(final Playlist module) {
        this.module = module;
    }

    @Override
    public void run(MessageReceivedEvent ev, String command, List<String> args) {
        IGuild guild = ev.getGuild();
        final IChannel source_chat = ev.getChannel();
        final IUser source_user = ev.getAuthor();
        module.notifyNowPlaying(guild, source_chat, source_user);
    }
}
