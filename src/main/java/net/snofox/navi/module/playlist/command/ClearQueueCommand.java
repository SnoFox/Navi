package net.snofox.navi.module.playlist.command;

import net.snofox.navi.module.command.CommandRequires;
import net.snofox.navi.module.command.ICommand;
import net.snofox.navi.module.playlist.Playlist;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

@CommandRequires(permission = Permissions.MANAGE_PERMISSIONS, voice = true)
public class ClearQueueCommand implements ICommand {
    final private Playlist module;

    public ClearQueueCommand(final Playlist module) {
        this.module = module;
    }

    @Override
    public void run(MessageReceivedEvent ev, String command, List<String> args) {
        final IChannel chat = ev.getChannel();
        final IUser user = ev.getAuthor();
        module.clearQueue(ev.getGuild());
        chat.sendMessage("Look out! " + user.mention() + ", the queue is cleared!");
    }
}
