package net.pwing.commands;

import net.pwing.PwingMagic;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantSpellCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /grantspell <player> <spell> <level>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        String spell = args[1];
        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid level. Must be a number.");
            return true;
        }

        // Grant the spell even if the player lacks permission
        PwingMagic.getInstance().getSpellDataManager().addSpell(target, spell, level);
        sender.sendMessage("§aGranted spell '" + spell + "' (Level " + level + ") to " + target.getName() + ".");
        target.sendMessage("§aYou have learned the spell: " + spell + " (Level " + level + ")!");
        return true;
    }
}
