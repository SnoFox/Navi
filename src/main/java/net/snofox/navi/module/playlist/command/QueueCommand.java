package net.snofox.navi.module.playlist.command;

import net.snofox.navi.module.command.ICommand;
import net.snofox.navi.module.playlist.Playlist;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;

import java.util.List;

public class QueueCommand implements ICommand {
    final private Playlist module;

    public QueueCommand(final Playlist module) {
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
        module.queueSong(guild, sb.toString().trim(), ev.getChannel());
    }
}
