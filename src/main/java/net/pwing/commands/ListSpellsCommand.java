package net.pwing.commands;

import net.pwing.PwingMagic;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ListSpellsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§cUsage: /listspells <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        List<String> spells = PwingMagic.getInstance().getSpellDataManager().getLearnedSpells(target);
        if (spells.isEmpty()) {
            sender.sendMessage("§c" + target.getName() + " has not learned any spells.");
        } else {
            sender.sendMessage("§a" + target.getName() + "'s learned spells:");
            for (String spell : spells) {
                sender.sendMessage("§7- " + spell);
            }
        }
        return true;
    }
}
