[Home](Home) > NBT 数据结构

[English](NBT-Structure) | [日本語](NBT-Structure-ja) | **中文** | [한국어](NBT-Structure-kr)

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

| | |
|:---|---:|
| [上一页: Apotheosis](Apotheosis-cn) | |
