package nl.gigafyde.apollo.commands;

import net.dv8tion.jda.core.entities.Message;
import nl.gigafyde.apollo.core.BaseCommand;
import nl.gigafyde.apollo.core.CommandEvent;

public class Ping extends BaseCommand {
    public Ping() {
        this.name = "Ping";
        this.triggers = new String[]{"ping", "pong", "pung"};
        this.description = "Checks ping";
    }

    public void execute(CommandEvent event) {
        Message trigger = event.getTrigger();
            long currentTime = System.currentTimeMillis();
            trigger.getChannel().sendMessage("Pinging...").queue((msg) -> {
                msg.editMessage(String.format("\uD83D\uDC93 Heartbeat: %d ms  \uD83C\uDFD3 Ping: %d ms", trigger.getJDA().getPing(), System.currentTimeMillis() - currentTime)).queue();
            });
        }
    }
