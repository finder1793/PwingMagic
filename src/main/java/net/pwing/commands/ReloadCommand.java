package net.pwing.commands;

import net.pwing.PwingMagic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PwingMagic plugin = PwingMagic.getInstance();

        // Reload configuration
        plugin.reloadConfig();

        // Reload spell list
        plugin.getSpellListManager().reload();

        sender.sendMessage("§aPwingMagic configuration and spell list reloaded.");
        return true;
    }
}
