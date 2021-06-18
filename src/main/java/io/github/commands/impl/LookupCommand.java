package io.github.commands.impl;

import io.github.Store;
import io.github.commands.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONObject;

public class LookupCommand extends BaseCommand {

    private Store store;

    public LookupCommand(Store store) {
        super(store.getConfig().getString("LOOKUP_COMMAND.MAIN_COMMAND"));
        this.store = store;
        setAliases(store.getConfig().getStringList("LOOKUP_COMMAND.ALIASES"));
    }

    @Override
    protected void execute(final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /lookup <pending/historical> <orderId/playerUUID (with dashes)>");
            return;
        }

        if (args[0].equals("pending")) {
            if (args[1].length() == 36) { //player uuid
                JSONObject jsonObject = store.getPendingTransactions().getPendingTransactionPlayer(args[1]);

                if (jsonObject == null) return;

                sender.sendMessage(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',
                        store.getConfig().getString("LOOKUP_COMMAND.PENDING_MESSAGE")
                                .replace("{player}", (String) jsonObject.get("playerUUID"))
                                .replace("{item}", (String) jsonObject.get("item"))
                                .replace("{cost}", (double) jsonObject.get("cost") + "")
                                .replace("{expiry}", (long) jsonObject.get("expiry") + "")));
            } else if (args[1].length() == 17) { //order id
                JSONObject jsonObject = store.getPendingTransactions().getPendingTransaction(args[1]);

                if (jsonObject == null) return;

                sender.sendMessage(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',
                        store.getConfig().getString("LOOKUP_COMMAND.PENDING_MESSAGE")
                                .replace("{player}", (String) jsonObject.get("playerUUID"))
                                .replace("{item}", (String) jsonObject.get("item"))
                                .replace("{cost}", (double) jsonObject.get("cost") + "")
                                .replace("{expiry}", (long) jsonObject.get("expiry") + "")));
            } else { //invalid
                sender.sendMessage(ChatColor.RED + "Usage: /lookup <pending/historical> <orderId/playerUUID (with dashes)>");
            }
        } else if (args[0].equals("historical")) {
            if (args[1].length() == 36) { //player uuid
                JSONObject jsonObject = store.getTransactionHistory().getTransactionPlayer(args[1]);

                if (jsonObject == null) return;

                sender.sendMessage(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',
                        store.getConfig().getString("LOOKUP_COMMAND.HISTORIC_MESSAGE")
                                .replace("{player}", (String) jsonObject.get("playerUUID"))
                                .replace("{item}", (String) jsonObject.get("item"))
                                .replace("{cost}", (double) jsonObject.get("cost") + "")
                                .replace("{created}", (long) jsonObject.get("created") + "")
                                .replace("{captured}", (long) jsonObject.get("captured") + "")));
            } else if (args[1].length() == 17) { //order id
                JSONObject jsonObject = store.getTransactionHistory().getTransaction(args[1]);

                if (jsonObject == null) return;

                sender.sendMessage(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',
                        store.getConfig().getString("LOOKUP_COMMAND.HISTORIC_MESSAGE")
                                .replace("{player}", (String) jsonObject.get("playerUUID"))
                                .replace("{item}", (String) jsonObject.get("item"))
                                .replace("{cost}", (double) jsonObject.get("cost") + "")
                                .replace("{created}", (long) jsonObject.get("created") + "")
                                .replace("{captured}", (long) jsonObject.get("captured") + "")));
            } else { //invalid
                sender.sendMessage(ChatColor.RED + "Usage: /lookup <pending/historical> <orderId/playerUUID (with dashes)>");
            }
        }

    }
}