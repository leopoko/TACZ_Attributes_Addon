[Home](Home) > Apotheosis

**English** | [日本語](Apotheosis-ja) | [中文](Apotheosis-cn) | [한국어](Apotheosis-kr)

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

---

| | |
|:---|---:|
| [Previous: Commands](Commands) | [Next: NBT Structure](NBT-Structure) |
