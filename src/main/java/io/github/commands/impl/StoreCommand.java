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
        if (args.length >= 1) {
            if (args[0].equals("reload") && sender.hasPermission("store.admin")) {
                store.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded config!");
                return;
            }
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length >= 1) {
                if (args[0].equals("help")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&' , "&7&m-------------------------------------"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&' , "&a&lSTORE &7- &fhttps://github.com/Cowings/Store/"));
                    player.sendMessage("");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&' , "&f/store &7- open the store menu"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&' , "&f/timer &7- check how long until your pending transaction expires"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&' , "&f/cancel &7- cancel your pending transaction."));

                    if (sender.hasPermission("store.admin")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&' , "&f/lookup <pending/historical> <orderId/playerUUID> &7- lookup a transaction"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&' , "&f/store reload &7- reload the config.yml"));
                    }
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&' , "&7&m-------------------------------------"));
                    return;
                }
            }

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
