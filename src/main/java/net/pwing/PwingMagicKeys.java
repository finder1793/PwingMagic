package net.pwing;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public class PwingMagicKeys {
    public static final NamespacedKey CAPACITY_KEY = new NamespacedKey(PwingMagic.getInstance(), "wand_capacity");
    public static final NamespacedKey STATS_KEY = new NamespacedKey(PwingMagic.getInstance(), "wand_stats");
    public static final NamespacedKey OWNER_KEY = new NamespacedKey(PwingMagic.getInstance(), "wand_owner");

    public static final PersistentDataType<Integer, Integer> INTEGER_TAG_TYPE = PersistentDataType.INTEGER;
    public static final PersistentDataType<String, String> STRING_TAG_TYPE = PersistentDataType.STRING;
}
