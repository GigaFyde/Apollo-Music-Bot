package nl.gigafyde.apollo.core.config;

import com.kaaz.configuration.ConfigurationOption;

@SuppressWarnings("WeakerAccess")
public class Config {
    @ConfigurationOption
    public static String token = "discord-bot-token";

    @ConfigurationOption
    public static String owner_id = "owner-id";

    @ConfigurationOption
    public static String default_prefix = "-";

    @ConfigurationOption
    public static String voice_channels = "";

    @ConfigurationOption
    public static boolean owner_only = false;

    @ConfigurationOption
    public static int default_volume = 100;
}