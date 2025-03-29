# PwingMagic

PwingMagic is a Minecraft plugin designed to enhance gameplay by introducing wands, spells, and dynamic stat systems. It integrates with **MythicLib** and **MMOCore** to provide a robust and customizable experience for players and server administrators.

---

## Features

### Wands
- **Customizable Wands**: Define wands in `wands.yml` with:
  - Display name
  - Capacity for spells
  - Custom model data
  - Stats (e.g., attack damage, healing)
  - Flavor lore for backstory or flavor text
  - Optional player binding (bound wands)
- **Dynamic Lore**: Wands dynamically display:
  - Assigned spells
  - Active spell (marked as `(Active)`)
  - Bound player (if applicable)

### Spells
- **Customizable Spells**: Define spells in `spells.yml` with:
  - Mana cost
  - Lore for description
  - Customizable lore line template for wand display
- **Spell Management**:
  - Grant or remove spells from players.
  - List learned spells for a player.
  - Add or remove spells from wands dynamically.

### Stats
- **MMO Stats Integration**:
  - Wands apply stats (e.g., attack damage, critical chance) to players when held.
  - Stats are dynamically added and removed based on the wand being held.

### Commands
- `/testskill <skillName>`: Test casting a MythicMobs skill.
- `/grantspell <player> <spell>`: Grant a player access to a spell.
- `/removespell <player> <spell>`: Remove a spell from a player.
- `/listspells <player>`: List all spells a player has learned.
- `/wand <player> <capacity>`: Give a player a basic wand with a specified capacity.
- `/wandmanage <add|remove|list> <spell>`: Manage spells on your wand.
- `/givewand <player> <wandName>`: Give a player a predefined wand from `wands.yml`.
- `/reload`: Reload the plugin configuration and spell list.

---

## Configuration

### `config.yml`
The main configuration file for general plugin settings and lore formatting.

```yaml
features:
  enable-wands: true
  enable-spell-management: true
  enable-mana-integration: true

permissions:
  require-permissions-for-spells: true

stats:
  default:
    DAMAGE: 0
    CRIT_CHANCE: 0
    HEALING: 0

lore-format:
  stat: "&7{stat-name}: &a+{stat-value} {stat-unit}"
  active-spell: "&6(Active)"
  bound-wand: "&7Bound to: &e{player-name}"
```

### `wands.yml`
Define wands with customizable properties.

```yaml
wands:
  FireWand:
    display-name: "§6Fire Wand"
    capacity: 5
    custom-model-data: 1001
    stats:
      ATTACK_DAMAGE: 10
      CRITICAL_STRIKE_CHANCE: 5
    bind-to-player: false
    lore:
      - "§7A wand imbued with the power of fire."
      - "§7Capacity: §e5"
    flavor-lore:
      - "§8Forged in the heart of a volcano."
      - "§8Its flames never extinguish."
  HealWand:
    display-name: "§6Heal Wand"
    capacity: 3
    custom-model-data: 1002
    stats:
      HEALING: 15
    bind-to-player: true
    lore:
      - "§7A wand that heals the wielder."
      - "§7Capacity: §e3"
    flavor-lore:
      - "§8Blessed by ancient clerics."
      - "§8A symbol of hope and restoration."
```

### `spells.yml`
Define spells with customizable properties.

```yaml
spells:
  Fireball:
    mana-cost: 10
    lore:
      - "§6Fireball"
      - "§7Unleash a fiery explosion."
    lore-line-template: "&c{spell-name}"
  Heal:
    mana-cost: 8
    lore:
      - "§6Heal"
      - "§7Restore health to yourself or allies."
    lore-line-template: "&a{spell-name}"
  LightningStrike:
    mana-cost: 12
    lore:
      - "§6Lightning Strike"
      - "§7Call down a bolt of lightning."
    lore-line-template: "&b{spell-name}"
```

---

## Dependencies

PwingMagic depends on the following plugins:
- **[MythicLib](https://mythiclib.net/)**: Provides the stat system and integration.
- **[MMOCore](https://mmocore.net/)**: Handles mana and player data.

Ensure these plugins are installed and configured on your server.

---

## Installation

1. Download the plugin JAR file and place it in your server's `plugins` folder.
2. Install the required dependencies (**MythicLib** and **MMOCore**).
3. Start your server to generate the default configuration files.
4. Customize the configuration files (`config.yml`, `wands.yml`, `spells.yml`) as needed.
5. Reload or restart your server to apply changes.

---

## Permissions

- `pwingmagic.admin.reload`: Allows reloading the plugin configuration.
- `pwingmagic.spell.<spell>`: Grants permission to cast a specific spell (if permissions are enabled in `config.yml`).

---

## Development

### Build Instructions
1. Clone the repository.
2. Ensure you have Gradle installed.
3. Run `gradle build` to compile the plugin.

### Dependencies in `build.gradle`
```gradle
dependencies {
    implementation 'io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT'
    implementation 'io.lumine:MythicLib-dist:1.6.2-SNAPSHOT' 
    implementation 'net.Indyuce:MMOCore-API:1.13.1-SNAPSHOT' 
}
```

---

## Contributing

Contributions are welcome! Feel free to submit issues or pull requests to improve the plugin.

---
