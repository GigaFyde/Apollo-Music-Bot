package nl.gigafyde.apollo.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import nl.gigafyde.apollo.core.BaseCommand;
import nl.gigafyde.apollo.core.CommandEvent;
import nl.gigafyde.apollo.core.MusicManager;
import nl.gigafyde.apollo.core.TrackScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Queue extends BaseCommand {
    public Queue() {
        this.name = "Queue";
        this.triggers = new String[]{"queue", "q"};
        this.description = "Shows the current queue";
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        TextChannel channel = event.getTextChannel();
        MusicManager musicManager = event.getClient().getMusicManager();
        TrackScheduler scheduler = musicManager.getScheduler(event.getGuild());

        List<AudioTrack> queuedTracks = new ArrayList<>(scheduler.getQueue());
        if (scheduler.getPlayer().isPaused()) {
            channel.sendMessage("**The player is paused!**").queue();
            return;
        }
        if (scheduler.getPlayer().getPlayingTrack() == null) {
            channel.sendMessage("**Nothing is playing!**").queue();
            return;
        }
        long dms = scheduler.getPlayer().getPlayingTrack().getDuration();
        long dmin = TimeUnit.MILLISECONDS.toMinutes(dms);
        long dsec = TimeUnit.MILLISECONDS.toSeconds(dms) % 60;
        long pms = scheduler.getPlayer().getPlayingTrack().getPosition();
        long pmin = TimeUnit.MILLISECONDS.toMinutes(pms);
        long psec = TimeUnit.MILLISECONDS.toSeconds(pms) % 60;
        String currentProgress;
        if (scheduler.getPlayer().getPlayingTrack().getInfo().isStream) {
            currentProgress = String.format("%d:%02d/LIVE", pmin, psec);
        } else {
            currentProgress = String.format("%d:%02d/%d:%02d", pmin, psec, dmin, dsec);
        }
        String SongSource = "[Link]";
        if (scheduler.getPlayer().getPlayingTrack().getInfo().uri.contains("youtube.com")) {
            SongSource = "[Youtube]";
        }
        if (scheduler.getPlayer().getPlayingTrack().getInfo().uri.contains("soundcloud.com")) {
            SongSource = "[SoundCloud]";
        }
        if (scheduler.getPlayer().getPlayingTrack().getInfo().uri.contains("twitch.tv")) {
            SongSource = "[Twitch]";
        }
        if (queuedTracks.isEmpty()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Currently playing " + event.getGuild().getName());
            eb.addField("Song Title", scheduler.getPlayer().getPlayingTrack().getInfo().title, false);
            eb.addField("Song Link", SongSource + "(" + scheduler.getPlayer().getPlayingTrack().getInfo().uri + ")", true);
            eb.addField("Current Progress", currentProgress, true);
            eb.setThumbnail(String.format("https://img.youtube.com/vi/%s/hqdefault.jpg", scheduler.getPlayer().getPlayingTrack().getInfo().identifier));
            channel.sendMessage(eb.build()).queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Currently playing:");
        eb.addField(scheduler.getPlayer().getPlayingTrack().getInfo().title, scheduler.getPlayer().getPlayingTrack().getInfo().uri, false);
        eb.addBlankField(false);

        int page = 1;
        try {
            page = Integer.parseInt(event.getArgument());
        } catch (NumberFormatException ignored) {
        }
        if (page < 1) page = 1;
        int maxPages = (queuedTracks.size() + 10 - 1) / 10;
        if (page > maxPages) page = maxPages;
        int lowerLimit = (page - 1) * 10;
        int higherLimit = lowerLimit + 10;
        if (higherLimit > queuedTracks.size()) higherLimit = queuedTracks.size();
        List<String> toShow = new ArrayList<>();
        for (int i = lowerLimit; i < higherLimit; i++) {
            eb.addField(String.format("`[%d]` %s", i + 1, queuedTracks.get(i).getInfo().title), queuedTracks.get(i).getInfo().uri, false);
            toShow.add(String.format("`[%d]` %s", i + 1, queuedTracks.get(i).getInfo().title));
        }
        eb.setFooter("Page " + page + " of " + maxPages, null);
        channel.sendMessage(eb.build()).queue();
    }
}