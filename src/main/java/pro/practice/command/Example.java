package pro.practice.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.security.auth.login.LoginException;

public class Example {

    public void Example() throws LoginException {
        JDA jda = JDABuilder.createDefault("bot-token").build();

        CommandHandler commandHandler = new CommandHandler(jda, "?");
        commandHandler.registerCommands(this);
    }

    @Command(name = "example", aliases = { "exp" }, log = false, memberOnly = true)
    public void exampleCommand(MessageReceivedEvent event) {
        event.getChannel().sendMessage("This is an example.").queue();
    }

}
