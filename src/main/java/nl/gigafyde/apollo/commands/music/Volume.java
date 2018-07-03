package nl.gigafyde.apollo.commands.music;

import net.dv8tion.jda.core.entities.TextChannel;
import nl.gigafyde.apollo.core.BaseCommand;
import nl.gigafyde.apollo.core.CommandEvent;
import nl.gigafyde.apollo.core.MusicManager;

public class Volume extends BaseCommand {
    public Volume() {
        this.name = "Volume";
        this.triggers = new String[]{"volume", "vol", "v"};
        this.description = "Changes the current volume";
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        TextChannel channel = event.getTextChannel();
        MusicManager musicManager = event.getClient().getMusicManager();
        if (!event.getGuild().getAudioManager().isConnected()) {
            channel.sendMessage("**Not connected!**").queue();
            return;
        }
        if (event.getArgument().isEmpty()) {
            int vol = musicManager.getScheduler(event.getGuild()).getPlayer().getVolume();
            channel.sendMessage("\uD83D\uDD0A **Current volume is: " + vol + "%**").queue();
        }
        else {
            try {
                int volume = Integer.parseInt(event.getArgument());
                musicManager.getScheduler(event.getGuild()).getPlayer().setVolume(volume);
                channel.sendMessage("\uD83D\uDD0A **Volume set to: " + musicManager.getScheduler(event.getGuild()).getPlayer().getVolume() + "%**").queue();
            } catch (NumberFormatException ignored) {
                channel.sendMessage("\u274C **Invalid number**").queue();
            }
        }
    }
}
