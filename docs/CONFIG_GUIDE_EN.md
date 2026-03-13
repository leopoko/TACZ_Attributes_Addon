# TACZ Attributes Addon - Configuration Guide

Config file: `config/tacz_attributes_addon-common.toml` (auto-generated on first launch)

---

## [general] Feature Toggles

Enable or disable each feature independently.

### `enableRandomOnObtain` (Default: `true`)
Automatically assigns random attributes to a gun when it enters inventory.

- `true`: Random attributes are automatically applied when a player obtains a gun
- `false`: No automatic assignment (manual generation via Attribute Station only)

> **Note:** Disabling this setting does not affect guns that already have attributes. It only applies to newly obtained guns.

### `enableWeaponTypeAttributes` (Default: `true`)
Enables fixed attributes per gun model, configured in `config/tacz_attributes_addon/weapon_attributes.json`.

- `true`: Applies fixed attributes based on weapon_attributes.json
- `false`: No fixed attributes are applied

### `enableAttributeStation` (Default: `true`)
Enables the Attribute Station block functionality.

- `true`: Players can insert guns into the block to generate or reroll attributes
- `false`: The block can still be placed but performs no processing

### `enableApotheosis` (Default: `true`)
Enables integration with the Apotheosis mod. When Apotheosis is installed, guns gain socket slots and gun-specific gems can be inserted.

- `true`: When Apotheosis is present, socket/gem integration is enabled
- `false`: Socket/gem features are disabled even if Apotheosis is present

### `enableRarityScoring` (Default: `true`)
Enables the rarity scoring system based on applied attributes.

- `true`: Calculates a score from attribute values and scoreWeights, then determines rarity (COMMON/UNCOMMON/RARE/EPIC). The gun item's name color changes to match its rarity.
- `false`: Skips rarity calculation. All guns display as COMMON.

### `showEmptySlots` (Default: `false`)
Shows empty attribute slots in tooltips when the gun has fewer random attributes than `maxAttributes`.

- `true`: Displays `[ ] Empty Attribute Slot` lines in the tooltip for unfilled slots. Uses per-gun `maxAttributes` override if set, otherwise the global value.
- `false`: No empty slot indication

---

## [random] Random Attribute Generation

Fine-tune the random attribute generation algorithm.

### `randomMode` (Default: `RARITY_ADAPTIVE`)

The algorithm used to select random attributes.

| Mode | Description | Recommended For |
|------|-------------|-----------------|
| `FULL_RANDOM` | Fully random from the entire attribute pool. Ignores gun type — SMGs may receive sniper-specific attributes. | Casual / Chaotic |
| `ADAPTIVE` | Filters attributes by gun type and fire mode. Off-type attributes cannot be selected. | Balanced play |
| `RARITY_ADAPTIVE` | ADAPTIVE + rarity weighting. Higher rarity attributes are less likely to appear + value skewing (lower values are more common). | **Recommended (Default)** |
| `BALANCED` | RARITY_ADAPTIVE + automatic buff/debuff ratio balancing, controlled by `buffDebuffRatio`. | Fairness-focused |

### `fixedAttributeMode` (Default: `BOTH_STACKING`)

The relationship between fixed attributes (weapon_attributes.json) and random attributes.

| Mode | Fixed Attrs | Random Attrs | Description |
|------|-------------|--------------|-------------|
| `FIXED_ONLY` | Applied | **Not generated** | Fixed attributes only. No randomness. |
| `RANDOM_ONLY` | **Not applied** | Generated | Random only. Fixed config is ignored. |
| `BOTH_STACKING` | Applied | Generated | Both applied independently (stored in separate NBT tags). **Recommended** |
| `FIXED_INFLUENCES_RANDOM` | Applied | Generated (influenced) | Fixed attributes affect the weights of random generation. |

### `minAttributes` (Default: `1`, Range: 0–20)
Minimum number of random attributes assigned to one gun. Setting to 0 means some guns may receive no attributes.

### `maxAttributes` (Default: `4`, Range: 0–20)
Maximum number of random attributes assigned to one gun. The actual count is randomly determined between min and max.

