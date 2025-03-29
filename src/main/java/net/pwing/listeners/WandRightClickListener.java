package net.pwing.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WandRightClickListener implements Listener {

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() != Material.STICK) {
            return;
        }

        ItemStack wand = event.getItem();
        ItemMeta meta = wand.getItemMeta();
        if (meta == null || !meta.getDisplayName().equals(ChatColor.GOLD + "Magic Wand")) {
            return;
        }

        List<String> lore = meta.getLore();
        if (lore == null) {
            return;
        }

        List<String> spells = new ArrayList<>();
        String activeSpell = null;

        for (String line : lore) {
            if (line.startsWith(ChatColor.GREEN.toString())) {
                activeSpell = ChatColor.stripColor(line.replace(" (Active)", ""));
            } else if (line.startsWith(ChatColor.AQUA.toString())) {
                spells.add(ChatColor.stripColor(line));
            }
        }

        if (spells.isEmpty()) {
            return;
        }

        int currentIndex = spells.indexOf(activeSpell);
        int nextIndex = (currentIndex + 1) % spells.size();
        String nextSpell = spells.get(nextIndex);

        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            if (line.startsWith(ChatColor.AQUA.toString()) || line.startsWith(ChatColor.GREEN.toString())) {
                lore.set(i, ChatColor.AQUA + ChatColor.stripColor(line.replace(" (Active)", ""))); // Reset all spells
            }
        }

        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).equals(ChatColor.AQUA + nextSpell)) {
                lore.set(i, ChatColor.GREEN + nextSpell + " (Active)"); // Mark the next spell as active
                break;
            }
        }

        meta.setLore(lore);
        wand.setItemMeta(meta);

        Player player = event.getPlayer();
        player.sendMessage(ChatColor.GOLD + "Active spell set to: " + ChatColor.GREEN + nextSpell);
    }
}
