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

### 素材配置（station_materials.json）

文件：`config/tacz_attributes_addon/station_materials.json`

当 `consumeItem = true` 时，此 JSON 文件可以注册多种素材类型。每种素材可以设置稀有度约束。首次启动时会自动生成默认文件。

```json
[
  {
    "item": "minecraft:diamond",
    "count": 1
  },
  {
    "item": "minecraft:emerald",
    "count": 2,
    "minRarity": 2
  },
  {
    "item": "minecraft:nether_star",
    "count": 1,
    "targetRarity": 3
  },
  {
    "item": "minecraft:amethyst_shard",
    "count": 4,
    "maxRarity": 1
  }
]
```

| 字段 | 说明 |
|------|------|
| `item` | 物品 ID（必填） |
| `count` | 消耗数量（默认：1） |
| `targetRarity` | 确定稀有度（0-3，最高优先级） |
| `minRarity` | 最低稀有度保证（0-3） |
| `maxRarity` | 最高稀有度限制（0-3） |

所有稀有度约束字段均为可选。省略时不设约束（正常随机）。可通过 `/taczaddon reload` 热重载。

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

[< 上一页: 稀有度评分](Rarity-Scoring-cn) | [下一页 >: Enhancement Station](Enhancement-Station-cn)
