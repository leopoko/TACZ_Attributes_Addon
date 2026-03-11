# TACZ Attributes Addon - 配置详细指南

配置文件：`config/tacz_attributes_addon-common.toml`（首次启动游戏时自动生成）

---

## [general] 功能开关

可以单独启用或禁用各项功能。

### `enableRandomOnObtain`（默认：`true`）
当枪械进入物品栏时，自动附加随机属性。

- `true`：玩家获得枪械时自动附加随机属性
- `false`：不自动附加（仅可通过属性工作台手动附加）

> **注意：** 禁用此设置不会影响已经附有属性的枪械，仅对新获得的枪械有效。

### `enableWeaponTypeAttributes`（默认：`true`）
启用每把枪型号对应的固定属性。固定属性在 `config/tacz_attributes_addon/weapon_attributes.json` 中配置。

- `true`：根据 weapon_attributes.json 的设置应用固定属性
- `false`：不应用任何固定属性

### `enableAttributeStation`（默认：`true`）
启用属性工作台方块的功能。

- `true`：将枪放入方块中，可以附加或重掷属性
- `false`：方块可以放置，但不会进行任何处理

### `enableApotheosis`（默认：`true`）
启用与 Apotheosis MOD 的联动功能。安装 Apotheosis 后，枪械将获得宝石插槽，可以插入枪械专用宝石。

- `true`：当 Apotheosis 存在时，启用插槽/宝石集成
- `false`：即使 Apotheosis 存在也禁用插槽/宝石功能

### `enableRarityScoring`（默认：`true`）
启用基于属性的稀有度评分系统。

- `true`：根据附加属性的数值和 scoreWeight 计算评分，并决定稀有度（COMMON/UNCOMMON/RARE/EPIC）。枪械物品的名称颜色随稀有度变化。
- `false`：跳过稀有度计算，所有枪械均显示为 COMMON。

---

## [random] 随机属性生成

对随机属性生成算法进行精细调整。

### `randomMode`（默认：`RARITY_ADAPTIVE`）

随机属性的选择算法。

| 模式 | 说明 | 适用场景 |
|------|------|----------|
| `FULL_RANDOM` | 从全部属性池中完全随机选取，不考虑枪械类型。SMG 可能会获得狙击枪专用属性。 | 休闲 / 混沌 |
| `ADAPTIVE` | 按枪械类型和射击模式过滤属性，不匹配的属性不会被选中。 | 注重平衡 |
| `RARITY_ADAPTIVE` | ADAPTIVE + 稀有度权重。稀有度越高的属性越难出现 + 数值偏低（较小的数值更容易出现）。 | **推荐（默认）** |
| `BALANCED` | RARITY_ADAPTIVE + 自动调整增益/减益比率，由 `buffDebuffRatio` 控制。 | 注重公平性 |

### `fixedAttributeMode`（默认：`BOTH_STACKING`）

固定属性（weapon_attributes.json）与随机属性的关系。

| 模式 | 固定属性 | 随机属性 | 说明 |
|------|----------|----------|------|
| `FIXED_ONLY` | 应用 | **不生成** | 仅固定属性，无随机性 |
| `RANDOM_ONLY` | **不应用** | 生成 | 仅随机属性，忽略固定配置 |
| `BOTH_STACKING` | 应用 | 生成 | 两者独立应用（分别存储在不同 NBT 标签中）**推荐** |
| `FIXED_INFLUENCES_RANDOM` | 应用 | 生成（受影响） | 固定属性影响随机生成的权重 |

### `minAttributes`（默认：`1`，范围：0～20）
一把枪附加的随机属性最小数量。设为 0 时，部分枪械可能不会获得任何属性。

### `maxAttributes`（默认：`4`，范围：0～20）
一把枪附加的随机属性最大数量。实际数量在 min～max 之间随机决定。

> **示例：** min=2，max=5 → 每把枪获得 2～5 个随机属性

### `valueDistribution`（默认：`EXPONENTIAL`）

属性数值的分布曲线，控制数值在 min～max 范围内的分布方式。

| 分布 | 特征 | 说明 |
|------|------|------|
| `LINEAR` | 均匀分布 | 所有数值出现概率相同 |
| `EXPONENTIAL` | 偏向较小数值 | 小数值更容易出现，大数值较为罕见。由 `distributionExponent` 控制。**推荐** |
| `QUADRATIC` | 二次曲线 | 比 EXPONENTIAL 更温和地偏向较小数值 |

