package nl.gigafyde.apollo.core;

import com.acoxgaming.jdacommando.core.BaseClientImpl;
import nl.gigafyde.apollo.core.config.Config;

public class CommandClient extends BaseClientImpl<CommandClient, BaseCommand> {
    private final MusicManager musicManager = new MusicManager(this);

    public CommandClient() {
        super(BaseCommand.class, Config.default_prefix);
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }
}
