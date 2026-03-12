[Home](Home) > 枪械属性覆盖

[English](Gun-Attribute-Overrides) | [日本語](Gun-Attribute-Overrides-ja) | **中文** | [한국어](Gun-Attribute-Overrides-kr)

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
    "minAttributesPos": 正属性最小数,
    "maxAttributesPos": 正属性最大数,
    "minAttributesNeg": 负属性最小数,
    "maxAttributesNeg": 负属性最大数,
    "maxEnhancement": 强化类型上限,
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
| `minAttributesPos` | 可选 | 正属性（增益）的最小数量。启用分离模式 |
| `maxAttributesPos` | 可选 | 正属性（增益）的最大数量。启用分离模式 |
| `minAttributesNeg` | 可选 | 负属性（减益）的最小数量。启用分离模式 |
| `maxAttributesNeg` | 可选 | 负属性（减益）的最大数量。启用分离模式 |
| `maxEnhancement` | 可选 | 强化属性类型数的上限。达到上限时 Enhancement Station 仅显示已强化的属性。0=无限制。省略时使用全局 `maxTypes` |
| `attributes` | 可选 | 允许属性的白名单。指定后，仅列出的属性可出现在该枪上。省略时使用常规属性池过滤 |

`attributes` 中的每个条目（除 `attribute` 外所有字段均为可选，省略时使用 `attribute_pool.json` 的值）：

| 字段 | 类型 | 说明 |
|------|------|------|
| `attribute` | string | **必需**。属性 ID（带 `tacz_attributes:` 前缀） |
| `minValue` | double | 该枪的自定义最小值 |
| `maxValue` | double | 该枪的自定义最大值 |
| `minValuePos` | double | 正值（增益）的最小值。用于分离模式 |
| `maxValuePos` | double | 正值（增益）的最大值。用于分离模式 |
| `minValueNeg` | double | 负值（减益）的最小值。用于分离模式 |
| `maxValueNeg` | double | 负值（减益）的最大值。用于分离模式 |
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

### 正负属性独立控制（分离模式）

使用 `minAttributesPos`/`maxAttributesPos` 和 `minAttributesNeg`/`maxAttributesNeg` 可以独立控制正属性（增益）和负属性（减益）的数量。这样可以保证精确的属性组合，如「3个增益 + 1个减益」。

```json
{
  "tacz:hk416d": {
    "minAttributesPos": 2,
    "maxAttributesPos": 3,
    "minAttributesNeg": 1,
    "maxAttributesNeg": 1,
    "attributes": [
      {
        "attribute": "tacz_attributes:reload_speed",
        "minValuePos": 0.10, "maxValuePos": 0.30,
        "minValueNeg": -0.30, "maxValueNeg": -0.10
      },
      {
        "attribute": "tacz_attributes:gun_damage",
        "minValuePos": 0.05, "maxValuePos": 0.15,
        "minValueNeg": -0.20, "maxValueNeg": -0.05
      }
    ]
  }
}
```

以上示例：
- **HK416D**：保证 2～3 个正属性和 1 个负属性（总计 3～4 个）
- 当属性被选为正属性时，在 `minValuePos`～`maxValuePos` 范围内生成数值
- 当属性被选为负属性时，在 `minValueNeg`～`maxValueNeg` 范围内生成数值
- `minValuePos`/`maxValuePos`/`minValueNeg`/`maxValueNeg` 均为可选。省略时使用属性完整值范围按 `buffThreshold`（通常为 0.0）分割

> **注意：** 当设置了 `minAttributesPos`/`maxAttributesPos`/`minAttributesNeg`/`maxAttributesNeg` 中的任意一个时，分离模式自动启用。与 `minAttributes`/`maxAttributes` 同时使用时，总数作为上限。支持所有生成模式（FULL_RANDOM、ADAPTIVE、RARITY_ADAPTIVE、BALANCED）。

### 枪械属性分组（覆盖）

在枪械覆盖配置中添加 `attributeGroups` 可为每把枪自定义属性分组限制。同名分组覆盖全局定义，新名称的分组则额外添加。

```json
{
  "tacz:ak47": {
    "minAttributes": 3,
    "maxAttributes": 5,
    "attributeGroups": [
      {
        "name": "damage",
        "maxFromGroup": 2
      },
      {
        "name": "speed",
        "maxFromGroup": 1,
        "attributes": [
          "tacz_attributes:reload_speed",
          "tacz_attributes:draw_speed",
          "tacz_attributes:ads_speed"
        ]
      }
    ]
  }
}
```

上述示例（AK47）：
- **damage 分组**：全局设置为 `maxFromGroup: 1`，但 AK47 放宽为 `2`。省略 `attributes` 时继承全局分组的属性列表
- **speed 分组**：AK47 专用的新分组，速度类属性最多出现1个

> **注意：** 省略 `attributes` 字段时继承同名全局分组的属性列表。仅存在于枪械配置中的新分组需要指定 `attributes`。

---

[< 上一页: 属性池](Attribute-Pool-cn) | [下一页 >: 命令](Commands-cn)
