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

---

[< Previous: Apotheosis](Apotheosis) | 
