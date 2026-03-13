[Home](Home) > NBT Structure

**English** | [日本語](NBT-Structure-ja) | [中文](NBT-Structure-cn) | [한국어](NBT-Structure-kr)

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

[< Previous: Apotheosis](Apotheosis) |
