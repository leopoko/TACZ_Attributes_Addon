[Home](Home) > Apotheosis 联动

[English](Apotheosis) | [日本語](Apotheosis-ja) | **中文** | [한국어](Apotheosis-kr)

---

## [apotheosis] Apotheosis 联动设置

安装 Apotheosis MOD 后启用的插槽/宝石功能设置。

### `gunBaseSockets`（默认：`2`，范围：0～6）
不与稀有度关联时（`socketsScaleWithRarity = false`）使用的固定插槽数量。

### `socketsScaleWithRarity`（默认：`true`）
插槽数量是否随枪械稀有度变化。

- `true`：插槽数量根据稀有度变化（使用以下各稀有度设置）
- `false`：所有枪械使用 `gunBaseSockets` 指定的固定插槽数量

### `commonSockets`（默认：`1`，范围：0～6）
COMMON 稀有度枪械的插槽数量。

### `uncommonSockets`（默认：`2`，范围：0～6）
UNCOMMON 稀有度枪械的插槽数量。

### `rareSockets`（默认：`3`，范围：0～6）
RARE 稀有度枪械的插槽数量。

### `epicSockets`（默认：`4`，范围：0～6）
EPIC 稀有度枪械的插槽数量。

---

## Apotheosis 枪械专用宝石一览

启用 Apotheosis 联动后，以下枪械专用宝石可用。

| 宝石 | 属性 | 类型 |
|------|------|------|
| 神射手宝石 | gun_damage | 单属性 |
| 稳定宝石 | recoil（降低） | 单属性 |
| 快速装填宝石 | reload_speed | 单属性 |
| 精确射击宝石 | headshot_multiplier | 单属性 |
| 扩容弹匣宝石 | magazine_capacity | 单属性 |
| 速射宝石 | rpm_multiplier | 单属性 |
| 战术宝石 | ads_speed + ads_accuracy | 复合属性 |
| 节约宝石 | ammo_save_chance | 单属性 |
| 弹药回复宝石 | ammo_recovery_chance + ammo_recovery_amount | 复合属性 |
| 额外弹药宝石 | bonus_ammo_chance + bonus_ammo_amount | 复合属性 |
| 击退宝石 | knockback_base | 单属性 |

## Apotheosis 枪械专用词缀一览

| 词缀 | 属性 |
|------|------|
| 杀伤 | gun_damage |
| 稳固 | recoil（降低） |
| 迅捷 | reload_speed |
| 精准 | headshot_multiplier |
| 大容量 | magazine_capacity |
| 速射 | rpm_multiplier |
| 专注 | ads_speed |
| 节约 | ammo_save_chance |
| 精确 | ads_accuracy |
| 敏捷 | draw_speed |
| 强击 | knockback_base |

---

[< 上一页: 命令](Commands-cn) | [下一页 >: NBT结构](NBT-Structure-cn)
