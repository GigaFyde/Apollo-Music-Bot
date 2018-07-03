package nl.gigafyde.apollo;

import com.kaaz.configuration.ConfigurationBuilder;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import nl.gigafyde.apollo.core.CommandClient;
import nl.gigafyde.apollo.core.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] ignore) throws Exception {
        LOGGER.trace("Hi");
        try {
            new ConfigurationBuilder(Config.class, new File("bot.cfg")).build(false);
        } catch (Exception e) {
            LOGGER.error("Failed to build config!", e);
            System.exit(1);
        }
        CommandClient client = new CommandClient();
        try {
            client.getRegistry().addCommandFromPackage("nl.gigafyde.apollo.commands", true);
        } catch (Exception e) {
            LOGGER.error("Failed to build commands!", e);
            return;
        }
        JDA jda = new JDABuilder(AccountType.BOT)
                .setToken(Config.token)
                .setAudioSendFactory(new NativeAudioSendFactory())
                .addEventListener(client)
                .buildBlocking();
        for(String id: Config.voice_channels.split(",")) {
            try {
                VoiceChannel channel = jda.getVoiceChannelById(Long.parseLong(id));
                if(channel == null) {
                    LOGGER.warn("Voice Channel doesn't exists: " + id);
                    continue;
                }
                if(!channel.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_SPEAK, Permission.VOICE_CONNECT)) {
                    LOGGER.warn("Can't connect/speak in Voice Channel: " + id);
                    continue;
                }
                if(channel.getGuild().getAudioManager().isConnected()) {
                    LOGGER.warn("Bot already connected: " + id);
                    continue;
                }
                client.getMusicManager().addScheduler(channel, true);
            } catch (NumberFormatException ignored) {
                LOGGER.warn("Voice Channel ID is invalid/unspecified: " + id);
            }
        }
    }
}