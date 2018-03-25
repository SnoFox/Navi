package net.snofox.navi.module;

import net.snofox.navi.Navi;
import net.snofox.navi.config.IConfig;
import net.snofox.navi.config.VoiceLogConfig;
import net.snofox.navi.module.command.CommandHandler;
import net.snofox.navi.module.command.CommandRequires;
import net.snofox.navi.module.command.ICommand;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.*;

import java.util.List;

@NaviModule
public class VoiceLog implements IListener {
    final private VoiceLogConfig config;
    public VoiceLog(final IConfig config) {
        if(config instanceof VoiceLogConfig)
            this.config = (VoiceLogConfig)config;
        else
            throw new IllegalArgumentException();
        CommandHandler.registerCommand("voicelog", new VoiceLogCommand());
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
        }
    }

    @CommandRequires(permission = Permissions.MANAGE_CHANNELS)
    private class VoiceLogCommand implements ICommand {

        @Override
        public void run(MessageReceivedEvent ev, String command, List<String> args) {
            IGuild guild = ev.getGuild();
            if(guild == null) return;
            final IMessage eventMessage = ev.getMessage();
            final IChannel source_chat = ev.getChannel();
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
}
