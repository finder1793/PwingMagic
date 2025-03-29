package net.pwing;

import net.pwing.skills.SkillHandler;
import net.pwing.skills.SkillRegistry;
import io.lumine.mythic.lib.api.player.MMOPlayerData; // Correct import for MMOPlayerData
import org.bukkit.entity.Player;
import net.Indyuce.mmocore.api.player.PlayerData;

public class MythicSkillManager {

    private final SkillRegistry skillRegistry;

    public MythicSkillManager() {
        this.skillRegistry = new SkillRegistry();
    }

    public SkillRegistry getSkillRegistry() {
        return skillRegistry;
    }

    // Execute a skill with mana cost and permission checks
    public boolean executeSkill(Player player, String skillName) {
        SkillHandler skillHandler = skillRegistry.getSkill(skillName);
        if (skillHandler == null) {
            player.sendMessage("§cSkill not found: " + skillName);
            return false;
        }

        // Check permissions if required
        if (PwingMagic.getInstance().getConfig().getBoolean("permissions.require-permissions-for-spells", true)) {
            if (!player.hasPermission("pwingmagic.spell." + skillName.toLowerCase())) {
                player.sendMessage("§cYou do not have permission to cast the spell: " + skillName);
                return false;
            }
        }

        // Check mana cost
        SpellListManager.SpellData spellData = PwingMagic.getInstance().getSpellListManager().getSpell(skillName);
        if (spellData == null) {
            player.sendMessage("§cSkill data not found for: " + skillName);
            return false;
        }

        int manaCost = spellData.getManaCost();
        PlayerData playerData = PlayerData.get(player);
        if (playerData.getMana() < manaCost) {
            player.sendMessage("§cNot enough mana to cast " + skillName + ". Mana required: " + manaCost);
            return false;
        }

        // Execute the skill
        boolean success = skillHandler.execute(player);
        if (success) {
            playerData.giveMana(-manaCost); // Deduct mana
            player.sendMessage("§aSuccessfully cast skill: " + skillName + " (Mana cost: " + manaCost + ")");
        } else {
            player.sendMessage("§cFailed to cast skill: " + skillName);
        }

        return success;
    }
}