### `distributionExponent`（默认：`2.0`，范围：1.0～10.0）
EXPONENTIAL 分布的指数。数值越大，结果越偏向最小值。

- `1.0`：等同于 LINEAR（无偏差）
- `2.0`：适度偏向较小数值（**推荐**）
- `5.0`：强烈偏向较小数值（大数值几乎不会出现）
- `10.0`：极端偏向最小数值

### `raritySpreadFactor`（默认：`2.0`，范围：1.0～10.0）
在 RARITY_ADAPTIVE/BALANCED 模式下，控制稀有度等级对属性选择权重的分散程度。

- `1.0`：所有稀有度等级被选中的概率相同
- `2.0`：高稀有度属性更难被选中（**推荐**）
- `5.0`：稀有度第 4 级的属性极为罕见

> **计算公式：** weight = baseWeight / (rarityTier ^ raritySpreadFactor)

### `buffDebuffRatio`（默认：`1.0`，范围：0.1～5.0）
仅限 BALANCED 模式使用。增益与减益的目标比率。

- `0.5`：减益是增益的两倍（硬核向）
- `1.0`：增益与减益数量相同（**推荐**）
- `2.0`：增益是减益的两倍（对玩家有利）

---

## [rarity] 稀有度评分阈值

根据属性总评分决定稀有度。

### 评分计算方式

```
评分 = Σ（属性数值 × scoreWeight）
```

- **普通属性**（伤害等，scoreWeight=+100）：增益数值越大，评分越高
- **反转属性**（后坐力等，scoreWeight=-90）：后坐力减少（增益）→ 评分越高

### `uncommonThreshold`（默认：`100`）
评分达到此值或以上 → UNCOMMON（黄色）。

### `rareThreshold`（默认：`300`）
评分达到此值或以上 → RARE（青色）。

### `epicThreshold`（默认：`600`）
评分达到此值或以上 → EPIC（紫色）。

| 评分 | 稀有度 | 物品名称颜色 |
|------|--------|-------------|
| 0～99 | COMMON | 白色 |
| 100～299 | UNCOMMON | 黄色 |
| 300～599 | RARE | 青色 |
| 600以上 | EPIC | 紫色 |

> **调整提示：** 降低阈值会使 EPIC 更容易出现。
> 请结合属性数量和数值范围进行调整。
> 例如：maxAttributes=2 时降低阈值；maxAttributes=8 时提高阈值。

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

## 枪型号固定属性（weapon_attributes.json）

文件：`config/tacz_attributes_addon/weapon_attributes.json`

首次启动时会生成一个空的 JSON 文件 `{}`，可以为每个枪 ID 配置固定属性。

### 格式

```json
{
  "枪ID": [
    {
      "attribute": "属性ID",
      "value": 数值,
      "operation": "运算符"
    }
  ]
}
```

### 运算符（operation）

| 运算符 | 说明 | 示例 |
|--------|------|------|
| `MULTIPLY_BASE` | 对基础值进行乘算。0.10 = +10% | 大多数属性使用此运算符 |
| `ADDITION` | 加到基础值上 | knockback_base、ammo_recovery_amount 等 |
| `MULTIPLY_TOTAL` | 对最终值进行乘算 | 仅用于特殊情况 |

### 配置示例

```json
{
  "tacz:ak47": [
    {
      "attribute": "tacz_attributes:gun_damage",
      "value": 0.05,
      "operation": "MULTIPLY_BASE"
    },
    {
      "attribute": "tacz_attributes:recoil",
      "value": 0.15,
      "operation": "MULTIPLY_BASE"
    }
  ],
  "tacz:m4a1": [
    {
      "attribute": "tacz_attributes:ads_accuracy",
      "value": 0.08,
      "operation": "MULTIPLY_BASE"
    },
    {
      "attribute": "tacz_attributes:reload_speed",
      "value": 0.05,
      "operation": "MULTIPLY_BASE"
    }
  ],
  "tacz:glock_17": [
    {
      "attribute": "tacz_attributes:draw_speed",
      "value": 0.20,
      "operation": "MULTIPLY_BASE"
    }
  ]
}
```

> **提示：** 枪 ID 格式为 `tacz:枪名`。请确保与 TACZ 数据包中定义的枪 ID 完全一致。

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

## NBT 数据结构（开发者参考）

属性数据以如下结构存储在枪械物品的 NBT 中。

