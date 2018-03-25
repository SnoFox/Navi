package net.snofox.navi.module;

import net.snofox.navi.Navi;
import net.snofox.navi.module.command.CommandHandler;
import sx.blah.discord.handle.obj.IVoiceChannel;

@NaviModule
public class TestEvents {
    public TestEvents() {
        boolean success = CommandHandler.registerCommand("hello", (ev, name, args) -> {
           ev.getChannel().sendMessage("Hello, world!");
        });
        success = success & CommandHandler.registerCommand("echo", (ev, name, args) -> {
            ev.getChannel().sendMessage("You said: " + args.toString());
        });
        success = success & CommandHandler.registerCommand("join", (ev, name, args) -> {
            final IVoiceChannel voice_chan = ev.getAuthor().getVoiceStateForGuild(ev.getGuild()).getChannel();
            if(voice_chan == null) {
                ev.getChannel().sendMessage("You're not in a voice channel");
            } else {
                voice_chan.join();
                ev.getChannel().sendMessage("Tried to join " + voice_chan.mention());
            }
        });
        success = success & CommandHandler.registerCommand("leave", (ev, name, args) -> {
            final IVoiceChannel channel = ev.getGuild().getConnectedVoiceChannel();
            if(channel == null) {
                ev.getChannel().sendMessage("I'm not in a voice channel");
            } else {
                channel.leave();
                ev.getChannel().sendMessage("Fled " + channel.mention());
            }
        });
        if(!success) Navi.getLogger(this).error("One or more TestEvents commands success to register");
    }
}
