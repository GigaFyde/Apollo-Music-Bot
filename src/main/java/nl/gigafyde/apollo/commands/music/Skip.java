package nl.gigafyde.apollo.commands.music;

import net.dv8tion.jda.core.entities.TextChannel;
import nl.gigafyde.apollo.core.BaseCommand;
import nl.gigafyde.apollo.core.CommandEvent;
import nl.gigafyde.apollo.core.MusicManager;
import nl.gigafyde.apollo.core.TrackScheduler;

public class Skip extends BaseCommand {
    public Skip() {
        this.name = "Skip";
        this.triggers = new String[]{"skip", "s"};
        this.description = "Skips the current song.";
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
        if (scheduler.getPlayer().getPlayingTrack() == null) {
            channel.sendMessage("**Nothing is playing!**").queue();
            return;
        }
        if (scheduler.isAllEmpty() & event.getArgument().isEmpty()) {
            scheduler.getPlayer().destroy();
            channel.sendMessage("\u23ED **Skipped!**").queue();
            return;
        }
        if (event.getArgument().isEmpty()) {
            scheduler.skip();
            channel.sendMessage("\u23ED **Skipped!**").queue();
            return;
        } else {
            int amountToSkip;
            try {
                amountToSkip = Integer.parseInt(event.getArgument());
                if (amountToSkip < 1) amountToSkip = 1;
            } catch (NumberFormatException ignored) {
                amountToSkip = 1;
            }
            channel.sendMessage(String.format("**Skipped** `%s` %s!", scheduler.skip(amountToSkip), amountToSkip == 1 ? "**song**" : "**songs**")).queue();
        }
    }
}