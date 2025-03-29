package net.pwing;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public List<String> getLearnedSpells(Player player) {
        File file = getPlayerFile(player);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getStringList("learned-spells");
    }

    public void addSpell(Player player, String spell) {
        List<String> spells = getLearnedSpells(player);
        if (!spells.contains(spell)) {
            spells.add(spell);
            saveSpells(player, spells);
        }
    }

    public void removeSpell(Player player, String spell) {
        List<String> spells = getLearnedSpells(player);
        if (spells.contains(spell)) {
            spells.remove(spell);
            saveSpells(player, spells);
        }
    }

    private void saveSpells(Player player, List<String> spells) {
        File file = getPlayerFile(player);
        YamlConfiguration config = new YamlConfiguration();
        config.set("learned-spells", spells);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
