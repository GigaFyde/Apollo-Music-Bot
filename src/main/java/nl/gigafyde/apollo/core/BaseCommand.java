package nl.gigafyde.apollo.core;

import com.acoxgaming.jdacommando.core.CommandEvent;
import com.acoxgaming.jdacommando.core.ICommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import nl.gigafyde.apollo.commands.music.Queue;
import nl.gigafyde.apollo.core.config.Config;
import nl.gigafyde.apollo.commands.music.NowPlaying;

public abstract class BaseCommand implements ICommand<CommandClient, BaseCommand> {
    protected boolean hidden = false;
    protected boolean guildOnly = false;
    protected boolean ownerOnly = false;
    protected String name = "";
    protected String description = "No description";
    protected String[] triggers = new String[0];

    @Override
    public final String[] getTriggers() {
        return triggers;
    }

    public final String getDescription() {
        return description;
    }

    public final boolean isHidden() {
        return hidden;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final void run(CommandEvent<CommandClient, BaseCommand> commandEvent) {
        if (Config.owner_only
                && !(this instanceof NowPlaying)
                && !(this instanceof Queue)
                && !Config.owner_id.equals(commandEvent.getAuthor().getId())) {
            return;
        }
        MessageChannel channel = commandEvent.getChannel();
        if (guildOnly && !commandEvent.isFromGuild()) {
            channel.sendMessage("This command can only be run inside a server!").queue();
            return;
        }
        if (ownerOnly && !commandEvent.getAuthor().getId().equals(Config.owner_id)) {
            return;
        }
        if (commandEvent.isFromGuild()) {
            if (!commandEvent.getSelfMember().hasPermission(commandEvent.getTextChannel(), Permission.MESSAGE_EMBED_LINKS)) {
                channel.sendMessage("I require the " + Permission.MESSAGE_EMBED_LINKS.getName() + " permission!").queue();
                return;
            }
        }
        execute(new nl.gigafyde.apollo.core.CommandEvent(commandEvent));
    }

    protected abstract void execute(nl.gigafyde.apollo.core.CommandEvent commandEvent);
}
