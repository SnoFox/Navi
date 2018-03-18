package net.snofox.navi.module;

import net.snofox.navi.Navi;
import net.snofox.navi.config.IConfig;
import net.snofox.navi.config.VoiceLogConfig;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.*;

public class VoiceLog implements IListener {
    final private VoiceLogConfig config;
    public VoiceLog(final IConfig config) {
        if(config instanceof VoiceLogConfig)
            this.config = (VoiceLogConfig)config;
        else
            throw new IllegalArgumentException();
    }

    private void logToTextChat(UserVoiceChannelEvent ev) {
        if(ev.getUser().getLongID() == Navi.getDiscordClient().getOurUser().getLongID()) return;
        final IGuild guild = ev.getGuild();
        final IChannel voice_chat = findVoiceChat(guild);
        if(voice_chat == null) return;
        final IUser user = ev.getUser();
        final IVoiceChannel voice_chan = ev.getVoiceChannel();
        String verb = "joined";
        if(ev instanceof UserVoiceChannelLeaveEvent)
            verb = "left";
        voice_chat.sendMessage(user.getDisplayName(guild) + ' ' + verb + ' ' + voice_chan.getName());
    }

    private IChannel findVoiceChat(final IGuild guild) {
        final Long channelId = config.getMapping(guild);
        if(channelId != null)
            return guild.getChannelByID(channelId);
        return null;
    }

    public void handle(final Event ev) {
        if(ev instanceof UserVoiceChannelJoinEvent ||
                ev instanceof UserVoiceChannelLeaveEvent ||
                ev instanceof UserVoiceChannelMoveEvent) {
            logToTextChat((UserVoiceChannelEvent)ev);
        } else if(ev instanceof MessageReceivedEvent) {
            handleCommand((MessageReceivedEvent)ev);
        }
    }

    private void handleCommand(final MessageReceivedEvent ev) {
        IGuild guild = ev.getGuild();
        if(guild == null) return;
        final IMessage eventMessage = ev.getMessage();
        final String msg = eventMessage.getContent();
        final String[] msgArr = msg.split(" ", 2);
        if(!msgArr[0].equalsIgnoreCase("!voicelog")) return;
        final IChannel source_chat = ev.getChannel();
        if(!ev.getAuthor().getPermissionsForGuild(guild).contains(Permissions.MANAGE_CHANNELS)) {
            source_chat.sendMessage("Listen! You can't do that, " + ev.getAuthor().mention()
                    + "! That might make the server owner angry!");
            return;
        }
        if(eventMessage.getChannelMentions().isEmpty()) {
            final Long currChan = config.getMapping(guild);
            if(currChan == null) {
                source_chat.sendMessage("Hey, listen! This feature isn't active!");
            } else {
                source_chat.sendMessage("Look! The current voicelog channel is "
                        + guild.getChannelByID(currChan));
            }
            return;
        }
        final IChannel text_chat = eventMessage.getChannelMentions().get(0);
        config.addMapping(guild, text_chat);
        Navi.saveConfig(config);
        source_chat.sendMessage("Watch out! People joining and leaving voice channels will be reported to " + text_chat.mention());
    }
}
