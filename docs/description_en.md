# TACZ Attributes Addon

**Transform every gun into a unique find.** This addon bridges [TACZ](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero) and [TACZ Attributes](https://www.curseforge.com/minecraft/mc-mods/tacz-attributes), adding a randomized attribute and rarity system to all TACZ guns. Each gun you pick up rolls unique stat modifiers — damage, accuracy, recoil, fire rate, and more — turning your arsenal into a loot-driven progression system.

---

## Features

### Random Attribute System
Every TACZ gun you obtain is automatically assigned random attributes from a pool of **50+ modifiers** across categories like damage, accuracy, recoil, handling speed, magazine capacity, and more. Attributes are intelligently filtered by gun type — shotguns won't roll sniper-only stats.

**4 Generation Modes:**
- **Full Random** — All attributes, no filtering
- **Adaptive** — Filtered by gun type and fire mode
- **Rarity Adaptive** *(default)* — Filtered + weighted by rarity tier + skewed distribution
- **Balanced** — All of the above + buff/debuff ratio balancing

### Rarity Tiers
Guns are assigned a rarity tier based on their total attribute score:

| Tier | Color | Default Threshold |
|------|-------|-------------------|
| **Common** | White | < 50 |
| **Uncommon** | Yellow | 50+ |
| **Rare** | Aqua | 70+ |
| **Epic** | Purple | 100+ |

Rarity affects the item's name color and integrates with Minecraft's vanilla rarity display.

### Attribute Station
A craftable workstation block for **rerolling** gun attributes. Place a gun inside, wait for processing, and receive a gun with freshly randomized stats. Supports:
- Configurable processing time
- Optional material cost (e.g. diamonds)
- Reroll count limits
- Hopper automation (input from top/sides, output from bottom)

### Enhancement Station
A second-tier workstation for **selectively improving** specific attributes. Unlike the Attribute Station's full reroll, the Enhancement Station presents **3 random enhancement choices** — pick the one you want to apply. Features:
- Choose which stat to boost from randomized options
- Reroll choices for a material cost
- Configurable max enhancement count per gun
- Scaling material costs as you enhance further
- Option to restrict to positive buffs only

### Per-Gun Fixed Attributes
Define base attribute presets for specific gun models via JSON config. For example, give the AK-47 a slight damage boost but increased recoil. Fixed attributes coexist independently with random attributes — both apply simultaneously.

### Barrage (Legendary Item)
A special offhand item crafted from a Vector .45 surrounded by 8 Enchanted Golden Apples. When held in the offhand:
- **+100% Fire Rate** (doubles RPM)
- **+50% Draw Speed**
- **+50% ADS Speed**

Fire-resistant, Epic rarity, with enchantment glint.

### Detailed Tooltips
Hold **Shift** to expand gun tooltips showing:
- Rarity tier and score
- Random modifiers (gold text)
- Fixed modifiers (blue text)
- Enhanced modifiers (purple text)
- Reroll and enhancement counts
- Color-coded buff (green) / debuff (red) indicators

### Admin Commands
Server operators have full control:

| Command | Description |
|---------|-------------|
| `/taczaddon clear` | Remove all addon attributes from held gun |
| `/taczaddon clear random/fixed` | Remove specific attribute type |
| `/taczaddon reroll` | Reroll random attributes |
| `/taczaddon reload` | Hot-reload JSON config files |
| `/taczaddon info` | Display detailed attribute data |
| `/taczaddon config get/set` | View or temporarily change settings |

---

## Configuration

All settings are fully configurable via `config/tacz_attributes_addon-common.toml`:

- **Feature toggles** — Enable/disable random attributes, fixed attributes, stations, rarity scoring
- **Generation mode** — Choose from 4 randomization algorithms
- **Attribute count** — Min/max attributes per gun (default: 1-5)
- **Value distribution** — Linear, Exponential, or Quadratic curves
- **Rarity thresholds** — Customize score boundaries for each tier
- **Station settings** — Processing time, material costs, reroll limits
- **Enhancement settings** — Choice count, max enhancements, cost scaling

### External JSON Configs
- `config/tacz_attributes_addon/attribute_pool.json` — Full attribute pool with weights, ranges, and rarity tiers
- `config/tacz_attributes_addon/weapon_attributes.json` — Per-gun fixed attribute definitions

Both can be hot-reloaded with `/taczaddon reload`.

---

## Recipes

**Attribute Station:**
| | | |
|---|---|---|
| Iron Ingot | Iron Ingot | Iron Ingot |
| Iron Ingot | Diamond | Iron Ingot |
| Wooden Slab | Wooden Slab | Wooden Slab |

**Enhancement Station:**
| | | |
|---|---|---|
| Diamond | Gold Block | Diamond |
| Iron Ingot | Attribute Station | Iron Ingot |
| Wooden Slab | Wooden Slab | Wooden Slab |

**Barrage:**
| | | |
|---|---|---|
| Enchanted Golden Apple | Enchanted Golden Apple | Enchanted Golden Apple |
| Enchanted Golden Apple | Vector .45 (TACZ Gun) | Enchanted Golden Apple |
| Enchanted Golden Apple | Enchanted Golden Apple | Enchanted Golden Apple |

---

## Requirements

- **Minecraft** 1.20.1
- **Forge** 47+
- **TACZ** (Timeless and Classics Zero) — Required
- **TACZ Attributes** — Required
- **Apotheosis** — Optional (socket integration)

---

## Multiplayer

Fully multiplayer compatible. Attribute data is stored in item NBT and syncs automatically. Per-player attribute tracking ensures no conflicts between players.

---

## Summary (Short Description)

> Adds a randomized attribute and rarity system to TACZ guns. Each gun rolls unique stats — damage, accuracy, recoil, fire rate, and more. Includes Attribute Station for rerolling, Enhancement Station for selective upgrades, rarity tiers (Common to Epic), 50+ configurable attributes, per-gun presets, and full multiplayer support.
