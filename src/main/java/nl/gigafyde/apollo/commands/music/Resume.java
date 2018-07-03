package nl.gigafyde.apollo.commands.music;

import net.dv8tion.jda.core.entities.TextChannel;
import nl.gigafyde.apollo.core.BaseCommand;
import nl.gigafyde.apollo.core.CommandEvent;
import nl.gigafyde.apollo.core.MusicManager;
import nl.gigafyde.apollo.core.TrackScheduler;

public class Resume extends BaseCommand {
    public Resume() {
        this.name = "Resume";
        this.triggers = new String[]{"resume"};
        this.description = "Resumes the music";
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
        scheduler.getPlayer().setPaused(false);
        channel.sendMessage("**Successfully resumed the music**").queue();
    }
}
