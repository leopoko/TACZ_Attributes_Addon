[Home](Home) > 属性工作台

[English](Attribute-Station) | [日本語](Attribute-Station-ja) | **中文** | [한국어](Attribute-Station-kr)

---

## [station] 属性工作台方块

### `processingTime`（默认：`200`，范围：1～72000）
处理所需时间（以 tick 为单位）。20 tick = 1 秒。

- `200`：10 秒（**默认**）
- `100`：5 秒（较快）
- `1200`：1 分钟（较慢）
- `72000`：1 小时（最大值）

### `consumeItem`（默认：`false`）
处理时是否消耗物品。

- `false`：不消耗物品，只需将枪放入即可处理（**默认**）
- `true`：消耗指定物品后进行处理

### `consumeItemId`（默认：`"minecraft:diamond"`）
消耗物品的 ID。仅在 `consumeItem` 为 `true` 时有效。

**配置示例：**
```toml
consumeItemId = "minecraft:diamond"          # 钻石
consumeItemId = "minecraft:netherite_ingot"  # 下界合金锭
consumeItemId = "minecraft:emerald"          # 绿宝石
consumeItemId = "tacz:gunsmith_table"        # TACZ MOD 的物品
```

### `consumeCount`（默认：`1`，范围：1～64）
每次处理消耗的物品数量。

### `allowReroll`（默认：`true`）
是否允许对已有属性的枪进行重掷（重新生成属性）。

- `true`：可以无限次重掷（上限由 `maxRerolls` 控制）
- `false`：已有属性的枪无法再次处理

### `maxRerolls`（默认：`0`，范围：0～1000）
单把枪允许重掷次数的上限。

- `0`：**无限制**（可以无限次重掷）
- `3`：最多可重掷 3 次
- `10`：最多可重掷 10 次

> 重掷次数会显示在物品提示信息中。
> 达到上限的枪无法在属性工作台中继续处理。

---

## 漏斗支持

属性工作台支持漏斗连接。

| 漏斗方向 | 访问的槽位 |
|----------|-----------|
| 从上方 | 枪槽（0）、材料槽（1） |
| 从侧面 | 枪槽（0）、材料槽（1） |
| 从下方 | 输出槽（2） |

### 自动化生产线构建示例

```
[漏斗（上方）] → 投入枪械
[属性工作台] ← 自动处理
[漏斗（下方）] → 回收成品
```

启用物品消耗时，可以通过侧面的漏斗供给材料。

---

| | |
|:---|---:|
| [上一页: 稀有度评分](Rarity-Scoring-cn) | [下一页: Enhancement Station](Enhancement-Station-cn) |
