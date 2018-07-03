package nl.gigafyde.apollo.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import nl.gigafyde.apollo.core.*;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Play extends BaseCommand {
    public Play() {
        this.name = "Play";
        this.triggers = new String[]{"play", "p"};
        this.description = "Plays the song/playlist you provide.";
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        TextChannel channel = event.getTextChannel();
        MusicManager musicManager = event.getClient().getMusicManager();
        TrackScheduler scheduler = musicManager.getScheduler(event.getGuild());

        if (scheduler == null) {
            VoiceChannel vc = event.getMember().getVoiceState().getChannel();
            if (vc == null) {
                channel.sendMessage("**Please join a voice channel first!**").queue();
                return;
            }
            try {
                scheduler = event.getClient().getMusicManager().addScheduler(vc, false);
            } catch (InsufficientPermissionException ignored) {
                channel.sendMessage("**Cannot join VC**").queue();
                return;
            }
        }
        loadHandler(scheduler, event, false);
    }

    private void loadHandler(TrackScheduler scheduler, CommandEvent event, boolean search) {
        TextChannel channel = event.getTextChannel();
        scheduler.getManager().loadItem(search ? "ytsearch:" + event.getArgument() : event.getArgument(), new AudioLoadResultHandler() {
            String TrackInfo;
            String SongDuration;
            @Override
            public void trackLoaded(AudioTrack track) {
                if (scheduler.addSong(track)) {
                    if (track.getInfo().title.isEmpty()) {
                        TrackInfo = "Unknown Title";
                    }
                    else {
                        TrackInfo = track.getInfo().title.replace("*", "\\*");
                    }
                    if (track.getInfo().isStream) {
                        SongDuration = "(livestream)";
                    }
                    else {
                        long millis = track.getDuration();
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
                        SongDuration = String.format("(%d:%02d)", minutes, seconds);
                    }
                    channel.sendMessage(Utils.stripatEveryone("✅ **"+ TrackInfo +" added!** "+ SongDuration)).queue();
                } else {
                    channel.sendMessage("**Track already exists in queue!**").queue();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (search) {
                    if (playlist.getTracks().isEmpty()) {
                        noMatches();
                        return;
                    }
                    trackLoaded(playlist.getTracks().get(0));
                } else {
                    int added = scheduler.addSongs(playlist.getTracks());
                    channel.sendMessage(String.format("✅ **Added %s of %s from the playlist!**", added, playlist.getTracks().size())).queue();
                }
            }

            @Override
            public void noMatches() {
                if (search) {
                    channel.sendMessage("**Couldn't find anything with the term: **" + event.getArgument()).queue();
                    return;
                }
                loadHandler(scheduler, event, true);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("**Could not load track!**").queue();
                LoggerFactory.getLogger(Play.class).warn("**Couldn't load track:** " + exception);
            }
        });
    }
}