> **Example:** min=2, max=5 → Each gun receives 2–5 random attributes.

### `valueDistribution` (Default: `EXPONENTIAL`)

The distribution curve for attribute values. Controls how values are distributed within the min–max range.

| Distribution | Characteristic | Description |
|--------------|----------------|-------------|
| `LINEAR` | Uniform | All values have equal probability |
| `EXPONENTIAL` | Biased toward lower values | Small values are more common, large values are rare. Controlled by `distributionExponent`. **Recommended** |
| `QUADRATIC` | Moderately biased low | Less extreme than EXPONENTIAL |

### `distributionExponent` (Default: `2.0`, Range: 1.0–10.0)
Exponent for EXPONENTIAL distribution. Higher values push results further toward the minimum.

- `1.0`: Equivalent to LINEAR (no bias)
- `2.0`: Moderately biased toward lower values (**Recommended**)
- `5.0`: Strongly biased toward lower values (high values rarely appear)
- `10.0`: Extremely biased toward lower values

### `raritySpreadFactor` (Default: `2.0`, Range: 1.0–10.0)
In RARITY_ADAPTIVE/BALANCED modes, controls the weight dispersion by rarity tier.

- `1.0`: All rarity tiers have equal probability of being selected
- `2.0`: Higher rarity attributes are less likely (**Recommended**)
- `5.0`: Tier 4 rarity attributes are extremely rare

> **Formula:** weight = baseWeight / (rarityTier ^ raritySpreadFactor)

### `buffDebuffRatio` (Default: `1.0`, Range: 0.1–5.0)
BALANCED mode only. Target ratio of buffs to debuffs.

- `0.5`: Twice as many debuffs (hardcore)
- `1.0`: Equal buffs and debuffs (**Recommended**)
- `2.0`: Twice as many buffs (player-favored)

---

## [rarity] Rarity Score Thresholds

Rarity is determined by the total attribute score.

### Score Calculation

```
Score = Σ (attribute value × scoreWeight)
```

- **Normal attributes** (damage, etc., scoreWeight=+100): Higher buff values → higher score
- **Inverted attributes** (recoil, etc., scoreWeight=-90): Recoil reduction (a buff) → higher score

### `uncommonThreshold` (Default: `100`)
Score at or above this value → UNCOMMON (yellow).

### `rareThreshold` (Default: `300`)
Score at or above this value → RARE (aqua).

### `epicThreshold` (Default: `600`)
Score at or above this value → EPIC (purple).

| Score | Rarity | Item Name Color |
|-------|--------|-----------------|
| 0–99 | COMMON | White |
| 100–299 | UNCOMMON | Yellow |
| 300–599 | RARE | Aqua |
| 600+ | EPIC | Purple |

> **Tuning Tip:** Lowering thresholds makes EPIC easier to obtain.
> Adjust these values alongside attribute count and value ranges.
> Example: If maxAttributes=2, lower the thresholds; if maxAttributes=8, raise them.

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

## [apotheosis] Apotheosis Integration Settings

Settings for the socket/gem features that are enabled when the Apotheosis mod is installed.

### `gunBaseSockets` (Default: `2`, Range: 0–6)
Fixed socket count used when `socketsScaleWithRarity` is `false`.

### `socketsScaleWithRarity` (Default: `true`)
Whether socket count scales with the gun's rarity.

- `true`: Socket count varies by rarity (uses the per-rarity settings below)
- `false`: All guns have the fixed number of sockets specified by `gunBaseSockets`

### `commonSockets` (Default: `1`, Range: 0–6)
Socket count for COMMON rarity guns.

### `uncommonSockets` (Default: `2`, Range: 0–6)
Socket count for UNCOMMON rarity guns.

### `rareSockets` (Default: `3`, Range: 0–6)
Socket count for RARE rarity guns.

### `epicSockets` (Default: `4`, Range: 0–6)
Socket count for EPIC rarity guns.

---

## [enhancement] Enhancement Station Block

### `maxTypes` (Default: `0`, Range: 0–100)
Maximum number of distinct enhancement attribute types per gun. When the number of distinct enhanced attribute types reaches this limit, only already-enhanced attributes appear as choices (values increase but no new types are added).

