[Home](Home) > 命令

[English](Commands) | [日本語](Commands-ja) | **中文** | [한국어](Commands-kr)

---

## 命令

需要 OP 权限（等级 2）。

### `/taczaddon clear`
从手持枪械中删除所有附加组件属性（随机、固定、评分、稀有度）。

### `/taczaddon clear random`
仅删除随机属性，固定属性保留。

### `/taczaddon clear fixed`
仅删除固定属性，随机属性保留。

### `/taczaddon clear enhanced`
仅删除强化属性。随机和固定属性保留。

### `/taczaddon add <attribute> <value> [operation]`
手动向持有枪械的强化属性中添加属性。如果同一属性已存在于强化属性中，值将合并（相加）。

- `attribute`：完整属性 ID（例如 `tacz_attributes:gun_damage`）。支持 Tab 自动补全
- `value`：数值（例如 `0.15`、`-0.10`）
- `operation`：可选。默认：`MULTIPLY_BASE`。选项：`ADDITION`、`MULTIPLY_BASE`、`MULTIPLY_TOTAL`

> **提示：** 适用于 KubeJS 集成。模组包开发者可以使用此命令创建为枪械添加特定属性的自定义物品。

### `/taczaddon reroll`
重新生成手持枪械的随机属性，忽略重掷次数限制。

### `/taczaddon reload`
重新加载 `attribute_pool.json`、`weapon_attributes.json` 和 `gun_attribute_overrides.json`。
无需重启游戏即可使配置更改生效。

### `/taczaddon info`
显示手持枪械上附加的详细组件数据。启用 Apotheosis 联动时，还会显示插槽/宝石信息。

### `/taczaddon config get <key>`
获取配置值，按 Tab 键可显示可用的配置键。

### `/taczaddon config set <key> <value>`
临时修改配置值，服务器重启后将重置。

**可配置的键：**
- `enableRandomOnObtain`、`enableWeaponTypeAttributes`、`enableAttributeStation`
- `enableApotheosis`、`enableRarityScoring`、`showEmptySlots`
- `randomMode`（FULL_RANDOM / ADAPTIVE / RARITY_ADAPTIVE / BALANCED）
- `fixedAttributeMode`（FIXED_ONLY / RANDOM_ONLY / BOTH_STACKING / FIXED_INFLUENCES_RANDOM）
- `minAttributes`、`maxAttributes`
- `valueDistribution`（LINEAR / EXPONENTIAL / QUADRATIC）
- `distributionExponent`、`raritySpreadFactor`、`buffDebuffRatio`
- `uncommonThreshold`、`rareThreshold`、`epicThreshold`
- `processingTime`、`consumeItem`、`consumeItemId`、`consumeCount`
- `allowReroll`、`maxRerolls`
- `gunBaseSockets`、`socketsScaleWithRarity`
- `commonSockets`、`uncommonSockets`、`rareSockets`、`epicSockets`
- `enhancementMaxTypes`、`enhancementExistingOnly`

---

## 固定属性的重掷独立性

固定属性（在 weapon_attributes.json 中定义）在重掷时不会重新生成。

- 固定属性仅在首次获得时应用
- 重掷仅重新生成随机属性
- 固定属性保持不变

**示例：** 假设 AK-47 的固定配置为 `gun_damage: 0.95`：
1. 首次获得时：固定 `gun_damage: 0.95` + 随机属性同时附加
2. 重掷后：固定 `gun_damage: 0.95` 保留，仅随机属性发生变化
3. 即使随机生成了 `gun_damage: 1.2`，固定的 `0.95` 也不会消失，两者叠加生效

---

[上一页: 枪械属性覆盖](Gun-Attribute-Overrides-cn) | [下一页: Apotheosis 联动](Apotheosis-cn)