```
ItemStack NBT → TaczAddon: {
  Modifiers: [                    // 随机属性
    {Attr: "tacz_attributes:gun_damage", Val: 0.15, Op: 1},
    {Attr: "tacz_attributes:recoil", Val: -0.10, Op: 1}
  ],
  FixedModifiers: [               // 固定属性
    {Attr: "tacz_attributes:reload_speed", Val: 0.05, Op: 1}
  ],
  Score: 42,                      // 稀有度评分
  Rarity: 2,                      // 0=COMMON, 1=UNCOMMON, 2=RARE, 3=EPIC
  Sealed: 1,                      // 防止重新生成的标志（布尔值）
  RerollCount: 3                  // 重掷次数
}
```

### Op 值

| Op | 名称 | 说明 |
|----|------|------|
| 0 | ADDITION | 加算 |
| 1 | MULTIPLY_BASE | 基础值乘算 |
| 2 | MULTIPLY_TOTAL | 最终值乘算 |

---

## 属性池配置（attribute_pool.json）

文件：`config/tacz_attributes_addon/attribute_pool.json`

首次启动时会自动生成包含默认属性池的 JSON 文件。
可以自由自定义每个属性的出现率、数值范围、稀有度评分等。

### 格式

```json
{
  "attributes": [
    {
      "attributeId": "tacz_attributes:gun_damage",
      "minValue": -0.20,
      "maxValue": 0.30,
      "operation": "MULTIPLY_BASE",
      "weight": 20,
      "rarityTier": 1,
      "applicableGunTypes": [],
      "buffThreshold": 0.0,
      "scoreWeight": 100,
      "linkedAttribute": "tacz_attributes:some_other_attribute"
    }
  ]
}
```

### 各字段说明

| 字段 | 说明 | 示例 |
|------|------|------|
| `attributeId` | TACZ Attributes 的属性 ID | `"tacz_attributes:gun_damage"` |
| `minValue` | 随机生成的最小值 | `-0.20`（= -20%） |
| `maxValue` | 随机生成的最大值 | `0.30`（= +30%） |
| `operation` | 运算符（`MULTIPLY_BASE`、`ADDITION`、`MULTIPLY_TOTAL`） | `"MULTIPLY_BASE"` |
| `weight` | 选择频率（越大越容易出现） | `20` |
| `rarityTier` | 稀有度等级（1=普通，2=较少见，3=稀有，4=极稀有） | `1` |
| `applicableGunTypes` | 适用枪械类型（空=全部）。可选值：`pistol`、`sniper`、`rifle`、`shotgun`、`smg`、`rpg`、`mg` | `["rifle", "smg"]` |
| `buffThreshold` | 该值以上视为增益（通常为 0.0） | `0.0` |
| `scoreWeight` | 对稀有度评分的贡献度（负数 = 后坐力等反转属性） | `100` 或 `-90` |
| `linkedAttribute` | （可选）必须成对选择的伙伴属性 ID。设置后，选中此属性时会自动添加伙伴属性。 | `"tacz_attributes:ammo_recovery_amount"` |

### 关联属性（成对生成）

`linkedAttribute` 字段可设置属性的成对生成。例如，当 `ammo_recovery_chance` 被选中时，`ammo_recovery_amount` 也会自动添加。

默认关联对：

| 属性 | 关联到 | 说明 |
|------|--------|------|
| `ammo_recovery_chance` | `ammo_recovery_amount` | 回复概率 → 回复数量 |
| `ammo_recovery_amount` | `ammo_recovery_chance` | 回复数量 → 回复概率 |
| `ammo_recovery_percent` | `ammo_recovery_chance` | 回复比例 → 回复概率 |
| `bonus_ammo_chance` | `bonus_ammo_amount` | 额外弹药概率 → 数量 |
| `bonus_ammo_amount` | `bonus_ammo_chance` | 额外弹药数量 → 概率 |

### 自定义示例

添加新属性：
```json
{
  "attributeId": "tacz_attributes:my_custom_attr",
  "minValue": -0.10,
  "maxValue": 0.20,
  "operation": "MULTIPLY_BASE",
  "weight": 10,
  "rarityTier": 2,
  "applicableGunTypes": ["rifle", "sniper"],
  "buffThreshold": 0.0,
  "scoreWeight": 80
}
```

自定义关联属性：
```json
{
  "attributeId": "tacz_attributes:custom_chance",
  "minValue": 0.0,
  "maxValue": 0.20,
  "operation": "ADDITION",
  "weight": 5,
  "rarityTier": 3,
  "applicableGunTypes": [],
  "buffThreshold": 0.0,
  "scoreWeight": 150,
  "linkedAttribute": "tacz_attributes:custom_amount"
}
```

