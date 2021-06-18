package io.github.commands.impl;

import io.github.Store;
import io.github.commands.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelCommand extends BaseCommand {

    private Store store;

    public CancelCommand(Store store) {
        super(store.getConfig().getString("CANCEL_COMMAND.MAIN_COMMAND"));
        this.store = store;
        setAliases(store.getConfig().getStringList("CANCEL_COMMAND.ALIASES"));
    }

    @Override
    protected void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (store.getPendingTransactions().isStored(player.getUniqueId().toString())) {
                store.getPendingTransactions().removePendingPlayer(player.getUniqueId().toString());
                player.sendMessage(ChatColor.GREEN + "Successfully removed pending transaction!");
                return;
            }

            player.sendMessage(ChatColor.RED + "You don't have a pending transaction!");
        } else {
            sender.sendMessage("Only players can do this.");
        }
    }
}
