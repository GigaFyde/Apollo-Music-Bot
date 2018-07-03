package nl.gigafyde.apollo.commands.music;

import net.dv8tion.jda.core.entities.TextChannel;
import nl.gigafyde.apollo.core.BaseCommand;
import nl.gigafyde.apollo.core.CommandEvent;
import nl.gigafyde.apollo.core.MusicManager;
import nl.gigafyde.apollo.core.TrackScheduler;

import java.util.concurrent.TimeUnit;

public class Seek extends BaseCommand {
    public Seek() {
        this.name = "Seek";
        this.triggers = new String[]{"seek"};
        this.description = "Jumps forward to a specific part in the song";
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        TextChannel channel = event.getTextChannel();
        if (!event.getGuild().getAudioManager().isConnected()) {
            channel.sendMessage("**Not connected!**").queue();
            return;
        }
        MusicManager musicManager = event.getClient().getMusicManager();
        TrackScheduler scheduler = musicManager.getScheduler(event.getGuild());
        if (scheduler == null) {
            channel.sendMessage("**Not connected!**").queue();
            return;
        }
        if (scheduler.isAllEmpty()) {
            channel.sendMessage("**Queue is empty!**").queue();
            return;
        }
        if (event.getArgument().isEmpty()) {
            channel.sendMessage("**Specify the amount of seconds to skip**").queue();
        } else {
            int initialSeek;
            long maxLength = scheduler.getPlayer().getPlayingTrack().getDuration();
            try {
                initialSeek = Integer.parseInt(event.getArgument());
                long amounttoSeek = initialSeek * 1000;
                if (amounttoSeek > maxLength)
                    channel.sendMessage("**Specified amount of seconds exceeds song length!**").queue();
                else {
                    long finalSeek = scheduler.getPlayer().getPlayingTrack().getPosition() + amounttoSeek;
                    scheduler.getPlayer().getPlayingTrack().setPosition(finalSeek);
                    if (amounttoSeek < 61000) {
                        channel.sendMessage(String.format("**%d seconds skipped**", TimeUnit.MILLISECONDS.toSeconds(amounttoSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amounttoSeek)))).queue();
                    } else {
                        channel.sendMessage(String.format("**%d min, %d seconds skipped**", TimeUnit.MILLISECONDS.toMinutes(amounttoSeek), TimeUnit.MILLISECONDS.toSeconds(amounttoSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amounttoSeek)))).queue();
                    }

                }
            } catch (NumberFormatException ignored) {

            }
        }
    }
}
