package net.snofox.navi.module.playlist.command;

import net.snofox.navi.module.command.CommandRequires;
import net.snofox.navi.module.command.ICommand;
import net.snofox.navi.module.playlist.Playlist;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.List;

@CommandRequires(voice = true)
public class PlayCommand implements ICommand {
    final private Playlist module;

    public PlayCommand(final Playlist module) {
        this.module = module;
    }

    @Override
    public void run(MessageReceivedEvent ev, String command, List<String> args) {
        IGuild guild = ev.getGuild();
        if(guild == null) return;
        final IChannel source_chat = ev.getChannel();
        final IUser source_user = ev.getAuthor();
        final IVoiceChannel voice_chan = source_user.getVoiceStateForGuild(guild).getChannel();
        source_chat.sendMessage("Trying to music...");
        module.play(voice_chan);
    }
}
