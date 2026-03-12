[Home](Home) > Gun Attribute Overrides

**English** | [日本語](Gun-Attribute-Overrides-ja) | [中文](Gun-Attribute-Overrides-cn) | [한국어](Gun-Attribute-Overrides-kr)

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

| | |
|:---|---:|
| [Previous: Attribute Pool](Attribute-Pool) | [Next: Commands](Commands) |
