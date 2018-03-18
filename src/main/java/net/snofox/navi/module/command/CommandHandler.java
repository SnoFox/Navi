package net.snofox.navi.module.command;

import net.snofox.navi.Navi;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.*;

public class CommandHandler implements IListener {
    private static CommandHandler instance;
    final private Map<String, ICommand> commands;

    public CommandHandler() {
        commands = new HashMap<String, ICommand>();
        instance = this;
    }

    public static boolean registerCommand(final String name, final ICommand command) {
            return instance != null &&
                    instance.commands.putIfAbsent(name, command) == null;
    }

    @Override
    public void handle(final Event event) {
        if(event instanceof MessageReceivedEvent) {
            final String[] args = ((MessageReceivedEvent) event).getMessage().getContent().split(" ");
            if(args.length == 0) return;
            String command = args[0];
            final String commandPrefix = Navi.getCoreConfig().getCommandPrefix();
            if(!command.startsWith(commandPrefix)) return;
            command = command.substring(commandPrefix.length(), command.length()).toLowerCase();
            if(commands.containsKey(command)) {
                ArrayList<String> argsList = new ArrayList<>();
                argsList.addAll(Arrays.asList(args));
                argsList.remove(0);
                commands.get(command).run((MessageReceivedEvent)event, command, argsList);
            }
        }
    }
}
