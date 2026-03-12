[Home](Home) > Rarity Scoring

**English** | [日本語](Rarity-Scoring-ja) | [中文](Rarity-Scoring-cn) | [한국어](Rarity-Scoring-kr)

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

| | |
|:---|---:|
| [Previous: Random Attribute Generation](Random-Attribute-Generation) | [Next: Attribute Station](Attribute-Station) |