> **注意：** `attributeId` 必须与 TACZ Attributes MOD 中注册的属性 ID 完全一致。
> 编辑文件后，可在游戏内使用 `/taczaddon reload` 命令重新加载。

---

## 枪械属性覆盖（gun_attribute_overrides.json）

文件：`config/tacz_attributes_addon/gun_attribute_overrides.json`

首次启动时会生成一个空的 JSON 文件。可为每个枪 ID 单独覆盖随机属性的生成规则。适用于类似 The Division 2 的 Looter-shooter 风格模组包，让每把枪拥有不同的属性构建。

### 格式

```json
{
  "枪ID": {
    "minAttributes": 最小属性数,
    "maxAttributes": 最大属性数,
    "attributes": [
      {"attribute": "属性ID", "minValue": 最小值, "maxValue": 最大值}
    ]
  }
}
```

### 各字段说明

| 字段 | 是否必需 | 说明 |
|------|----------|------|
| `minAttributes` | 可选 | 随机属性的最小数量。省略时使用全局设置 |
| `maxAttributes` | 可选 | 随机属性的最大数量。省略时使用全局设置 |
| `attributes` | 可选 | 允许属性的白名单。指定后，仅列出的属性可出现在该枪上。省略时使用常规属性池过滤 |

`attributes` 中的每个条目（除 `attribute` 外所有字段均为可选，省略时使用 `attribute_pool.json` 的值）：

| 字段 | 类型 | 说明 |
|------|------|------|
| `attribute` | string | **必需**。属性 ID（带 `tacz_attributes:` 前缀） |
| `minValue` | double | 该枪的自定义最小值 |
| `maxValue` | double | 该枪的自定义最大值 |
| `weight` | int | 该枪的选择频率（越大越容易出现） |
| `rarityTier` | int | 该枪的稀有度等级（影响 RARITY_ADAPTIVE/BALANCED 模式的权重） |
| `scoreWeight` | double | 该枪的稀有度评分贡献度 |
| `operation` | string | 该枪的运算符（`MULTIPLY_BASE`、`ADDITION`、`MULTIPLY_TOTAL`） |

### 配置示例

```json
{
  "tacz:hk416d": {
    "minAttributes": 1,
    "maxAttributes": 3,
    "attributes": [
      {"attribute": "tacz_attributes:reload_speed", "minValue": -0.20, "maxValue": 0.20, "weight": 30},
      {"attribute": "tacz_attributes:gun_damage", "minValue": -0.10, "maxValue": 0.15, "weight": 50, "rarityTier": 2},
      {"attribute": "tacz_attributes:recoil", "minValue": -0.30, "maxValue": 0.10, "scoreWeight": -120}
    ]
  },
  "tacz:rpg7": {
    "minAttributes": 0,
    "maxAttributes": 1
  }
}
```

以上示例：
- **HK416D**：1～3 个随机属性，仅限 reload_speed、gun_damage 和 recoil 三种属性，且自定义了数值范围、选择权重、稀有度等级和评分贡献度
- **RPG-7**：0～1 个随机属性，属性类型按常规属性池过滤

> **提示：** 省略 `attributes` 可以仅控制属性数量。
> 未配置的枪继续使用全局设置。
> 编辑文件后，可在游戏内使用 `/taczaddon reload` 命令重新加载。

### 选择概率

在各属性条目中指定 `weight` 和 `rarityTier` 可以**仅为该枪**自定义选择概率。省略时使用 `attribute_pool.json` 的值。

例如，如果 `attribute_pool.json` 中 `gun_damage` 的 weight=20，`reload_speed` 的 weight=15，在 `gun_attribute_overrides.json` 中将 `gun_damage` 的 weight 覆盖为 50，则该枪的选择概率变为 50/(50+15)=77% 和 15/(50+15)=23%。

覆盖 `scoreWeight` 可为该枪的稀有度评分计算应用自定义贡献度。

---

## 命令

需要 OP 权限（等级 2）。

### `/taczaddon clear`
从手持枪械中删除所有附加组件属性（随机、固定、评分、稀有度）。

### `/taczaddon clear random`
仅删除随机属性，固定属性保留。

### `/taczaddon clear fixed`
仅删除固定属性，随机属性保留。

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
- `enableApotheosis`、`enableRarityScoring`
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
