[Home](Home) > Attribute Station

**English** | [日本語](Attribute-Station-ja) | [中文](Attribute-Station-cn) | [한국어](Attribute-Station-kr)

---

## [station] Attribute Station Block

### `processingTime` (Default: `200`, Range: 1–72000)
Processing time in ticks. 20 ticks = 1 second.

- `200`: 10 seconds (**Default**)
- `100`: 5 seconds (fast)
- `1200`: 1 minute (slow)
- `72000`: 1 hour (maximum)

### `consumeItem` (Default: `false`)
Whether to consume an item during processing.

- `false`: No item consumed. Processing occurs just by inserting the gun (**Default**)
- `true`: Consumes the specified item to process

### `consumeItemId` (Default: `"minecraft:diamond"`)
Item ID to consume. Only effective when `consumeItem` is `true`.

**Examples:**
```toml
consumeItemId = "minecraft:diamond"          # Diamond
consumeItemId = "minecraft:netherite_ingot"  # Netherite Ingot
consumeItemId = "minecraft:emerald"          # Emerald
consumeItemId = "tacz:gunsmith_table"        # A TACZ mod item
```

### `consumeCount` (Default: `1`, Range: 1–64)
Number of items consumed per processing operation.

### `allowReroll` (Default: `true`)
Whether to allow rerolling (regenerating attributes) for guns that already have attributes.

- `true`: Can reroll any number of times (limited by `maxRerolls`)
- `false`: Guns with existing attributes cannot be reprocessed

### `maxRerolls` (Default: `0`, Range: 0–1000)
Maximum number of rerolls allowed per gun.

- `0`: **Unlimited** (reroll as many times as desired)
- `3`: Up to 3 rerolls
- `10`: Up to 10 rerolls

> Reroll count is shown in the item tooltip.
> Guns that have reached the limit cannot be processed in the Attribute Station.

### Material Configuration (station_materials.json)

File: `config/tacz_attributes_addon/station_materials.json`

When `consumeItem = true`, this JSON file allows registering multiple material types. Each material can have rarity constraints. A default file is auto-generated on first launch.

```json
[
  {
    "item": "minecraft:diamond",
    "count": 1
  },
  {
    "item": "minecraft:emerald",
    "count": 2,
    "minRarity": 2
  },
  {
    "item": "minecraft:nether_star",
    "count": 1,
    "targetRarity": 3
  },
  {
    "item": "minecraft:amethyst_shard",
    "count": 4,
    "maxRarity": 1
  }
]
```

| Field | Description |
|-------|-------------|
| `item` | Item ID (required) |
| `count` | Amount consumed (default: 1) |
| `targetRarity` | Exact rarity (0-3, highest priority) |
| `minRarity` | Minimum rarity guarantee (0-3) |
| `maxRarity` | Maximum rarity limit (0-3) |

All rarity constraint fields are optional. When omitted, no constraint is applied (normal random). Hot-reloadable via `/taczaddon reload`.

---

## Hopper Support

The Attribute Station supports hoppers.

| Hopper Direction | Accessed Slots |
|------------------|----------------|
| From above | Gun slot (0), Material slot (1) |
| From the side | Gun slot (0), Material slot (1) |
| From below | Output slot (2) |

### Example Automated Processing Line

```
[Hopper (top)] → Insert gun
[Attribute Station] ← Auto-processes
[Hopper (bottom)] → Collect output
```

If item consumption is enabled, you can feed materials via a hopper from the side.

---

[< Previous: Rarity Scoring](Rarity-Scoring) | [Next >: Enhancement Station](Enhancement-Station)
