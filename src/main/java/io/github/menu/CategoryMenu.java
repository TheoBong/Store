package io.github.menu;

import io.github.Store;
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

public class CategoryMenu extends IconMenu {

    private final FileConfiguration config;
    private final Store store;

    public CategoryMenu(Store store, Player player) {
        super(store, Colors.parseColors(store.getConfig().getString("CATEGORY_MENU_TITLE")), store.getConfig().getInt("CATEGORY_MENU_ROWS"), player);

        this.store = store;
        this.config = store.getConfig();

        for (String key : config.getConfigurationSection("CATEGORIES").getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection("CATEGORIES." + key);

            int position;
            String materialString;
            String display;
            List<String> lore;
            int amount = 1;

            position = section.getInt("POSITION");
            materialString = section.getString("MATERIAL");
            display = section.getString("DISPLAY", " ");
            lore = section.getStringList("LORE");

            if (materialString == null) {
                player.sendMessage("Missing item option for item " + key);
                return;
            }

            if (materialString.equals("AIR")) {
                return;
            }

            ItemBuilder builder = store.getItemFromMaterialString(player, materialString);

            builder.amount(amount);
            builder.coloredName(PlaceholderUtil.parsePapiPlaceholders(player, display));
            builder.coloredLore(PlaceholderUtil.parsePapiPlaceholders(player, lore));

            ItemStack item = builder.create();

            if (position < 0) {
                for (int i = 0; i < getInventory().getSize(); i++) {
                    if (!hasItem(i)) {
                        addItem(i, item);
                    }
                }
            } else {
                addItem(position, item);
            }
        }
    }

    @Override
    public boolean onOptionClick(OptionClickEvent event) {
        int slot = event.getPosition();
        ConfigurationSection subSection = null;

        for (String key : config.getConfigurationSection("CATEGORIES").getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection("CATEGORIES." + key);
            if (section.getInt("POSITION") == slot) {
                subSection = section;
            }
        }

        if (subSection == null) {
            System.out.println("Rip");
        }

        Player player = event.getPlayer();

        new StoreMenu(store, player, subSection.getName());
        return false;
    }
}