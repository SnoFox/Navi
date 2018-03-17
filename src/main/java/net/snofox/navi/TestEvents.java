package net.snofox.navi;

import sx.blah.discord.Discord4J;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class TestEvents {
    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
        final IChannel chan = event.getChannel();
        final IGuild guild = event.getGuild();
        if(event.getMessage().getContent().equalsIgnoreCase("!hello"))
            chan.sendMessage("Hello, world!");
        if(event.getMessage().getContent().equalsIgnoreCase("!channel"))
            chan.sendMessage("Channel info: " + chan.getName());
        if(event.getMessage().getContent().equalsIgnoreCase("!join")) {
            final IVoiceChannel voice_chan = event.getAuthor().getVoiceStateForGuild(event.getGuild()).getChannel();
            if(voice_chan == null) {
                chan.sendMessage("You're not in a voice channel");
            } else {
                voice_chan.join();
                chan.sendMessage("Tried to join " + voice_chan.getName());
            }
        }
        if(event.getMessage().getContent().equalsIgnoreCase("!leave")) {
            chan.sendMessage("Heck off, " + event.getAuthor().getDisplayName(guild));
        }
        //System.out.println("Here's some cool info: " + (guild == null ? "private" : guild.getName()));
    }
    @EventSubscriber
    public void onGenericEvent(Event ev) {
        Discord4J.LOGGER.debug("Got a " + ev.getClass().getName());
    }
}
