[Home](Home) > Enhancement Station

[English](Enhancement-Station) | [日本語](Enhancement-Station-ja) | **中文** | [한국어](Enhancement-Station-kr)

---

## [enhancement] Enhancement Station 方块

### `maxTypes`（默认：`0`，范围：0～100）
每把枪的强化属性类型最大数量。当不同的强化属性类型数达到此上限时，仅显示已强化的属性作为选项（数值增加但不添加新类型）。

- `0`：无限制（无类型限制）
- `>0`：当不同类型数达到此值时，选项限制为已有的强化属性

> **注意：** 这与 `maxEnhancements`（强化应用总次数上限）是独立的设置。`maxTypes` 控制属性种类，而非总次数。

### `existingOnly`（默认：`false`）
启用后，Enhancement Station 的选项仅限于枪上已有的属性（随机 + 固定 + 强化属性）。

- `true`：仅显示枪上已有的属性作为选项
- `false`：属性池中所有适用属性都可作为选项

> **注意：** `gun_attribute_overrides.json` 中每枪的 `maxEnhancement` 覆盖也会在类型上限达到时自动触发此限制。

---

[< 上一页: 属性工作台](Attribute-Station-cn) | [下一页 >: 固定属性](Weapon-Attributes-cn)