- `0`: Unlimited (no type restriction)
- `>0`: When distinct types reach this number, choices are restricted to existing enhanced attributes

> **Note:** This is independent from `maxEnhancements` which is a hard cap on total enhancement applications. `maxTypes` controls attribute variety, not total count.

### `existingOnly` (Default: `false`)
When enabled, the Enhancement Station only offers choices from attributes that are already present on the gun (random + fixed + enhanced modifiers).

- `true`: Enhancement choices are restricted to attributes already on the gun
- `false`: All applicable attributes from the pool can appear as choices

> **Note:** Per-gun `maxEnhancement` override in `gun_attribute_overrides.json` can also trigger this restriction when the type limit is reached.

---

## Per-Weapon Fixed Attributes (weapon_attributes.json)

File: `config/tacz_attributes_addon/weapon_attributes.json`

An empty JSON file `{}` is generated on first launch. You can configure fixed attributes per gun ID.

### Format

```json
{
  "gun_id": [
    {
      "attribute": "attribute_id",
      "value": number,
      "operation": "operator"
    }
  ]
}
```

### Operations

| Operation | Description | Example |
|-----------|-------------|---------|
| `MULTIPLY_BASE` | Multiplied against the base value. 0.10 = +10% | Used for most attributes |
| `ADDITION` | Added to the base value | knockback_base, ammo_recovery_amount, etc. |
| `MULTIPLY_TOTAL` | Multiplied against the final value | Special cases only |

### Example Configuration

```json
{
  "tacz:ak47": [
    {
      "attribute": "tacz_attributes:gun_damage",
      "value": 0.05,
      "operation": "MULTIPLY_BASE"
    },
    {
      "attribute": "tacz_attributes:recoil",
      "value": 0.15,
      "operation": "MULTIPLY_BASE"
    }
  ],
  "tacz:m4a1": [
    {
      "attribute": "tacz_attributes:ads_accuracy",
      "value": 0.08,
      "operation": "MULTIPLY_BASE"
    },
    {
      "attribute": "tacz_attributes:reload_speed",
      "value": 0.05,
      "operation": "MULTIPLY_BASE"
    }
  ],
  "tacz:glock_17": [
    {
      "attribute": "tacz_attributes:draw_speed",
      "value": 0.20,
      "operation": "MULTIPLY_BASE"
    }
  ]
}
```

> **Tip:** Gun IDs follow the format `tacz:gun_name`. Match the gun ID exactly as defined in the TACZ data pack.

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

## NBT Data Structure (For Developers)

Attribute data is stored in the gun item's NBT with the following structure.

```
ItemStack NBT → TaczAddon: {
  Modifiers: [                    // Random attributes
    {Attr: "tacz_attributes:gun_damage", Val: 0.15, Op: 1},
    {Attr: "tacz_attributes:recoil", Val: -0.10, Op: 1}
  ],
  FixedModifiers: [               // Fixed attributes
    {Attr: "tacz_attributes:reload_speed", Val: 0.05, Op: 1}
  ],
  Score: 42,                      // Rarity score
  Rarity: 2,                      // 0=COMMON, 1=UNCOMMON, 2=RARE, 3=EPIC
  Sealed: 1,                      // Regeneration prevention flag (boolean)
  RerollCount: 3                  // Number of rerolls performed
}
```

### Op Values

| Op | Name | Description |
|----|------|-------------|
| 0 | ADDITION | Additive |
| 1 | MULTIPLY_BASE | Multiplied against base value |
| 2 | MULTIPLY_TOTAL | Multiplied against total value |

### Rarity Preset (TaczPreset)

You can pre-specify rarity for guns via `/give` commands or loot tables. This is a separate NBT tag independent of `TaczAddon`. It is automatically removed after attribute generation.

```
ItemStack NBT → TaczPreset: {
  MinRarity: 2,                   // Minimum rarity guarantee (0-3, optional)
  TargetRarity: 3                 // Exact rarity (0-3, optional, takes precedence over MinRarity)
}
```

