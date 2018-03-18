package net.snofox.navi.module.playlist.command;

import net.snofox.navi.module.command.ICommand;
import net.snofox.navi.module.playlist.Playlist;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

public class ClearQueueCommand implements ICommand {
    final private Playlist module;

    public ClearQueueCommand(final Playlist module) {
        this.module = module;
    }

    @Override
    public void run(MessageReceivedEvent ev, String command, List<String> args) {
        final IChannel chat = ev.getChannel();
        final IUser user = ev.getAuthor();
        final IVoiceChannel voice_chan = user.getVoiceStateForGuild(ev.getGuild()).getChannel();
        if (voice_chan == null) {
            chat.sendMessage("Hello! " + user.mention() + ", you need to be in a voice channel for that!");
            return;
        }
        if(!voice_chan.getModifiedPermissions(user).contains(Permissions.MANAGE_PERMISSIONS)) {
            chat.sendMessage("Hey! " + user.mention() + ", you need the *Manage Permissions* permission to do that!");
            return;
        }
        module.clearQueue(ev.getGuild());
        chat.sendMessage("Look out! " + user.mention() + ", the queue is cleared!");
    }
}
