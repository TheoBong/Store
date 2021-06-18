package io.github.commands.impl;

import io.github.Store;
import io.github.commands.BaseCommand;
import io.github.utils.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimerCommand extends BaseCommand {

    private Store store;

    public TimerCommand(Store store) {
        super(store.getConfig().getString("TIMER_COMMAND.MAIN_COMMAND"));
        this.store = store;
        setAliases(store.getConfig().getStringList("TIMER_COMMAND.ALIASES"));
    }

    @Override
    protected void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            long expiry = store.getPendingTransactions().expiryTime(player.getUniqueId().toString());
            if (expiry == 0) {
                player.sendMessage(ChatColor.RED + "You don't have a pending transaction.");
                return;
            }

            long expiryTime = expiry - System.currentTimeMillis();

            player.sendMessage(ChatColor.GREEN + "Your pending transaction expires in: " + TimeUtil.formatTimeMillis(expiryTime));
        } else {
            sender.sendMessage("Only players can do this.");
        }
    }
}