- **MinRarity**: Guarantees the specified rarity or higher. Up to 50 regeneration attempts are made
- **TargetRarity**: Forces the exact specified rarity. Takes precedence over MinRarity
- If both are omitted, normal generation behavior applies

**Examples:**
```
/give @p tacz:modern_kinetic_gun{GunId:"tacz:ak47",TaczPreset:{MinRarity:2}}
/give @p tacz:modern_kinetic_gun{GunId:"tacz:ak47",TaczPreset:{TargetRarity:3}}
```

---

## Attribute Pool Configuration (attribute_pool.json)

File: `config/tacz_attributes_addon/attribute_pool.json`

A JSON file with default attribute pool entries is auto-generated on first launch.
You can freely customize each attribute's appearance rate, value range, rarity score, and more.

### Format

```json
{
  "attributes": [
    {
      "attributeId": "tacz_attributes:gun_damage",
      "minValue": -0.20,
      "maxValue": 0.30,
      "operation": "MULTIPLY_BASE",
      "weight": 20,
      "rarityTier": 1,
      "applicableGunTypes": [],
      "buffThreshold": 0.0,
      "scoreWeight": 100,
      "linkedAttribute": "tacz_attributes:some_other_attribute"
    }
  ]
}
```

### Field Descriptions

| Field | Description | Example |
|-------|-------------|---------|
| `attributeId` | Attribute ID from TACZ Attributes | `"tacz_attributes:gun_damage"` |
| `minValue` | Minimum randomly generated value | `-0.20` (= -20%) |
| `maxValue` | Maximum randomly generated value | `0.30` (= +30%) |
| `operation` | Operator (`MULTIPLY_BASE`, `ADDITION`, `MULTIPLY_TOTAL`) | `"MULTIPLY_BASE"` |
| `weight` | Selection frequency (higher = more common) | `20` |
| `rarityTier` | Rarity tier (1=common, 2=uncommon, 3=rare, 4=very rare) | `1` |
| `applicableGunTypes` | Target gun types (empty = all). `pistol`, `sniper`, `rifle`, `shotgun`, `smg`, `rpg`, `mg` | `["rifle", "smg"]` |
| `buffThreshold` | Values at or above this are considered buffs (usually 0.0) | `0.0` |
| `scoreWeight` | Contribution to rarity score (negative = inverted attributes like recoil) | `100` or `-90` |
| `linkedAttribute` | (Optional) Attribute ID of a partner that must be selected together. When set, selecting this attribute automatically adds the partner. | `"tacz_attributes:ammo_recovery_amount"` |
| `weaponBlacklist` | (Optional) List of weapon IDs to always exclude from receiving this attribute. | `["tacz:rpg7"]` |
| `weaponWhitelist` | (Optional) List of weapon IDs to additionally allow, regardless of `applicableGunTypes`. | `["tacz:desert_eagle"]` |

### Per-Weapon Filtering (weaponBlacklist / weaponWhitelist)

While `applicableGunTypes` filters by weapon **type** (pistol, rifle, etc.), `weaponBlacklist` and `weaponWhitelist` provide fine-grained control by individual weapon **ID** (e.g., tacz:ak47).

**Filter priority:**
1. `weaponBlacklist` contains the weapon → **always excluded** (highest priority)
2. `weaponWhitelist` is non-empty and contains the weapon → **always allowed** (regardless of `applicableGunTypes`)
3. `applicableGunTypes` → standard gun type check as before

```json
{
  "attributeId": "tacz_attributes:headshot_multiplier",
  "minValue": -0.15,
  "maxValue": 0.50,
  "applicableGunTypes": ["sniper", "rifle"],
  "weaponBlacklist": ["tacz:rpg7"],
  "weaponWhitelist": ["tacz:desert_eagle"]
}
```

In this example:
- `tacz:rpg7` → excluded by blacklist
- `tacz:desert_eagle` → allowed by whitelist (despite being a pistol)
- Any sniper or rifle → allowed by `applicableGunTypes`
- Other types (shotgun, smg, etc.) → not allowed

> **Note:** Both fields are optional. When omitted, no per-weapon filtering is applied (default behavior).
> Blacklist/whitelist filtering is applied even in `FULL_RANDOM` mode.

