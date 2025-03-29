package net.pwing.skills;

import org.bukkit.entity.Player;

public interface SkillHandler {

    /**
     * Executes the skill for the given player.
     *
     * @param player The player executing the skill.
     * @return True if the skill was successfully executed, false otherwise.
     */
    boolean execute(Player player);
}
