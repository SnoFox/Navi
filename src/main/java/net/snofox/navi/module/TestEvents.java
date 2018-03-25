package net.snofox.navi.module;

import net.snofox.navi.module.command.CommandHandler;
import net.snofox.navi.module.command.CommandRequires;
import net.snofox.navi.module.command.ICommand;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

public class TestEvents {

    public TestEvents() {
        CommandHandler.registerCommand("hello", (ev, name, args) -> {
           ev.getChannel().sendMessage("Hello, world!");
        });
        CommandHandler.registerCommand("echo", (ev, name, args) -> {
            ev.getChannel().sendMessage("You said: " + args.toString());
        });
        CommandHandler.registerCommand("join", (ev, name, args) -> {
            final IVoiceChannel voice_chan = ev.getAuthor().getVoiceStateForGuild(ev.getGuild()).getChannel();
            if(voice_chan == null) {
                ev.getChannel().sendMessage("You're not in a voice channel");
            } else {
                voice_chan.join();
                ev.getChannel().sendMessage("Tried to join " + voice_chan.mention());
            }
        });
        CommandHandler.registerCommand("leave", (ev, name, args) -> {
            final IVoiceChannel channel = ev.getGuild().getConnectedVoiceChannel();
            if(channel == null) {
                ev.getChannel().sendMessage("I'm not in a voice channel");
            } else {
                channel.leave();
                ev.getChannel().sendMessage("Fled " + channel.mention());
            }
        });
    }
}
