package nl.gigafyde.apollo.commands.music;

import net.dv8tion.jda.core.entities.TextChannel;
import nl.gigafyde.apollo.core.BaseCommand;
import nl.gigafyde.apollo.core.CommandEvent;
import nl.gigafyde.apollo.core.MusicManager;
import nl.gigafyde.apollo.core.TrackScheduler;

public class Clear extends BaseCommand {
    public Clear() {
        this.name = "Clear";
        this.triggers = new String[]{"clear", "c"};
        this.description = "Clears the queue";
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
        if (scheduler.isEmpty()) {
            channel.sendMessage("**Queue is empty!").queue();
            return;
        }
        scheduler.getQueue().clear();
        channel.sendMessage("\uD83D\uDCA5 **Queue cleared**").queue();

    }
}