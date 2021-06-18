package io.github.menu;

import java.util.List;

import io.github.Store;
import io.github.paypal.DepositHandler;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.derkades.derkutils.bukkit.Colors;
import xyz.derkades.derkutils.bukkit.ItemBuilder;
import xyz.derkades.derkutils.bukkit.PlaceholderUtil;
import xyz.derkades.derkutils.bukkit.menu.IconMenu;
import xyz.derkades.derkutils.bukkit.menu.OptionClickEvent;

public class StoreMenu extends IconMenu {

    private final FileConfiguration config;

    public StoreMenu(final Player player, final String name, final FileConfiguration config) {
        super(Store.INSTANCE, Colors.parseColors(config.getString("CATEGORIES." + name + ".DISPLAY")), config.getInt("CATEGORIES." + name + ".ROWS"), player);

        this.config = config;

        for (final String key : config.getConfigurationSection("ITEMS").getKeys(false)) {
            final ConfigurationSection section = config.getConfigurationSection("ITEMS." + key);

            int position;
            String materialString;
            String display;
            String category;
            List<String> lore;
            int amount = 1;

            position = section.getInt("POSITION");
            materialString = section.getString("MATERIAL");
            display = section.getString("DISPLAY");
            category = section.getString("CATEGORY");
            lore = section.getStringList("LORE");

            if (materialString == null) {
                player.sendMessage("Missing item option for item " + key);
                return;
            }

            if (materialString.equals("AIR")) {
                return;
            }

            final ItemBuilder builder = Store.getItemFromMaterialString(player, materialString);

            builder.amount(amount);
            builder.coloredName(PlaceholderUtil.parsePapiPlaceholders(player, display));
            builder.coloredLore(PlaceholderUtil.parsePapiPlaceholders(player, lore));

            final ItemStack item = builder.create();

            if (position < 0) {
                for (int i = 0; i < this.getInventory().getSize(); i++) {
                    if (!this.hasItem(i)) {
                        if (category.equals(name)) {
                            this.addItem(i, item);
                        }
                    }
                }
            } else {
                if (category.equals(name)) {
                    this.addItem(position, item);
                }
            }
        }
    }

    @Override
    public boolean onOptionClick(final OptionClickEvent event) {
        final int slot = event.getPosition();
        ItemStack itemStack = event.getItemStack();
        ConfigurationSection subSection = null;

        for (final String key : config.getConfigurationSection("ITEMS").getKeys(false)) {
            final ConfigurationSection section = config.getConfigurationSection("ITEMS." + key);
            String translated = ChatColor.translateAlternateColorCodes('&', section.getString("DISPLAY"));

            if (section.getInt("POSITION") == slot && translated.equals(itemStack.getItemMeta().getDisplayName())) {
                subSection = section;
            }
        }

        final Player player = event.getPlayer();

        double cost = subSection.getDouble("COST");
        String item = subSection.getName();

        new DepositHandler((Store) Store.INSTANCE).createDepositOrder(cost, item, player);
        return true;
    }
}