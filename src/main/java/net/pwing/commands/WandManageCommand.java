package net.pwing.commands;

import net.pwing.PwingMagic;
import net.pwing.SpellListManager.SpellData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WandManageCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack wand = player.getInventory().getItemInMainHand();

        if (wand == null || wand.getType() != Material.STICK || !wand.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Magic Wand")) {
            player.sendMessage(ChatColor.RED + "You must hold a valid Magic Wand to manage spells.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /wandmanage <add|remove|list> <spell>");
            return true;
        }

        String action = args[0].toLowerCase();
        String spellName = args.length > 1 ? args[1] : null;

        ItemMeta meta = wand.getItemMeta();
        List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
        int capacity = getCapacityFromLore(lore);

        switch (action) {
            case "add":
                if (spellName == null) {
                    player.sendMessage(ChatColor.RED + "You must specify a spell to add.");
                    return true;
                }

                SpellData spellData = PwingMagic.getInstance().getSpellListManager().getSpell(spellName);
                if (spellData == null) {
                    player.sendMessage(ChatColor.RED + "The spell '" + spellName + "' does not exist.");
                    return true;
                }

                List<String> learnedSpells = PwingMagic.getInstance().getSpellDataManager().getLearnedSpells(player);
                if (!learnedSpells.contains(spellName)) {
                    player.sendMessage(ChatColor.RED + "You have not learned the spell: " + spellName);
                    return true;
                }

                if (getAssignedSpells(lore).size() >= capacity) {
                    player.sendMessage(ChatColor.RED + "Your wand is full. Remove a spell to add a new one.");
                    return true;
                }

                String formattedSpell = spellData.getLoreLineTemplate().replace("{spell-name}", spellName);
                lore.add(formattedSpell);
                updateLoreWithActiveSpell(lore, spellName); // Update lore with the new active spell
                meta.setLore(lore);
                wand.setItemMeta(meta);
                player.sendMessage(ChatColor.GREEN + "Added spell: " + spellName + " to your wand.");
                break;

            case "remove":
                if (spellName == null) {
                    player.sendMessage(ChatColor.RED + "You must specify a spell to remove.");
                    return true;
                }

                String spellToRemove = ChatColor.stripColor(spellName);
                if (!lore.contains(spellToRemove)) {
                    player.sendMessage(ChatColor.RED + "The spell: " + spellName + " is not on your wand.");
                    return true;
                }

                lore.remove(spellToRemove);
                updateLoreWithActiveSpell(lore, null); // Update lore after removing the spell
                meta.setLore(lore);
                wand.setItemMeta(meta);
                player.sendMessage(ChatColor.GREEN + "Removed spell: " + spellName + " from your wand.");
                break;

            case "list":
                player.sendMessage(ChatColor.GOLD + "Spells on your wand:");
                for (String line : lore) {
                    if (!line.startsWith(ChatColor.GRAY + "Capacity:")) {
                        player.sendMessage(ChatColor.AQUA + "- " + ChatColor.stripColor(line));
                    }
                }
                break;

            default:
                player.sendMessage(ChatColor.RED + "Invalid action. Use add, remove, or list.");
                break;
        }

        return true;
    }

    private int getCapacityFromLore(List<String> lore) {
        for (String line : lore) {
            if (line.startsWith(ChatColor.GRAY + "Capacity:")) {
                try {
                    return Integer.parseInt(ChatColor.stripColor(line).split(": ")[1]);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return 0;
    }

    private List<String> getAssignedSpells(List<String> lore) {
        List<String> spells = new ArrayList<>();
        for (String line : lore) {
            if (line.startsWith(ChatColor.AQUA.toString())) {
                spells.add(ChatColor.stripColor(line));
            }
        }
        return spells;
    }

    private void updateLoreWithActiveSpell(List<String> lore, String activeSpell) {
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            if (line.startsWith(ChatColor.AQUA.toString())) {
                lore.set(i, ChatColor.AQUA + ChatColor.stripColor(line)); // Reset all spells to inactive
            }
        }

        if (activeSpell != null) {
            for (int i = 0; i < lore.size(); i++) {
                if (lore.get(i).equals(ChatColor.AQUA + activeSpell)) {
                    lore.set(i, ChatColor.GREEN + activeSpell + " " + PwingMagic.getInstance().getConfig().getString("lore-format.active-spell", "&6(Active)")); // Mark the active spell
                    break;
                }
            }
        }
    }
}
