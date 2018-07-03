package nl.gigafyde.apollo.commands.music;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import nl.gigafyde.apollo.core.BaseCommand;
import nl.gigafyde.apollo.core.CommandEvent;

public class Connect extends BaseCommand {
    public Connect() {
        this.name = "Connect";
        this.triggers = new String[]{"connect", "join", "j","summon"};
        this.description = "Joins the voice channel you're in.";
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        TextChannel channel = event.getTextChannel();
        VoiceChannel vc = event.getMember().getVoiceState().getChannel();
        if (vc == null) {
            channel.sendMessage("**Please join a voice channel first!**").queue();
            return;
        }
        if (vc == event.getSelfMember().getVoiceState().getChannel()) {
            channel.sendMessage("**Already connected!**").queue();
            return;
        }
        try {
            event.getClient().getMusicManager().moveVoiceChannel(vc);
            channel.sendMessage("**Connected!**").queue();
        } catch (InsufficientPermissionException ignored) {
            channel.sendMessage("**Cannot join the voice channel you're in!**").queue();
        }
    }
}
