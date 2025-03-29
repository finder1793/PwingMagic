package net.pwing.skills;

import java.util.HashMap;
import java.util.Map;

public class SkillRegistry {

    private final Map<String, SkillHandler> skills = new HashMap<>();

    /**
     * Registers a skill with the given name.
     *
     * @param name   The name of the skill.
     * @param handler The handler for the skill.
     */
    public void registerSkill(String name, SkillHandler handler) {
        skills.put(name.toLowerCase(), handler);
    }

    /**
     * Retrieves a skill by name.
     *
     * @param name The name of the skill.
     * @return The skill handler, or null if not found.
     */
    public SkillHandler getSkill(String name) {
        return skills.get(name.toLowerCase());
    }
}
