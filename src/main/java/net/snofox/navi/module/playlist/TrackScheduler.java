package net.snofox.navi.module.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.snofox.navi.Navi;
import sx.blah.discord.Discord4J;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

class TrackScheduler extends AudioEventAdapter {
    final private Map<Long, LinkedList<AudioTrack>> queues;

    TrackScheduler() {
        queues = new HashMap<>();
    }

    /*
    scheduler:
    play / dequeue
    pause
    stop
    skip
    back
    fast forward
    rewind
    queue
    delete
    */

    LinkedList<AudioTrack> getQueue(final long queueId) {
        if(!queues.containsKey(queueId))
            queues.put(queueId, new LinkedList<>());
        return queues.get(queueId);
    }

    void queue(final long queueId, final AudioTrack track) {
        System.out.println("Queuing up " + track.getInfo().title + " for " + queueId);
        getQueue(queueId).addLast(track);
    }

    void queueFirst(final long queueId, final AudioTrack track) {
        getQueue(queueId).addFirst(track);
    }

    AudioTrack dequeue(final long queueId) {
        final LinkedList<AudioTrack> queue = getQueue(queueId);
        try {
            return queue.pop();
        } catch(NoSuchElementException e) {

        }
        return null;
    }

    void shuffleQueue(final long queueId) {
        // TODO: write LinkedList shuffle code
    }


    public void clearQueue(final long queueId) {
        getQueue(queueId).clear();
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
        SessionMetadata meta = track.getUserData(SessionMetadata.class);
        if(meta == null) {
            Discord4J.LOGGER.error("Got a finished track with no metadata, bleh");
            return;
        }
        MusicManager.getInstance().play(Navi.getDiscordClient().getGuildByID(meta.sessionId));
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