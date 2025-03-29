package net.pwing.commands;

import net.pwing.PwingMagic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestSkillCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage("§cUsage: /testskill <skillName>");
            return true;
        }

        String skillName = args[0];
        PwingMagic.getInstance().getSkillManager().executeSkill(player, skillName);
        return true;
    }
}
