package io.github.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class BaseCommand extends Command {

    public BaseCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        execute(sender, args);
        return true;
    }

    protected abstract void execute(CommandSender sender, String[] args);
}

