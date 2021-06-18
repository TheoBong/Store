package io.github.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;

public abstract class BaseCommand extends Command {

    public BaseCommand(final String name) {
        super(name);
    }

    @Override
    public final boolean execute(final CommandSender sender, final String alias, final String[] args) {
        this.execute(sender, args);
        return true;
    }

    protected abstract void execute(CommandSender sender, String[] args);
}

