[Home](Home) > Enhancement Station

**English** | [日本語](Enhancement-Station-ja) | [中文](Enhancement-Station-cn) | [한국어](Enhancement-Station-kr)

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

| | |
|:---|---:|
| [Previous: Attribute Station](Attribute-Station) | [Next: Weapon Attributes](Weapon-Attributes) |
