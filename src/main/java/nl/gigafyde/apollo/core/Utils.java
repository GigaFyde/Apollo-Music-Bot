package nl.gigafyde.apollo.core;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

public class Utils {
    public static boolean isConnected(TextChannel channel, CommandClient client) {
        Guild guild = channel.getGuild();
        if (!guild.getAudioManager().isConnected()) {
            channel.sendMessage("**Not connected!**").queue();
            return false;
        }
        MusicManager musicManager = client.getMusicManager();
        TrackScheduler scheduler = musicManager.getScheduler(guild);
        if (scheduler == null) {
            channel.sendMessage("**Not connected!**").queue();
            return false;
        }
        return true;
    }

    public static String stripatEveryone(String message) {
        return message.replace("@everyone", "@\u0435veryone").replace("@here", "@h\u0435re");
    }
}