### Linked Attributes (Paired Generation)

The `linkedAttribute` field enables paired attribute generation. For example, if `ammo_recovery_chance` is selected, `ammo_recovery_amount` is automatically added as well.

Default linked pairs:

| Attribute | Linked To | Description |
|-----------|-----------|-------------|
| `ammo_recovery_chance` | `ammo_recovery_amount` | Recovery chance → amount |
| `ammo_recovery_amount` | `ammo_recovery_chance` | Recovery amount → chance |
| `ammo_recovery_percent` | `ammo_recovery_chance` | Recovery percent → chance |
| `bonus_ammo_chance` | `bonus_ammo_amount` | Bonus ammo chance → amount |
| `bonus_ammo_amount` | `bonus_ammo_chance` | Bonus ammo amount → chance |

### Customization Examples

Add a new attribute:
```json
{
  "attributeId": "tacz_attributes:my_custom_attr",
  "minValue": -0.10,
  "maxValue": 0.20,
  "operation": "MULTIPLY_BASE",
  "weight": 10,
  "rarityTier": 2,
  "applicableGunTypes": ["rifle", "sniper"],
  "buffThreshold": 0.0,
  "scoreWeight": 80
}
```

Custom linked attributes:
```json
{
  "attributeId": "tacz_attributes:custom_chance",
  "minValue": 0.0,
  "maxValue": 0.20,
  "operation": "ADDITION",
  "weight": 5,
  "rarityTier": 3,
  "applicableGunTypes": [],
  "buffThreshold": 0.0,
  "scoreWeight": 150,
  "linkedAttribute": "tacz_attributes:custom_amount"
}
```

> **Note:** `attributeId` must match an attribute ID registered in the TACZ Attributes mod.
> After editing the file, reload in-game with `/taczaddon reload`.

### Attribute Groups (Mutex Control)

The `attributeGroups` field lets you group similar attributes and limit how many from the same group can appear on a single gun. This prevents stacking of similar attribute types (e.g., multiple damage attributes at once).

```json
{
  "attributes": [ ... ],
  "attributeGroups": [
    {
      "name": "damage",
      "maxFromGroup": 1,
      "attributes": [
        "tacz_attributes:gun_damage",
        "tacz_attributes:headshot_multiplier",
        "tacz_attributes:ads_damage",
        "tacz_attributes:hip_fire_damage"
      ]
    },
    {
      "name": "recoil",
      "maxFromGroup": 2,
      "attributes": [
        "tacz_attributes:recoil",
        "tacz_attributes:vertical_recoil",
        "tacz_attributes:horizontal_recoil"
      ]
    }
  ]
}
```

| Field | Description |
|-------|-------------|
| `name` | Group identifier |
| `maxFromGroup` | Maximum number of attributes from this group that can appear on a single gun |
| `attributes` | List of attribute IDs in this group |

In the example above:
- **damage group**: Only 1 of the 4 damage-related attributes can appear at once
- **recoil group**: Up to 2 of the 3 recoil attributes can appear together

> **Note:** An attribute can belong to multiple groups; the most restrictive group wins. Omitting `attributeGroups` or using an empty array means no restrictions (backwards compatible). Linked attributes (paired generation) are not affected by group limits.

---

## Per-Gun Attribute Overrides (gun_attribute_overrides.json)

File: `config/tacz_attributes_addon/gun_attribute_overrides.json`

An empty JSON file is generated on first launch. You can override random attribute generation rules per gun ID. This is ideal for looter-shooter modpacks (like The Division 2) where each gun needs distinct attribute builds.

### Format

```json
{
  "gun_id": {
    "minAttributes": min_count,
    "maxAttributes": max_count,
    "minAttributesPos": min_positive_count,
    "maxAttributesPos": max_positive_count,
    "minAttributesNeg": min_negative_count,
    "maxAttributesNeg": max_negative_count,
    "maxEnhancement": max_enhancement_types,
    "attributes": [
      {"attribute": "attribute_id", "minValue": min_val, "maxValue": max_val}
    ]
  }
}
```

