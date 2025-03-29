package net.pwing;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellListManager {
    private final Map<String, SpellData> spells = new HashMap<>();

    public SpellListManager(File dataFolder) {
        File spellFile = new File(dataFolder, "spells.yml");
        if (!spellFile.exists()) {
            PwingMagic.getInstance().saveResource("spells.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(spellFile);
        for (String spellName : config.getConfigurationSection("spells").getKeys(false)) {
            int manaCost = config.getInt("spells." + spellName + ".mana-cost");
            List<String> lore = config.getStringList("spells." + spellName + ".lore");
            String loreLineTemplate = config.getString("spells." + spellName + ".lore-line-template", "&7{spell-name}");
            spells.put(spellName, new SpellData(manaCost, lore, loreLineTemplate));
        }
    }

    public SpellData getSpell(String spellName) {
        return spells.get(spellName);
    }

    public boolean isSpellAvailable(String spellName) {
        return spells.containsKey(spellName);
    }

    public void reload() {
        spells.clear();
        File spellFile = new File(PwingMagic.getInstance().getDataFolder(), "spells.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(spellFile);

        for (String spellName : config.getConfigurationSection("spells").getKeys(false)) {
            int manaCost = config.getInt("spells." + spellName + ".mana-cost");
            List<String> lore = config.getStringList("spells." + spellName + ".lore");
            String loreLineTemplate = config.getString("spells." + spellName + ".lore-line-template", "&7{spell-name}");
            spells.put(spellName, new SpellData(manaCost, lore, loreLineTemplate));
        }
    }

    public static class SpellData {
        private final int manaCost;
        private final List<String> lore;
        private final String loreLineTemplate;

        public SpellData(int manaCost, List<String> lore, String loreLineTemplate) {
            this.manaCost = manaCost;
            this.lore = lore;
            this.loreLineTemplate = loreLineTemplate;
        }

        public int getManaCost() {
            return manaCost;
        }

        public List<String> getLore() {
            return lore;
        }

        public String getLoreLineTemplate() {
            return loreLineTemplate;
        }
    }
}
