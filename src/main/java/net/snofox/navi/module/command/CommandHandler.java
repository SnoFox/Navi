package net.snofox.navi.module.command;

import net.snofox.navi.Navi;
import net.snofox.navi.util.MessageUtils;
import org.slf4j.Logger;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

import java.lang.reflect.Method;
import java.util.*;

public class CommandHandler implements IListener {
    private static CommandHandler instance;
    private Logger logger;
    final private Map<String, ICommand> commands;

    public CommandHandler() {
        commands = new HashMap<String, ICommand>();
        logger = Navi.getLogger(this.getClass());
        instance = this;
    }

    public static boolean registerCommand(final String name, final ICommand command) {
        if(instance == null) return false;
        instance.logger.debug("Registered command {} to {}", name, command.getClass().getName());
        return instance.commands.putIfAbsent(name, command) == null;
    }

    @Override
    public void handle(final Event event) {
        if(event instanceof MessageReceivedEvent) {
            final MessageReceivedEvent messageEvent = (MessageReceivedEvent) event;
            final String[] args = messageEvent.getMessage().getContent().split(" ");
            if(args.length == 0) return;
            String command = args[0];
            final String commandPrefix = Navi.getCoreConfig().getCommandPrefix();
            if(!command.startsWith(commandPrefix)) return;
            command = command.substring(commandPrefix.length(), command.length()).toLowerCase();
            if(commands.containsKey(command)) {
                ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
                argsList.remove(0);
                final ICommand commandToRun = commands.get(command);
                final CommandFailureReason runFailureReason = validateCanRun(commandToRun, messageEvent);
                if(runFailureReason.equals(CommandFailureReason.NOT_FAILED))
                    commandToRun.run(messageEvent, command, argsList);
                else
                    respondToFailure(commandToRun, runFailureReason, messageEvent);
            } else {
                respondToFailure(null, CommandFailureReason.NOT_FOUND, messageEvent);
            }
        }
    }

    private CommandFailureReason validateCanRun(final ICommand command, final MessageReceivedEvent event) {
        try {
            final Method runMethod = command.getClass().getMethod("run", event.getClass(), String.class, List.class);
            final CommandRequires requirements = command.getClass().getAnnotation(CommandRequires.class);
            if(requirements == null)
                return CommandFailureReason.NOT_FAILED;
            if(!event.getChannel().getModifiedPermissions(event.getAuthor()).contains(requirements.permission()))
                return CommandFailureReason.NO_PERMISSIONS;
            if(requirements.voice() && event.getAuthor().getVoiceStateForGuild(event.getGuild()).getChannel() == null)
                return CommandFailureReason.REQUIRE_VOICE;
        } catch (NoSuchMethodException e) {
            logger.warn("Can't find the run() method for {}", command.getClass().getCanonicalName());
            return CommandFailureReason.MISDEFINED;
        }
        return CommandFailureReason.NOT_FAILED;
    }

    private void respondToFailure(final ICommand command, final CommandFailureReason reason, final MessageReceivedEvent event) {
        final IChannel chan = event.getChannel();
        final StringBuilder sb = new StringBuilder();
        sb.append(MessageUtils.getTagline());
        sb.append(" ");
        sb.append(event.getAuthor().mention());
        sb.append(", ");
        switch(reason) {
            case NOT_FAILED:
                // Command was actually run, don't react
                break;
            case NO_PERMISSIONS:
                final CommandRequires requirements = command.getClass().getAnnotation(CommandRequires.class);
                sb.append("you need the *");
                sb.append(MessageUtils.getFriendlyPermission(requirements.permission()));
                sb.append("* permission to do that!");
                break;
            case REQUIRE_VOICE:
                sb.append("you need to be in a voice channel for that!");
                break;
            case MISSING_ARGUMENTS:
                sb.append("you have the wrong syntax!");
                break;
            case MISDEFINED:
                sb.append("I found an internal issue that prevents this from working until a code change. Sorry!");
                break;
            case NOT_FOUND:
                // should not react, return immediately
                return;
            default:
                logger.warn("Unhandled command failure: {}", reason.name());
                sb.append("Something that was never supposed to happen... Happened.");
                break;
        }
        chan.sendMessage(sb.toString());
    }
}