### Field Descriptions

| Field | Required | Description |
|-------|----------|-------------|
| `minAttributes` | Optional | Minimum number of random attributes. Uses global config value if omitted. |
| `maxAttributes` | Optional | Maximum number of random attributes. Uses global config value if omitted. |
| `minAttributesPos` | Optional | Minimum number of positive (buff) attributes. Enables split mode. |
| `maxAttributesPos` | Optional | Maximum number of positive (buff) attributes. Enables split mode. |
| `minAttributesNeg` | Optional | Minimum number of negative (debuff) attributes. Enables split mode. |
| `maxAttributesNeg` | Optional | Maximum number of negative (debuff) attributes. Enables split mode. |
| `maxEnhancement` | Optional | Maximum number of distinct enhancement attribute types for this gun. When reached, Enhancement Station only shows already-enhanced attributes. 0 = unlimited. Uses global `maxTypes` if omitted. |
| `attributes` | Optional | Whitelist of allowed attributes. When specified, ONLY listed attributes can appear on this gun. Uses normal pool filtering if omitted. |

Each entry in `attributes` (all fields except `attribute` are optional — omitted fields fall back to `attribute_pool.json`):

| Field | Type | Description |
|-------|------|-------------|
| `attribute` | string | **Required.** Attribute ID (with `tacz_attributes:` prefix) |
| `minValue` | double | Custom minimum value for this gun |
| `maxValue` | double | Custom maximum value for this gun |
| `minValuePos` | double | Minimum value for positive (buff) rolls. For split mode. |
| `maxValuePos` | double | Maximum value for positive (buff) rolls. For split mode. |
| `minValueNeg` | double | Minimum value for negative (debuff) rolls. For split mode. |
| `maxValueNeg` | double | Maximum value for negative (debuff) rolls. For split mode. |
| `weight` | int | Selection frequency for this gun (higher = more common) |
| `rarityTier` | int | Rarity tier for this gun (affects weighting in RARITY_ADAPTIVE/BALANCED modes) |
| `scoreWeight` | double | Rarity score contribution for this gun |
| `operation` | string | Operation for this gun (`MULTIPLY_BASE`, `ADDITION`, `MULTIPLY_TOTAL`) |

### Example Configuration

```json
{
  "tacz:hk416d": {
    "minAttributes": 1,
    "maxAttributes": 3,
    "attributes": [
      {"attribute": "tacz_attributes:reload_speed", "minValue": -0.20, "maxValue": 0.20, "weight": 30},
      {"attribute": "tacz_attributes:gun_damage", "minValue": -0.10, "maxValue": 0.15, "weight": 50, "rarityTier": 2},
      {"attribute": "tacz_attributes:recoil", "minValue": -0.30, "maxValue": 0.10, "scoreWeight": -120}
    ]
  },
  "tacz:rpg7": {
    "minAttributes": 0,
    "maxAttributes": 1
  }
}
```

In this example:
- **HK416D**: 1–3 random attributes, restricted to reload_speed, gun_damage, and recoil with custom value ranges and per-gun weight/rarityTier/scoreWeight overrides.
- **RPG-7**: 0–1 random attributes, attribute types follow normal pool filtering.

> **Tip:** Omit `attributes` to control only the attribute count.
> Guns without an override entry use global settings as before.
> After editing, reload in-game with `/taczaddon reload`.

### Selection Probability

You can specify `weight` and `rarityTier` per attribute entry to customize selection probability **for this gun only**. If omitted, the values from `attribute_pool.json` are used.

For example, if `attribute_pool.json` defines `gun_damage` with weight=20 and `reload_speed` with weight=15, and you override `gun_damage`'s weight to 50 in `gun_attribute_overrides.json`, the selection probabilities for this gun become 50/(50+15)=77% and 15/(50+15)=23%.

Overriding `scoreWeight` applies a custom rarity score contribution for this gun's scoring calculation.

### Positive/Negative Attribute Split Mode

Use `minAttributesPos`/`maxAttributesPos` and `minAttributesNeg`/`maxAttributesNeg` to independently control the number of positive (buff) and negative (debuff) attributes. This guarantees exact compositions like "3 buffs + 1 debuff".

