package net.snofox.navi.module.playlist.command;

import net.snofox.navi.module.command.CommandRequires;
import net.snofox.navi.module.command.ICommand;
import net.snofox.navi.module.playlist.Playlist;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

@CommandRequires(permission = Permissions.MANAGE_PERMISSIONS, voice = true)
public class QueueNextCommand implements ICommand {
    final private Playlist module;

    public QueueNextCommand(final Playlist module) {
        this.module = module;
    }

    @Override
    public void run(MessageReceivedEvent ev, String command, List<String> args) {
        ev.getChannel().setTypingStatus(true);
        final StringBuilder sb = new StringBuilder();
        args.forEach((arg) -> {
            sb.append(arg);
            sb.append(' ');
        });
        final IGuild guild = ev.getGuild();
        module.setSessionChannel(guild, ev.getChannel());
        module.queueSong(guild, sb.toString().trim(), ev.getChannel(), true);
    }
}
