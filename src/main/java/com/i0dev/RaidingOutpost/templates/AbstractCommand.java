package com.i0dev.RaidingOutpost.templates;

import com.i0dev.RaidingOutpost.Heart;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class AbstractCommand extends AbstractManager implements org.bukkit.command.CommandExecutor, org.bukkit.command.TabExecutor {

    // i0 did
    public List<String> tabCompleteHelper(String arg, Collection<String> options) {
        if (arg.equalsIgnoreCase("") || arg.equalsIgnoreCase(" "))
            return new ArrayList<>(options);
        else
            return options.stream().filter(s -> s.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());
    }

    /*
    Credit given to EmberCM
    Discord: Ember#1404
    GitHub: https://github.com/EmberCM
     */

    String command;

    public AbstractCommand(Heart heart, String command) {
        super(heart);
        this.command = command;
    }

    public AbstractCommand(Heart heart) {
        super(heart);
        String name = getClass().getSimpleName().toLowerCase();
        this.command = name.substring(2);
    }

    public abstract void execute(org.bukkit.command.CommandSender sender, String[] args);

    public List<String> tabComplete(org.bukkit.command.CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command
            command, String label, String[] args) {
        if (!command.getName().equals(this.command)) return false;
        execute(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!command.getName().equals(this.command)) return null;
        return tabComplete(sender, args);
    }

}