package nl.gigafyde.apollo.commands.music;

import nl.gigafyde.apollo.core.BaseCommand;
import nl.gigafyde.apollo.core.CommandEvent;

public class Disconnect extends BaseCommand {
    public Disconnect() {
        this.name = "Disconnect";
        this.triggers = new String[]{"disconnect", "dc", "leave", "fuckoff",};
        this.description = "Leaves the voice channel you're in.";
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        if (!event.getGuild().getAudioManager().isConnected()) {
            event.getChannel().sendMessage("**Not connected**").queue();
            return;
        }
        event.getGuild().getAudioManager().closeAudioConnection();
        event.getChannel().sendMessage("**Disconnected!**").queue();
    }
}
