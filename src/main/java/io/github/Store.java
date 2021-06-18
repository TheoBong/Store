package io.github;

import io.github.commands.BaseCommand;
import io.github.commands.impl.LookupCommand;
import io.github.commands.impl.StoreCommand;
import io.github.paypal.CaptureTask;
import io.github.utils.PendingTransactions;
import io.github.utils.TransactionHistory;
import io.github.commands.impl.CancelCommand;
import io.github.commands.impl.TimerCommand;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.derkades.derkutils.bukkit.ItemBuilder;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class Store extends JavaPlugin {
    public static Store INSTANCE;
    private Set<BaseCommand> commands;
    private CommandMap commandMap;
    @Getter @Setter Boolean serverScanning = false;

    @Getter
    PendingTransactions pendingTransactions;
    @Getter
    TransactionHistory transactionHistory;

    File configFile;
    @Getter private YamlConfiguration config;

    @Override
    public void onEnable() {
        INSTANCE = this;

        commands = new HashSet<>();

        pendingTransactions = new PendingTransactions();
        pendingTransactions.importConfig();

        transactionHistory = new TransactionHistory();
        transactionHistory.importConfig();

        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(configFile);

        if (config.getString("CLIENT_ID").equals("") || config.getString("SECRET").equals("")) {
            getServer().getLogger().log(Level.SEVERE, "You must fill out your CLIENT_ID and SECRET in the config.yml");
            getServer().getLogger().log(Level.SEVERE, "Get your PayPal CLIENT_ID and SECRET by creating an APP here: https://developer.paypal.com/developer/applications");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getConfig().getBoolean("STORE_COMMAND.ENABLED")) {
            registerCommand(new StoreCommand(this));
        }

        if (getConfig().getBoolean("CANCEL_COMMAND.ENABLED")) {
            registerCommand(new CancelCommand(this));
        }

        if (getConfig().getBoolean("TIMER_COMMAND.ENABLED")) {
            registerCommand(new TimerCommand(this));
        }

        if (getConfig().getBoolean("LOOKUP_COMMAND.ENABLED")) {
            registerCommand(new LookupCommand(this));
        }

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new CaptureTask(this), 20L * config.getInt("CHECK_EVERY"), 20L * config.getInt("CHECK_EVERY"));
    }

    @Override
    public void onDisable() {
        pendingTransactions.exportConfig();
        transactionHistory.exportConfig();
        this.getServer().getScheduler().cancelTasks(this);
    }


    public Store registerCommand(BaseCommand command) {
        commands.add(command);
        commandMap.register(command.getName(), command);
        return this;
    }

    public static ItemBuilder getItemFromMaterialString(final Player player, final String materialString) {
        if (materialString.startsWith("head:")) {
            final String owner = materialString.split(":")[1];
            if (owner.equals("auto")) {
                return new ItemBuilder(player.getName());
            } else {
                return new ItemBuilder(owner);
            }
        } else {
            try {
                final Material material = Material.valueOf(materialString.toUpperCase());
                return new ItemBuilder(material);
            } catch (final IllegalArgumentException e) {
                player.sendMessage("Invalid item name " + materialString.toUpperCase());
                player.sendMessage("https://github.com/ServerSelectorX/ServerSelectorX/wiki/Item-names");
                return new ItemBuilder(Material.COBBLESTONE);
            }
        }
    }
}