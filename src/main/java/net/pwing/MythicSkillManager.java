package net.pwing;

import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.comp.mythicmobs.mechanic.MMODamageMechanic;
import net.pwing.skills.SkillHandler;
import net.pwing.skills.SkillRegistry;
import org.bukkit.entity.Player;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.LivingEntity;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;

import io.lumine.mythic.lib.MythicLib;

public class MythicSkillManager {

    private final SkillRegistry skillRegistry;

    public MythicSkillManager() {
        this.skillRegistry = new SkillRegistry();
    }

    public SkillRegistry getSkillRegistry() {
        return skillRegistry;
    }

    // Execute a skill with mana cost, permission checks, and level-based scaling
    public boolean executeSkill(Player player, String skillName) {
        // Check if the player can cast the spell
        if (!PwingMagic.getInstance().getSpellDataManager().canCastSpell(player, skillName)) {
            player.sendMessage("§cYou cannot cast the spell: " + skillName);
            return false;
        }

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

        // Get spell level
        int spellLevel = PwingMagic.getInstance().getSpellDataManager().getSpellLevel(player, skillName);

        // Calculate damage based on base damage and spell level
        double baseDamage = spellData.getBaseDamage();
        double damage = baseDamage + (baseDamage * 0.1 * spellLevel); // Example: 10% more damage per level

        // Apply damage using MythicLib's APIs
        player.getWorld().getNearbyEntities(player.getLocation(), 5, 5, 5).stream()
            .filter(entity -> entity instanceof LivingEntity && !entity.equals(player))
            .forEach(entity -> {
                LivingEntity target = (LivingEntity) entity;
                DamageMetadata damageMeta = new DamageMetadata(damage, DamageType.SKILL, DamageType.MAGIC);
                AttackMetadata attackMeta = new AttackMetadata(damageMeta, target, StatProvider.get(player));
                MythicLib.plugin.getDamage().markAsMetadata(attackMeta);
                target.damage(damage);
                MythicLib.plugin.getDamage().unmarkAsMetadata(target);
            });

        // Execute the skill
        boolean success = skillHandler.execute(player);
        if (success) {
            playerData.giveMana(-manaCost); // Deduct mana
            player.sendMessage("§aSuccessfully cast skill: " + skillName + " (Mana cost: " + manaCost + ", Level: " + spellLevel + ", Damage: " + damage + ")");
        } else {
            player.sendMessage("§cFailed to cast skill: " + skillName);
        }

        return success;
    }
}
