package net.pwing.listeners;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import net.pwing.PwingMagic;
import net.pwing.WandManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class WandStatListener implements Listener {

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());

        // Remove stats from the previously held wand
        if (oldItem != null) {
            removeWandStats(player, oldItem);
        }

        // Apply stats from the newly held wand
        if (newItem != null) {
            applyWandStats(player, newItem);
        }
    }

    private void applyWandStats(Player player, ItemStack wand) {
        WandManager.WandData wandData = PwingMagic.getInstance().getWandManager().getWandFromItem(wand);
        if (wandData == null) {
            return; // Not a valid wand
        }

        MMOPlayerData playerData = MMOPlayerData.get(player);
        wandData.getStats().forEach((stat, value) -> {
            StatModifier modifier = new StatModifier(
                UUID.randomUUID(), // Unique identifier for the modifier
                "PwingMagic",      // Plugin key
                stat,              // Stat name
                value,             // Stat value
                ModifierType.FLAT, // Modifier type (flat value)
                EquipmentSlot.OTHER, // Equipment slot (not tied to a specific slot)
                ModifierSource.OTHER  // Modifier source (not tied to a specific source)
            );
            StatInstance instance = playerData.getStatMap().getInstance(stat);
            instance.addModifier(modifier); // Add the stat modifier
        });
    }

    private void removeWandStats(Player player, ItemStack wand) {
        WandManager.WandData wandData = PwingMagic.getInstance().getWandManager().getWandFromItem(wand);
        if (wandData == null) {
            return; // Not a valid wand
        }

        MMOPlayerData playerData = MMOPlayerData.get(player);
        wandData.getStats().forEach((stat, value) -> {
            StatInstance instance = playerData.getStatMap().getInstance(stat);
            instance.getModifiers().stream()
                .filter(mod -> mod instanceof StatModifier && ((StatModifier) mod).getKey().equals("PwingMagic"))
                .forEach(mod -> instance.removeModifier(((StatModifier) mod).getUniqueId())); // Remove modifiers by unique ID
        });
    }
}
