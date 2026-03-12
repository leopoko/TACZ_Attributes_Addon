[Home](Home) > Commands

**English** | [日本語](Commands-ja) | [中文](Commands-cn) | [한국어](Commands-kr)

---

## Commands

Requires OP permission (level 2).

### `/taczaddon clear`
Removes all addon attributes (random, fixed, score, rarity) from the held gun.

### `/taczaddon clear random`
Removes random attributes only. Fixed attributes are preserved.

### `/taczaddon clear fixed`
Removes fixed attributes only. Random attributes are preserved.

### `/taczaddon clear enhanced`
Removes enhanced attributes only. Random and fixed attributes are preserved.

### `/taczaddon add <attribute> <value> [operation]`
Manually adds an attribute to the held gun's enhanced modifiers. If the same attribute already exists in enhanced modifiers, values are merged (added together).

- `attribute`: Full attribute ID (e.g. `tacz_attributes:gun_damage`). Tab completion available.
- `value`: Numeric value (e.g. `0.15`, `-0.10`)
- `operation`: Optional. Default: `MULTIPLY_BASE`. Options: `ADDITION`, `MULTIPLY_BASE`, `MULTIPLY_TOTAL`

> **Tip:** This command is useful for KubeJS integration — modpack developers can create custom items that add specific attributes to guns via this command.

### `/taczaddon reroll`
Regenerates the random attributes on the held gun. Ignores the reroll count limit.

### `/taczaddon reload`
Reloads `attribute_pool.json`, `weapon_attributes.json`, and `gun_attribute_overrides.json`.
Config changes take effect without restarting the game.

### `/taczaddon info`
Displays detailed addon data for the held gun. When Apotheosis integration is enabled, socket/gem information is also shown.

### `/taczaddon config get <key>`
Retrieves a config value. Tab completion shows available keys.

### `/taczaddon config set <key> <value>`
Temporarily changes a config value. Resets on server restart.

**Configurable keys:**
- `enableRandomOnObtain`, `enableWeaponTypeAttributes`, `enableAttributeStation`
- `enableApotheosis`, `enableRarityScoring`, `showEmptySlots`
- `randomMode` (FULL_RANDOM / ADAPTIVE / RARITY_ADAPTIVE / BALANCED)
- `fixedAttributeMode` (FIXED_ONLY / RANDOM_ONLY / BOTH_STACKING / FIXED_INFLUENCES_RANDOM)
- `minAttributes`, `maxAttributes`
- `valueDistribution` (LINEAR / EXPONENTIAL / QUADRATIC)
- `distributionExponent`, `raritySpreadFactor`, `buffDebuffRatio`
- `uncommonThreshold`, `rareThreshold`, `epicThreshold`
- `processingTime`, `consumeItem`, `consumeItemId`, `consumeCount`
- `allowReroll`, `maxRerolls`
- `gunBaseSockets`, `socketsScaleWithRarity`
- `commonSockets`, `uncommonSockets`, `rareSockets`, `epicSockets`
- `enhancementMaxTypes`, `enhancementExistingOnly`

---

## Fixed Attribute Reroll Independence

Fixed attributes (defined in weapon_attributes.json) are not regenerated on reroll.

- Fixed attributes are applied only on first obtain
- Rerolling regenerates random attributes only
- Fixed attributes are preserved as-is

**Example:** If an AK-47 has `gun_damage: 0.95` configured as a fixed attribute:
1. On first obtain: Fixed `gun_damage: 0.95` + random attributes are applied
2. After reroll: Fixed `gun_damage: 0.95` is kept, only random attributes change
3. Even if random generates `gun_damage: 1.2`, the fixed `0.95` remains and both stack

---

| | |
|:---|---:|
| [Previous: Gun Attribute Overrides](Gun-Attribute-Overrides) | [Next: Apotheosis](Apotheosis) |