```json
{
  "tacz:hk416d": {
    "minAttributesPos": 2,
    "maxAttributesPos": 3,
    "minAttributesNeg": 1,
    "maxAttributesNeg": 1,
    "attributes": [
      {
        "attribute": "tacz_attributes:reload_speed",
        "minValuePos": 0.10, "maxValuePos": 0.30,
        "minValueNeg": -0.30, "maxValueNeg": -0.10
      },
      {
        "attribute": "tacz_attributes:gun_damage",
        "minValuePos": 0.05, "maxValuePos": 0.15,
        "minValueNeg": -0.20, "maxValueNeg": -0.05
      }
    ]
  }
}
```

In this example:
- **HK416D**: Guarantees 2–3 positive attributes and exactly 1 negative attribute (3–4 total).
- When an attribute is selected as positive, values are generated within `minValuePos`–`maxValuePos`.
- When an attribute is selected as negative, values are generated within `minValueNeg`–`maxValueNeg`.
- `minValuePos`/`maxValuePos`/`minValueNeg`/`maxValueNeg` are all optional. If omitted, the attribute's full value range is split at `buffThreshold` (usually 0.0).

> **Note:** Split mode activates automatically when any of `minAttributesPos`/`maxAttributesPos`/`minAttributesNeg`/`maxAttributesNeg` is set. When combined with `minAttributes`/`maxAttributes`, the total count acts as an upper cap. Works with all generation modes (FULL_RANDOM, ADAPTIVE, RARITY_ADAPTIVE, BALANCED).

### Per-Gun Attribute Groups (Override)

Add `attributeGroups` to a gun's override to customize attribute group restrictions per gun. Same-name groups override the global definition; new names are added on top.

```json
{
  "tacz:ak47": {
    "minAttributes": 3,
    "maxAttributes": 5,
    "attributeGroups": [
      {
        "name": "damage",
        "maxFromGroup": 2
      },
      {
        "name": "speed",
        "maxFromGroup": 1,
        "attributes": [
          "tacz_attributes:reload_speed",
          "tacz_attributes:draw_speed",
          "tacz_attributes:ads_speed"
        ]
      }
    ]
  }
}
```

In this example (AK47):
- **damage group**: Global config has `maxFromGroup: 1`, but AK47 relaxes it to `2`. The attribute list is inherited from the global group when `attributes` is omitted.
- **speed group**: A new AK47-specific group. Only 1 speed attribute can appear.

> **Note:** Omitting the `attributes` field inherits the attribute list from the same-name global group. New groups that only exist per-gun require `attributes` to be specified.

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

## Apotheosis Gun-Specific Gems

When Apotheosis integration is enabled, the following gun-specific gems are available.

| Gem | Attribute | Type |
|-----|-----------|------|
| Marksman Gem | gun_damage | Single attribute |
| Stabilizer Gem | recoil (reduction) | Single attribute |
| Quickloader Gem | reload_speed | Single attribute |
| Sharpshooter Gem | headshot_multiplier | Single attribute |
| Extended Mag Gem | magazine_capacity | Single attribute |
| Rapid Fire Gem | rpm_multiplier | Single attribute |
| Tactical Gem | ads_speed + ads_accuracy | Multi-attribute |
| Conservation Gem | ammo_save_chance | Single attribute |
| Ammo Recovery Gem | ammo_recovery_chance + ammo_recovery_amount | Multi-attribute |
| Bonus Ammo Gem | bonus_ammo_chance + bonus_ammo_amount | Multi-attribute |
| Knockback Gem | knockback_base | Single attribute |

## Apotheosis Gun-Specific Affixes

| Affix | Attribute |
|-------|-----------|
| Deadly | gun_damage |
| Steadfast | recoil (reduction) |
| Swift | reload_speed |
| Precise | headshot_multiplier |
| Capacious | magazine_capacity |
| Rapid | rpm_multiplier |
| Focused | ads_speed |
| Economical | ammo_save_chance |
| Accurate | ads_accuracy |
| Agile | draw_speed |
| Forceful | knockback_base |
