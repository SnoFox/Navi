package net.snofox.navi.module.command;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public interface ICommand {

    void run(final MessageReceivedEvent ev, final String command, final List<String> args);
}
