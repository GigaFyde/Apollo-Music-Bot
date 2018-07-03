package nl.gigafyde.apollo.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class TrackScheduler extends AudioEventAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackScheduler.class);
    private final CommandClient client;
    private final AudioPlayer player;
    private final AudioPlayerManager manager;
    private final Queue<AudioTrack> queue = new LinkedBlockingDeque<>();

    public AudioPlayerManager getManager() {
        return manager;
    }

    public TrackScheduler(CommandClient client, AudioPlayer player, AudioPlayerManager manager, boolean start) {
        this.client = client;
        this.player = player;
        this.manager = manager;
        if (start) nextSong();
    }

    public boolean isAllEmpty() {
        return queue.isEmpty() && client.getMusicManager().getDefaultTracks().isEmpty();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void nextSong() {
        if (!queue.isEmpty()) {
            player.playTrack(queue.poll());
        } else {
            if (!client.getMusicManager().getDefaultTracks().isEmpty())
                player.playTrack(client.getMusicManager().getSong());
        }
    }

    public boolean addSong(AudioTrack track) {
        if (client.getMusicManager().getDefaultTracks().contains(track))
            return false;
        if (queue.contains(track))
            return false;
        queue.add(track);
        if (player.getPlayingTrack() == null)
            nextSong();
        return true;
    }

    public int addSongs(AudioTrack... tracks) {
        return addSongs(Arrays.asList(tracks));
    }

    public int addSongs(List<AudioTrack> tracks) {
        int added = 0;
        for (AudioTrack track : tracks) {
            if (addSong(track)) added++;
        }
        return added;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public void skip() {
        skip(1);
    }

    public int skip(int amount) {
        if (amount < 1) return 0;
        amount--;
        int skipped = 1;
        if (queue.size() > amount) {
            for (int i = 0; i < amount; i++) {
                queue.poll();
                skipped++;
            }
        } else {
            skipped = queue.size();
            queue.clear();
        }
        nextSong();
        return skipped;
    }

    public void clear() {
        queue.clear();
    }

    public Queue<AudioTrack> getQueue() {
        return queue;
    }


    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextSong();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        nextSong();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        nextSong();
    }
}
