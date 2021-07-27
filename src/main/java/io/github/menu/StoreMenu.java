package io.github.menu;

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

import java.util.List;

public class StoreMenu extends IconMenu {

    private final FileConfiguration config;
    private final Store store;

    public StoreMenu(Store store, Player player, String name) {
        super(store, Colors.parseColors(store.getConfig().getString("CATEGORIES." + name + ".DISPLAY")), store.getConfig().getInt("CATEGORIES." + name + ".ROWS"), player);

        this.store = store;
        this.config = store.getConfig();

        for (String key : config.getConfigurationSection("ITEMS").getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection("ITEMS." + key);

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

            ItemBuilder builder = Store.getItemFromMaterialString(player, materialString);

            builder.amount(amount);
            builder.coloredName(PlaceholderUtil.parsePapiPlaceholders(player, display));
            builder.coloredLore(PlaceholderUtil.parsePapiPlaceholders(player, lore));

            ItemStack item = builder.create();

            if (position < 0) {
                for (int i = 0; i < getInventory().getSize(); i++) {
                    if (!hasItem(i)) {
                        if (category.equals(name)) {
                            addItem(i, item);
                        }
                    }
                }
            } else {
                if (category.equals(name)) {
                    addItem(position, item);
                }
            }
        }
    }

    @Override
    public boolean onOptionClick(OptionClickEvent event) {
        int slot = event.getPosition();
        ItemStack itemStack = event.getItemStack();
        ConfigurationSection subSection = null;

        for (String key : config.getConfigurationSection("ITEMS").getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection("ITEMS." + key);
            String translated = ChatColor.translateAlternateColorCodes('&', section.getString("DISPLAY"));

            if (section.getInt("POSITION") == slot && translated.equals(itemStack.getItemMeta().getDisplayName())) {
                subSection = section;
            }
        }

        Player player = event.getPlayer();

        double cost = subSection.getDouble("COST");
        String item = subSection.getName();

        new DepositHandler(store).createDepositOrder(cost, item, player);
        return true;
    }
}