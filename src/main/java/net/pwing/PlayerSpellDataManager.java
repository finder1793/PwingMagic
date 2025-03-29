package net.pwing;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlayerSpellDataManager {
    private final File spellDataFolder;

    public PlayerSpellDataManager(File dataFolder) {
        this.spellDataFolder = new File(dataFolder, "player-spells");
        if (!spellDataFolder.exists()) {
            spellDataFolder.mkdirs();
        }
    }

    private File getPlayerFile(Player player) {
        return new File(spellDataFolder, player.getUniqueId() + ".yml");
    }

    public Map<String, Integer> getLearnedSpells(Player player) {
        File file = getPlayerFile(player);
        if (!file.exists()) {
            return new HashMap<>();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Map<String, Integer> spells = new HashMap<>();
        for (String spell : config.getConfigurationSection("learned-spells").getKeys(false)) {
            spells.put(spell, config.getInt("learned-spells." + spell));
        }
        return spells;
    }

    public void addSpell(Player player, String spell, int level) {
        Map<String, Integer> spells = getLearnedSpells(player);
        spells.put(spell, level);
        saveSpells(player, spells);
    }

    public void removeSpell(Player player, String spell) {
        Map<String, Integer> spells = getLearnedSpells(player);
        if (spells.containsKey(spell)) {
            spells.remove(spell);
            saveSpells(player, spells);
        }
    }

    public void levelUpSpell(Player player, String spell) {
        Map<String, Integer> spells = getLearnedSpells(player);
        if (spells.containsKey(spell)) {
            spells.put(spell, spells.get(spell) + 1);
            saveSpells(player, spells);
        }
    }

    public int getSpellLevel(Player player, String spell) {
        return getLearnedSpells(player).getOrDefault(spell, 0);
    }

    public boolean canCastSpell(Player player, String spell) {
        SpellListManager.SpellData spellData = PwingMagic.getInstance().getSpellListManager().getSpell(spell);
        if (spellData == null) {
            return false; // Spell does not exist
        }

        // Check if the player has permission or has been granted the spell
        String permission = spellData.getPermission();
        return (permission == null || player.hasPermission(permission)) || getLearnedSpells(player).containsKey(spell);
    }

    private void saveSpells(Player player, Map<String, Integer> spells) {
        File file = getPlayerFile(player);
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<String, Integer> entry : spells.entrySet()) {
            config.set("learned-spells." + entry.getKey(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
