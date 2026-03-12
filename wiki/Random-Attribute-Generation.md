[Home](Home) > Random Attribute Generation

**English** | [日本語](Random-Attribute-Generation-ja) | [中文](Random-Attribute-Generation-cn) | [한국어](Random-Attribute-Generation-kr)

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

[Previous: Feature Toggles](Feature-Toggles) | [Next: Rarity Scoring](Rarity-Scoring)
