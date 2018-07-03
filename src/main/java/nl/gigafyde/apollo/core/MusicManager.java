package nl.gigafyde.apollo.core;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import nl.gigafyde.apollo.core.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public final class MusicManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MusicManager.class);
    private final CommandClient client;
    private final Random random = new Random();
    private final Map<Long, TrackScheduler> schedulers = new ConcurrentHashMap<>();
    private final List<AudioTrack> defaultTracks = new ArrayList<>();
    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public MusicManager(CommandClient client) {
        this.client = client;
        AudioSourceManagers.registerRemoteSources(playerManager);
        try (Scanner scanner = new Scanner(new File("songs.txt"))) {
            while (scanner.hasNextLine()) {
                String song = scanner.nextLine();
                try {
                    playerManager.loadItem(song, new AudioLoadResultHandler() {
                        @Override
                        public void trackLoaded(AudioTrack track) {
                            if (!defaultTracks.contains(track)) {
                                LOGGER.info("Found track: " + song);
                                defaultTracks.add(track);
                            }
                        }

                        @Override
                        public void playlistLoaded(AudioPlaylist playlist) {
                            int added = 0;
                            for (AudioTrack track : playlist.getTracks()) {
                                if (!defaultTracks.contains(track)) {
                                    defaultTracks.add(track);
                                    added++;
                                }
                            }
                            LOGGER.info(String.format("Loaded %d of %d from playlist: " + song, added, playlist.getTracks().size()));
                        }

                        @Override
                        public void noMatches() {
                            LOGGER.warn("No matches for: " + song);
                        }

                        @Override
                        public void loadFailed(FriendlyException exception) {
                            LOGGER.error("Failed to load: " + song, exception);
                        }
                    }).get();
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("Exception Occurred", e);
                }
            }
        } catch (FileNotFoundException ignored) {
            LOGGER.warn("File, songs.txt, was not found!");
        }
    }

    public TrackScheduler getScheduler(Guild guild) {
        return schedulers.get(guild.getIdLong());
    }

    public void moveVoiceChannel(VoiceChannel voiceChannel) {
        Guild guild = voiceChannel.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        if (!audioManager.isConnected()) {
            addScheduler(voiceChannel, true);
        } else {
            audioManager.openAudioConnection(voiceChannel);
        }
    }

    public TrackScheduler addScheduler(VoiceChannel voiceChannel, boolean start) {
        Guild guild = voiceChannel.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        AudioPlayer player = playerManager.createPlayer();
        player.setVolume(Config.default_volume);
        audioManager.openAudioConnection(voiceChannel);
        audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
        TrackScheduler scheduler = new TrackScheduler(client, player, playerManager, start);
        player.addListener(scheduler);
        schedulers.put(guild.getIdLong(), scheduler);
        return scheduler;
    }

    public List<AudioTrack> getDefaultTracks() {
        return Collections.unmodifiableList(defaultTracks);
    }

    public AudioTrack getSong() {
        return defaultTracks.get(random.nextInt(defaultTracks.size())).makeClone();
    }
}
