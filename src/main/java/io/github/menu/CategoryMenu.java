package io.github.menu;

import java.util.List;

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

public class CategoryMenu extends IconMenu {

    private final FileConfiguration config;

    public CategoryMenu(final Player player, final FileConfiguration config) {
        super(Store.INSTANCE, Colors.parseColors(config.getString("CATEGORY_MENU_TITLE")), config.getInt("CATEGORY_MENU_ROWS"), player);

        this.config = config;

        for (final String key : config.getConfigurationSection("CATEGORIES").getKeys(false)) {
            final ConfigurationSection section = config.getConfigurationSection("CATEGORIES." + key);

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

            final ItemBuilder builder = Store.getItemFromMaterialString(player, materialString);

            builder.amount(amount);
            builder.coloredName(PlaceholderUtil.parsePapiPlaceholders(player, display));
            builder.coloredLore(PlaceholderUtil.parsePapiPlaceholders(player, lore));

            final ItemStack item = builder.create();

            if (position < 0) {
                for (int i = 0; i < this.getInventory().getSize(); i++) {
                    if (!this.hasItem(i)) {
                        this.addItem(i, item);
                    }
                }
            } else {
                this.addItem(position, item);
            }
        }
    }

    @Override
    public boolean onOptionClick(final OptionClickEvent event) {
        final int slot = event.getPosition();
        ConfigurationSection subSection = null;

        for (final String key : config.getConfigurationSection("CATEGORIES").getKeys(false)) {
            final ConfigurationSection section = config.getConfigurationSection("CATEGORIES." + key);
            if (section.getInt("POSITION") == slot) {
                subSection = section;
            }
        }

        if (subSection == null) {
            System.out.println("Rip");
        }

        final Player player = event.getPlayer();

        new StoreMenu(player, subSection.getName(), config);
        return false;
    }
}