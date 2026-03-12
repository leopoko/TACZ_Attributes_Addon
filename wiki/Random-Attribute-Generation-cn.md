[Home](Home) > 随机属性生成

[English](Random-Attribute-Generation) | [日本語](Random-Attribute-Generation-ja) | **中文** | [한국어](Random-Attribute-Generation-kr)

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

[上一页: 功能开关](Feature-Toggles-cn) | [下一页: 稀有度评分阈值](Rarity-Scoring-cn)
