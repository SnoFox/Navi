package net.snofox.navi.module;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.snofox.navi.Navi;
import net.snofox.navi.sound.LavaAudioProviderImpl;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;

public class Playlist implements IListener {
    private final AudioPlayerManager playerManager;
    private AudioPlayer player;
    private LavaAudioProviderImpl lavaAudioProvider;
    private TrackScheduler scheduler;

    public Playlist() {
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        this.player = playerManager.createPlayer();
        this.scheduler = new TrackScheduler(player);
        this.lavaAudioProvider = new LavaAudioProviderImpl(player);
        player.addListener(scheduler);
    }
    @Override
    public void handle(Event event) {
        if(event instanceof MessageReceivedEvent) {
            handleCommand((MessageReceivedEvent)event);
        }
    }

    private void handleCommand(final MessageReceivedEvent ev) {
        IGuild guild = ev.getGuild();
        if(guild == null) return;
        final IMessage eventMessage = ev.getMessage();
        final String msg = eventMessage.getContent();
        final String[] msgArr = msg.split(" ", 2);
        final IChannel source_chat = ev.getChannel();
        final IUser source_user = ev.getAuthor();
        final IVoiceChannel voice_chan = source_user.getVoiceStateForGuild(guild).getChannel();
        if(msgArr[0].equalsIgnoreCase("!music")) {
            if (!guild.getName().equalsIgnoreCase("Tableflippers Anonymous")) {
                ev.getChannel().sendMessage("Look out! This feature is only available in Tableflippers Anonymous");
                return;
            }
            if (voice_chan == null) {
                source_chat.sendMessage("Hello! " + source_user.mention() + ", you need to be in a voice channel for that!");
                return;
            }
            if(msgArr.length < 2) {
                source_chat.sendMessage("Hello! You need to give me a URL to try to play!");
                return;
            }
            final String sauce = msgArr[1];
            voice_chan.join();
            guild.getAudioManager().setAudioProvider(lavaAudioProvider);
            playerManager.loadItem(sauce, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack audioTrack) {
                    System.out.println("Track loaded; playing");
                    player.startTrack(audioTrack, false);
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {
                    System.out.println("Playlist loaded?");
                }

                @Override
                public void noMatches() {
                    voice_chan.leave();
                    source_chat.sendMessage("Watch out! I couldn't find anything there!");
                }

                @Override
                public void loadFailed(FriendlyException e) {
                    voice_chan.leave();
                    source_chat.sendMessage("Watch out! The song failed to load! " + e.getMessage());
                }
            });
            source_chat.sendMessage("Trying to music...");
        } else if(msgArr[0].equalsIgnoreCase("!underflows")) {
            source_chat.sendMessage("Watch out! " + lavaAudioProvider.getUnderflows() + " underflows have been detected");
        } else if(msgArr[0].equalsIgnoreCase("!resetunderflows")) {
            lavaAudioProvider.resetUnderflows();
            source_chat.sendMessage("Look! Underflow counter was reset!");
        } else if(msgArr[0].equalsIgnoreCase("!volume")) {
            if(msgArr.length > 1) {
                final Integer newVolume = Integer.parseInt(msgArr[1]);
                player.setVolume(newVolume);
            }
            source_chat.sendMessage("Listen! The volume is set to " + player.getVolume());
        }
    }

    private class TrackScheduler extends AudioEventAdapter {
        private AudioPlayer player;

        TrackScheduler(AudioPlayer player) {
            this.player = player;
        }

        @Override
        public void onTrackStart(AudioPlayer player, AudioTrack track) {
            System.out.println("Track started");
        }

        @Override
        public void onPlayerPause(AudioPlayer player) {
            System.out.println("Track paused");
        }

        @Override
        public void onPlayerResume(AudioPlayer player) {
            System.out.println("Track resumed");
        }

        @Override
        public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
            IGuild tblflp = null;
            for(IGuild thisGuild : Navi.getDiscordClient().getGuilds()) {
                if(thisGuild.getName().equalsIgnoreCase("Tableflippers Anonymous")) {
                    tblflp = thisGuild;
                    break;
                }
            }
            if(tblflp != null) {
                IVoiceChannel chan = tblflp.getConnectedVoiceChannel();
                if(chan != null)
                    chan.leave();
            }
        }

        @Override
        public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
            System.out.println("Track broke");
        }

        @Override
        public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
            System.out.println("Track hung");
        }
    }
}
