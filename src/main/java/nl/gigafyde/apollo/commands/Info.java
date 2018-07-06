package nl.gigafyde.apollo.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import nl.gigafyde.apollo.core.BaseCommand;
import nl.gigafyde.apollo.core.CommandEvent;

public class Info extends BaseCommand {
    public Info(){
        this.name = "info";
        this.triggers = new String[]{"info"};
        this.guildOnly = false;
    }

    public void execute(CommandEvent event){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Info about the Apollo project");
        eb.setDescription("Apollo is a selfhost music bot made by **GigaFyde#0008**\nIt features many commands popular music bots also have.\n\nCheck the support server for more details!\n[Support Server](https://discord.gg/pNMp7fN)");
        eb.setFooter("Apollo version 1.5",null);

        event.getTrigger().getChannel().sendMessage(eb.build()).queue();
    }
}
