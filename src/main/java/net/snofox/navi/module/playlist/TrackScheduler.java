package net.snofox.navi.module.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.snofox.navi.Navi;
import net.snofox.navi.util.RandUtils;
import org.slf4j.Logger;
import sx.blah.discord.Discord4J;

import java.util.*;

class TrackScheduler extends AudioEventAdapter {
    final private Map<Long, LinkedList<AudioTrack>> queues;
    final private Logger logger;

    TrackScheduler() {
        queues = new HashMap<>();
        logger = Navi.getLogger(this);
    }

    /*
    scheduler:
    [x] play / dequeue
    [ ] pause
    [ ] stop
    [x] skip
    [ ] back
    [ ] seek back/forward
    [x] queue
    [ ] delete
    [ ] shuffle
    [ ] repeat? (A->B, playlist)
    */

    LinkedList<AudioTrack> getQueue(final long queueId) {
        if(!queues.containsKey(queueId))
            queues.put(queueId, new LinkedList<>());
        return queues.get(queueId);
    }

    void queue(final long queueId, final AudioTrack track, final boolean queueFirst) {
        logger.info("Queuing up {} for {}", track.getInfo().title, queueId);
        if(queueFirst) getQueue(queueId).addFirst(track);
        else getQueue(queueId).addLast(track);
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
        Collections.shuffle(getQueue(queueId), RandUtils.getRandom());
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