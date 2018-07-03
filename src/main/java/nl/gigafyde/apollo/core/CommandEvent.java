package nl.gigafyde.apollo.core;

import net.dv8tion.jda.core.entities.Message;

public class CommandEvent extends com.acoxgaming.jdacommando.core.CommandEvent<CommandClient, BaseCommand> {
    public CommandEvent(CommandClient client, Message trigger, String argument) {
        super(client, trigger, argument);
    }

    public CommandEvent(com.acoxgaming.jdacommando.core.CommandEvent<CommandClient, BaseCommand> event) {
        this(event.getClient(), event.getTrigger(), event.getArgument());
    }
}
