package io.github.commands.impl;

import io.github.Store;
import io.github.commands.BaseCommand;
import io.github.menu.CategoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StoreCommand extends BaseCommand {

    private Store store;

    public StoreCommand(Store store) {
        super(store.getConfig().getString("STORE_COMMAND.MAIN_COMMAND"));
        this.store = store;
        setAliases(store.getConfig().getStringList("STORE_COMMAND.ALIASES"));
    }

    @Override
    protected void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (store.getPendingTransactions().isStored(player.getUniqueId().toString())) {
                player.sendMessage(ChatColor.RED + "You already have a pending transaction!");
                player.sendMessage(ChatColor.RED + "Use /cancelitem to cancel your pending transaction.");
                return;
            }

            new CategoryMenu(player, store.getConfig());
        } else {
            sender.sendMessage("Only players can do this.");
        }
    }
}
