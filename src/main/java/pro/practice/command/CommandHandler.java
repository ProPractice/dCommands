package pro.practice.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CommandHandler extends ListenerAdapter {

    private final Map<String, Entry<Method, Object>> commandMap = new HashMap<>();
    private final String prefix;

    public CommandHandler(JDABuilder builder, String prefix) {
        builder.addEventListeners(this);
       this.prefix = prefix;
    }

    public CommandHandler(JDA jda, String prefix) {
        jda.addEventListener(this);
        this.prefix = prefix;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        if(message.startsWith("?")) {
            handleCommand(event, message.split(" "));
        }
    }

    private void handleCommand(MessageReceivedEvent event, String[] args) {
        Entry<String, Entry<Method, Object>> entry = commandMap.entrySet().stream().filter(command -> command.getKey().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if(entry != null) {
            Method method = entry.getValue().getKey();
            Object methodObject = entry.getValue().getValue();
            Command command = method.getAnnotation(Command.class);
            try {
                method.invoke(methodObject, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            if(command.log()) System.out.println(event.getAuthor().getAsTag() + " has issued command: " + event.getMessage().getContentRaw());
        }
    }

    public void registerCommands(Object... objs) {
        for (Object obj : objs) {
            for (Method m : obj.getClass().getMethods()) {
                if (m.getAnnotation(Command.class) != null) {
                    Command command = m.getAnnotation(Command.class);
                    registerCommand(command.name(), m, obj);
                    for (String alias : command.aliases()) {
                        registerCommand(alias, m, obj);
                    }
                }
            }
        }
    }

    private void registerCommand(String label, Method m, Object obj) {
        commandMap.put(prefix + label.toLowerCase(), new AbstractMap.SimpleEntry<>(m, obj));
    }

}
