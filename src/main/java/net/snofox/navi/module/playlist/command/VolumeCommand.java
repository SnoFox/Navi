package net.snofox.navi.module.playlist.command;

import net.snofox.navi.NumberUtils;
import net.snofox.navi.module.command.ICommand;
import net.snofox.navi.module.playlist.Playlist;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

public class VolumeCommand implements ICommand {
    final private Playlist module;

    public VolumeCommand(Playlist module) {
        this.module = module;
    }

    @Override
    public void run(MessageReceivedEvent ev, String command, List<String> args) {
        final IChannel chat = ev.getChannel();
        final IUser user = ev.getAuthor();
        if(args.size() > 0) {
            final IVoiceChannel voice_chan = user.getVoiceStateForGuild(ev.getGuild()).getChannel();
            if (voice_chan == null) {
                chat.sendMessage("Hello! " + user.mention() + ", you need to be in a voice channel for that!");
                return;
            }
            if(!voice_chan.getModifiedPermissions(user).contains(Permissions.MANAGE_PERMISSIONS)) {
                chat.sendMessage("Hey! " + user.mention() + ", you need the *Manage Permissions* permission to do that!");
                return;
            }
            Integer volume;
            try {
                volume = NumberUtils.clamp(Integer.parseInt(args.get(0)), 0, 150);
            } catch (NumberFormatException e) {
                chat.sendMessage("Listen! " + user.mention() + ", volume should be a number between 0 and 150!");
                return;
            }
            module.setVolume(ev.getGuild(), volume);
            chat.sendMessage("Hey! " + user.mention() + ", the volume was set to " + module.getVolume(ev.getGuild()));
        } else {
            chat.sendMessage("Hey! " + user.mention() + ", the volume is currently " + module.getVolume(ev.getGuild()));
        }
    }
}
