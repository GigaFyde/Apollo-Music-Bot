package nl.gigafyde.apollo.commands;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import net.dv8tion.jda.core.entities.Message;
import nl.gigafyde.apollo.core.config.Config;
import nl.gigafyde.apollo.core.BaseCommand;
import nl.gigafyde.apollo.core.CommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class Eval extends BaseCommand {
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("Eval Thread Pool");
    private static final Executor POOL = Executors.newCachedThreadPool(r -> new Thread(THREAD_GROUP, r,
            THREAD_GROUP.getName() + THREAD_GROUP.activeCount()));
    private static final Logger LOGGER = LoggerFactory.getLogger(Eval.class);

    static {
        THREAD_GROUP.setMaxPriority(Thread.MIN_PRIORITY);
    }

    public Eval() {
        this.name = "Eval";
        this.triggers = new String[]{"eval"};
        this.description = "Owner Only Command";
        this.hidden = true;
        this.ownerOnly = true;
    }

    @Override
    public void execute(CommandEvent event) {
        Message trigger = event.getTrigger();
        String arg = event.getArgument();
        //If they bypassed the check for owner, send DM to owner
        trigger.getJDA().getUserById(Config.owner_id).openPrivateChannel().queue((channel) -> channel.sendMessage(String.format("Eval Command Used!\nUser: %s#%s (%s)\nCommand: %s", trigger.getAuthor().getName(), trigger.getAuthor().getDiscriminator(), trigger.getAuthor().getId(), arg)).queue());
        GroovyShell shell = this.createShell(event);
        POOL.execute(() -> {
            try {
                Object result = shell.evaluate(arg);
                if (result == null) {
                    trigger.getChannel().sendMessage("Executed successfully").queue();
                    return;
                }
                trigger.getChannel().sendMessage("```\n" + result.toString() + "```").queue();
            } catch (Throwable t) {
                trigger.getChannel().sendMessage("**" + t.getClass().getName() + "**: " + t.getMessage()).queue();
                LOGGER.info("Eval Error", t);
            }
        });
    }

    private GroovyShell createShell(CommandEvent event) {
        Message message = event.getTrigger();
        Binding binding = new Binding();
        binding.setVariable("api", message.getJDA());
        binding.setVariable("jda", message.getJDA());
        binding.setVariable("channel", message.getChannel());
        binding.setVariable("author", message.getAuthor());
        binding.setVariable("message", message);
        binding.setVariable("msg", message);
        binding.setVariable("guild", message.getGuild());
        binding.setVariable("event", event);
        binding.setVariable("client", event.getClient());
        binding.setVariable("musicManager", event.getClient().getMusicManager());
        binding.setVariable("selfMember", event.getSelfMember());
        binding.setVariable("selfUser", event.getSelfUser());
        return new GroovyShell(binding);
    }
}