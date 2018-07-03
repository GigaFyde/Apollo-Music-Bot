package nl.gigafyde.apollo.commands;

import nl.gigafyde.apollo.core.BaseCommand;
import nl.gigafyde.apollo.core.CommandEvent;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

public class Uptime extends BaseCommand {
    public Uptime() {
        name = "Uptime";
        triggers = new String[]{"uptime", "up"};
        guildOnly = false;
    }

    public void execute(CommandEvent event) {
        long millisecond = ManagementFactory.getRuntimeMXBean().getUptime();
        long days = TimeUnit.MILLISECONDS.toDays(millisecond);
        long hours = TimeUnit.MILLISECONDS.toHours(millisecond) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisecond) % 60;

        String currentUptime = String.format("%02d minutes", minutes);
        if (hours > 0 || days > 0) {
            currentUptime = String.format("%02d hours, %s", hours, currentUptime);
        }
        if (days > 0) {
            currentUptime = String.format("%,d days, %s", days, currentUptime);
        }

        event.getChannel().sendMessage(currentUptime).queue();

    }
}