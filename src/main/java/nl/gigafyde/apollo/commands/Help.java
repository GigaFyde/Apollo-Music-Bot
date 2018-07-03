package nl.gigafyde.apollo.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import nl.gigafyde.apollo.core.BaseCommand;
import nl.gigafyde.apollo.core.CommandEvent;

import java.awt.Color;

public class Help extends BaseCommand {
    public Help() {
        this.name = "Help";
        this.triggers = new String[]{"help", "commands", "command", "cmds", "cmd"};
        this.description = "Shows the commands.";
    }

    public void execute(CommandEvent event) {
        Message trigger = event.getTrigger();
        //Used to make the method less receptive
        String authorTitle = event.getSelfTag(true) + "'s Help Menu";
        String footerText = "Command used by: " + event.getAuthorTag(true);

        //Build a fancy embed
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(authorTitle, null, event.getSelfUser().getAvatarUrl())
                .setColor(event.isFromGuild() ? event.getSelfMember().getColor() : new Color(0x75aaff))
                .setDescription("Did someone need some help? Well here are my commands for you to use!")
                .setFooter(footerText, event.getSelfUser().getAvatarUrl());

        //Add commands
        StringBuilder fieldText = new StringBuilder();
        for (BaseCommand command : event.getClient().getRegistry().getCommands()) {
            if (command.isHidden()) continue;
            fieldText.append(command.getName())
                    .append(" - ")
                    .append(command.getDescription())
                    .append("\n");
        }
        embed.addField("Commands:", fieldText.toString(), false);

        //Send Embed
        trigger.getChannel().sendMessage(embed.build()).queue();
    }
}
