[Home](Home) > Attribute Pool

**English** | [日本語](Attribute-Pool-ja) | [中文](Attribute-Pool-cn) | [한국어](Attribute-Pool-kr)

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

| | |
|:---|---:|
| [Previous: Weapon Attributes](Weapon-Attributes) | [Next: Gun Attribute Overrides](Gun-Attribute-Overrides) |
