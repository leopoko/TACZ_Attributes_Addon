[Home](Home) > Weapon Attributes

**English** | [日本語](Weapon-Attributes-ja) | [中文](Weapon-Attributes-cn) | [한국어](Weapon-Attributes-kr)

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

[< Previous: Enhancement Station](Enhancement-Station) | [Next >: Attribute Pool](Attribute-Pool)
