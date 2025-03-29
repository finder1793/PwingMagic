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
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /grantspell <player> <spell>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        String spell = args[1];
        PwingMagic.getInstance().getSpellDataManager().addSpell(target, spell);
        sender.sendMessage("§aGranted spell '" + spell + "' to " + target.getName() + ".");
        target.sendMessage("§aYou have learned the spell: " + spell + "!");
        return true;
    }
}
