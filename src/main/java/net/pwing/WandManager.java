package net.pwing;

import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.api.stat.handler.StatHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WandManager {

    private final Map<String, WandData> wands = new HashMap<>();
    private final String statFormat;
    private final String activeSpellFormat; // Declare the missing variable
    private final String boundWandFormat;

    public WandManager(File dataFolder) {
        File wandFile = new File(dataFolder, "wands.yml");
        if (!wandFile.exists()) {
            PwingMagic.getInstance().saveResource("wands.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(wandFile);
        FileConfiguration pluginConfig = PwingMagic.getInstance().getConfig();

        // Load lore formats from config.yml
        statFormat = pluginConfig.getString("lore-format.stat", "&7{stat-name}: &a+{stat-value} {stat-unit}");
        activeSpellFormat = pluginConfig.getString("lore-format.active-spell", "&6(Active)"); // Initialize the variable
        boundWandFormat = pluginConfig.getString("lore-format.bound-wand", "&7Bound to: &e{player-name}");

        for (String wandName : config.getConfigurationSection("wands").getKeys(false)) {
            String displayName = config.getString("wands." + wandName + ".display-name");
            int capacity = config.getInt("wands." + wandName + ".capacity");
            int customModelData = config.getInt("wands." + wandName + ".custom-model-data", 0);
            boolean bindToPlayer = config.getBoolean("wands." + wandName + ".bind-to-player", false);
            List<String> lore = config.getStringList("wands." + wandName + ".lore");
            List<String> flavorLore = config.getStringList("wands." + wandName + ".flavor-lore");

            Map<String, Double> stats = new HashMap<>();
            if (config.contains("wands." + wandName + ".stats")) {
                for (String stat : config.getConfigurationSection("wands." + wandName + ".stats").getKeys(false)) {
                    stats.put(stat, config.getDouble("wands." + wandName + ".stats." + stat));
                }
            }

            wands.put(wandName.toLowerCase(), new WandData(displayName, capacity, customModelData, stats, bindToPlayer, lore, flavorLore));
        }
    }

    public WandData getWand(String name) {
        return wands.get(name.toLowerCase());
    }

    public ItemStack createWand(String name, Player owner) {
        WandData wandData = getWand(name);
        if (wandData == null) {
            return null;
        }

        ItemStack wand = new ItemStack(Material.STICK);
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName(wandData.getDisplayName());
        List<String> lore = new ArrayList<>(wandData.getLore());

        // Add stats to lore
        wandData.getStats().forEach((stat, value) -> {
            String formattedStat = statFormat
                    .replace("{stat-name}", stat)
                    .replace("{stat-value}", String.valueOf(value))
                    .replace("{stat-unit}", ""); // Add units if needed
            lore.add(formattedStat);
        });

        // Add flavor lore
        lore.addAll(wandData.getFlavorLore());

        // Add binding information
        if (wandData.isBindToPlayer() && owner != null) {
            String boundLore = boundWandFormat.replace("{player-name}", owner.getName());
            lore.add(boundLore);
        }

        meta.setLore(lore);

        // Set custom model data
        if (wandData.getCustomModelData() > 0) {
            meta.setCustomModelData(wandData.getCustomModelData());
        }

        wand.setItemMeta(meta);

        // Attach custom data
        wand = attachCustomData(wand, wandData, owner);
        return wand;
    }

    private ItemStack attachCustomData(ItemStack wand, WandData wandData, Player owner) {
        ItemMeta meta = wand.getItemMeta();
        meta.getPersistentDataContainer().set(PwingMagicKeys.CAPACITY_KEY, PwingMagicKeys.INTEGER_TAG_TYPE, wandData.getCapacity());
        meta.getPersistentDataContainer().set(PwingMagicKeys.STATS_KEY, PwingMagicKeys.STRING_TAG_TYPE, serializeStats(wandData.getStats()));
        if (wandData.isBindToPlayer() && owner != null) {
            meta.getPersistentDataContainer().set(PwingMagicKeys.OWNER_KEY, PwingMagicKeys.STRING_TAG_TYPE, owner.getUniqueId().toString());
        }
        wand.setItemMeta(meta);
        return wand;
    }

    private String serializeStats(Map<String, Double> stats) {
        StringBuilder builder = new StringBuilder();
        stats.forEach((key, value) -> builder.append(key).append(":").append(value).append(";"));
        return builder.toString();
    }

    public void validateAndUpdateWand(ItemStack wand) {
        if (wand == null || !wand.hasItemMeta()) {
            return;
        }

        ItemMeta meta = wand.getItemMeta();
        String displayName = meta.getDisplayName();
        WandData wandData = wands.values().stream()
                .filter(data -> data.getDisplayName().equals(displayName))
                .findFirst()
                .orElse(null);

        if (wandData == null) {
            return;
        }

        // Update lore and stats if needed
        List<String> lore = new ArrayList<>(wandData.getLore());
        if (meta.getPersistentDataContainer().has(PwingMagicKeys.OWNER_KEY, PwingMagicKeys.STRING_TAG_TYPE)) {
            String ownerName = Bukkit.getOfflinePlayer(UUID.fromString(meta.getPersistentDataContainer().get(PwingMagicKeys.OWNER_KEY, PwingMagicKeys.STRING_TAG_TYPE))).getName();
            lore.add("§7Bound to: §e" + ownerName);
        }
        meta.setLore(lore);
        wand.setItemMeta(meta);
    }

    public WandData getWandFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }

        String displayName = item.getItemMeta().getDisplayName();
        return wands.values().stream()
                .filter(data -> data.getDisplayName().equals(displayName))
                .findFirst()
                .orElse(null);
    }

    public static class WandData {
        private final String displayName;
        private final int capacity;
        private final int customModelData;
        private final Map<String, Double> stats;
        private final boolean bindToPlayer;
        private final List<String> lore;
        private final List<String> flavorLore;

        public WandData(String displayName, int capacity, int customModelData, Map<String, Double> stats, boolean bindToPlayer, List<String> lore, List<String> flavorLore) {
            this.displayName = displayName;
            this.capacity = capacity;
            this.customModelData = customModelData;
            this.stats = stats;
            this.bindToPlayer = bindToPlayer;
            this.lore = lore;
            this.flavorLore = flavorLore;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getCapacity() {
            return capacity;
        }

        public int getCustomModelData() {
            return customModelData;
        }

        public Map<String, Double> getStats() {
            return stats;
        }

        public boolean isBindToPlayer() {
            return bindToPlayer;
        }

        public List<String> getLore() {
            return lore;
        }

        public List<String> getFlavorLore() {
            return flavorLore;
        }
    }
}
