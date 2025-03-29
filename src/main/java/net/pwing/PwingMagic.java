package net.pwing;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import io.lumine.mythic.lib.api.stat.handler.StatHandler;

public class PwingMagic extends JavaPlugin {

    private static PwingMagic instance;
    private PlayerSpellDataManager spellDataManager;
    private MythicSkillManager skillManager;
    private SpellListManager spellListManager;
    private WandManager wandManager;
    private StatHandler statHandler;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config
        saveDefaultConfig();

        // Initialize managers
        spellDataManager = new PlayerSpellDataManager(getDataFolder());
        skillManager = new MythicSkillManager();
        spellListManager = new SpellListManager(getDataFolder());
        wandManager = new WandManager(getDataFolder());

        // Initialize StatHandler with a valid ConfigurationSection and key
        ConfigurationSection statsConfig = getConfig().getConfigurationSection("stats");
        if (statsConfig == null) {
            statsConfig = getConfig().createSection("stats");
        }
        statHandler = new StatHandler(statsConfig, "default");

        // Register commands
        getCommand("testskill").setExecutor(new net.pwing.commands.TestSkillCommand());
        getCommand("grantspell").setExecutor(new net.pwing.commands.GrantSpellCommand());
        getCommand("removespell").setExecutor(new net.pwing.commands.RemoveSpellCommand());
        getCommand("listspells").setExecutor(new net.pwing.commands.ListSpellsCommand());
        getCommand("wand").setExecutor(new net.pwing.commands.WandCommand());
        getCommand("wandmanage").setExecutor(new net.pwing.commands.WandManageCommand());
        getCommand("reload").setExecutor(new net.pwing.commands.ReloadCommand());
        getCommand("givewand").setExecutor(new net.pwing.commands.GiveWandCommand());

        // Register example skills
        skillManager.getSkillRegistry().registerSkill("Fireball", player -> {
            player.getWorld().createExplosion(player.getLocation(), 2.0F);
            return true;
        });

        skillManager.getSkillRegistry().registerSkill("Heal", player -> {
            player.setHealth(Math.min(player.getHealth() + 10.0, player.getMaxHealth()));
            return true;
        });

        // Register listeners
        getServer().getPluginManager().registerEvents(new net.pwing.listeners.WandStatListener(), this);
        getServer().getPluginManager().registerEvents(new net.pwing.listeners.WandRightClickListener(), this);

        getLogger().info("Registered example skills.");
        getLogger().info("PwingMagic has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PwingMagic has been disabled!");
    }

    public static PwingMagic getInstance() {
        return instance;
    }

    public PlayerSpellDataManager getSpellDataManager() {
        return spellDataManager;
    }

    public MythicSkillManager getSkillManager() {
        return skillManager;
    }

    public SpellListManager getSpellListManager() {
        return spellListManager;
    }

    public WandManager getWandManager() {
        return wandManager;
    }

    public StatHandler getStatHandler() {
        return statHandler;
    }
}
