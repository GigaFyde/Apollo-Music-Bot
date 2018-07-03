package nl.gigafyde.apollo.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import nl.gigafyde.apollo.core.BaseCommand;
import nl.gigafyde.apollo.core.CommandEvent;
import nl.gigafyde.apollo.core.MusicManager;
import nl.gigafyde.apollo.core.TrackScheduler;

import java.util.concurrent.TimeUnit;

public class NowPlaying extends BaseCommand {
    public NowPlaying() {
        this.name = "NowPlaying";
        this.triggers = new String[]{"nowplaying", "nowplay", "np"};
        this.description = "Shows the currently playing song/stream";
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        TextChannel channel = event.getTextChannel();
        if (!event.getGuild().getAudioManager().isConnected()) {
            channel.sendMessage("Not connected!").queue();
            return;
        }
        MusicManager musicManager = event.getClient().getMusicManager();
        TrackScheduler scheduler = musicManager.getScheduler(event.getGuild());
        if (scheduler == null) {
            channel.sendMessage("Not connected!").queue();
            return;
        }
        AudioTrack nowPlaying = scheduler.getPlayer().getPlayingTrack();
        if (nowPlaying == null) {
            channel.sendMessage("Not playing!").queue();
            return;
        }

        long dms = nowPlaying.getDuration();
        long dmin = TimeUnit.MILLISECONDS.toMinutes(dms);
        long dsec = TimeUnit.MILLISECONDS.toSeconds(dms) % 60;
        long pms = nowPlaying.getPosition();
        long pmin = TimeUnit.MILLISECONDS.toMinutes(pms);
        long psec = TimeUnit.MILLISECONDS.toSeconds(pms) % 60;

        EmbedBuilder eb = new EmbedBuilder();
        String currentProgress;
        if (nowPlaying.getInfo().isStream) {
            currentProgress = String.format("%d:%02d/LIVE", pmin, psec);
        } else {
            currentProgress = String.format("%d:%02d/%d:%02d", pmin, psec, dmin, dsec);
        }
        String SongSource = "[Link]";
        if (nowPlaying.getInfo().uri.contains("youtube.com")) {
            SongSource = "[Youtube]";
        }
        if (nowPlaying.getInfo().uri.contains("soundcloud.com")) {
            SongSource = "[SoundCloud]";
        }
        if (nowPlaying.getInfo().uri.contains("twitch.tv")) {
            SongSource = "[Twitch]";
        }


        eb.setAuthor("Currently playing in " + event.getGuild().getName());
        eb.setColor(event.getSelfMember().getColor());
        eb.setThumbnail(String.format("https://img.youtube.com/vi/%s/hqdefault.jpg", nowPlaying.getInfo().identifier));
        eb.addField("Song Title", nowPlaying.getInfo().title, false);
        eb.addField("Song Link", SongSource + "(" + nowPlaying.getInfo().uri + ")", true);
        eb.addField("Current Progress", currentProgress, true);
        channel.sendMessage(eb.build()).queue();
    }
}