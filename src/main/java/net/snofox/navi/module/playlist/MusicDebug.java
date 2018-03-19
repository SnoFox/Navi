package net.snofox.navi.module.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.snofox.navi.module.command.ICommand;
import net.snofox.navi.sound.LavaAudioProviderImpl;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.LinkedList;
import java.util.List;

public class MusicDebug implements ICommand {
    @Override
    public void run(MessageReceivedEvent ev, String command, List<String> args) {
        final IGuild guild = ev.getGuild();
        final IUser user = ev.getAuthor();
        final IChannel chan = ev.getChannel();
        if(!user.getPermissionsForGuild(guild).contains(Permissions.MANAGE_SERVER)) {
            chan.sendMessage("Look! You need the *Manage Server* permission to do that!");
            return;
        }
        PlaySession session = MusicManager.getInstance().getSession(guild);
        StringBuilder sb = new StringBuilder();
        sb.append("Debug stats for " + guild.getName());
        sb.append("\n");
        for(int i = -16; i < guild.getName().length(); ++i) sb.append('-');
        sb.append("\n**PlayerManager info**");
        sb.append("\nFrame duration: " + MusicManager.getInstance().getPlayerManager().getFrameBufferDuration());
        sb.append("\n**Player stats**");
        final AudioPlayer player = session.getAudioDevice().getAudioPlayer();
        sb.append("\nPaused: " + player.isPaused());
        sb.append("\nVolume: " + player.getVolume());
        AudioTrack track = player.getPlayingTrack();
        if(track != null) {
            AudioTrackInfo trackInfo = track.getInfo();
            sb.append("\nTrack loaded: " + trackInfo.title);
            sb.append("\nAuthor: " + trackInfo.author);
            sb.append("\nIdentifier: " + trackInfo.identifier);
            sb.append("\nURI: <" + trackInfo.uri + ">");
        } else {
            sb.append("\nNo track loaded");
        }
        sb.append("\n**Provider stats**");
        final LavaAudioProviderImpl provider = (LavaAudioProviderImpl)session.getAudioDevice().getAudioProvider();
        sb.append("\nIs ready? " + provider.isReady());
        sb.append("\nCalls: " + provider.getCalls());
        sb.append("\nUnderflows: " + provider.getUnderflows());
        sb.append("\n**TrackScheduler stats**");
        final LinkedList<AudioTrack> queue = MusicManager.getInstance().getScheduler().getQueue(guild.getLongID());
        sb.append("\nQueue: " + queue.size() + " tracks");
        if(queue.size() > 0) sb.append("\nNext song: " + queue.get(1).getInfo().title);
        ev.getChannel().sendMessage(sb.toString());
    }
}
