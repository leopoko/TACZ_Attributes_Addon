[Home](Home) > 稀有度评分阈值

[English](Rarity-Scoring) | [日本語](Rarity-Scoring-ja) | **中文** | [한국어](Rarity-Scoring-kr)

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

[上一页: 随机属性生成](Random-Attribute-Generation-cn) | [下一页: 属性工作台](Attribute-Station-cn)
