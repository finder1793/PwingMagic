package net.pwing.commands;

import net.pwing.PwingMagic;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveWandCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /givewand <player> <wandName>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        String wandName = args[1];
        ItemStack wand = PwingMagic.getInstance().getWandManager().createWand(wandName, target);
        if (wand == null) {
            sender.sendMessage("§cWand not found: " + wandName);
            return true;
        }

        target.getInventory().addItem(wand);
        sender.sendMessage("§aGave " + wandName + " to " + target.getName() + ".");
        return true;
    }
}
