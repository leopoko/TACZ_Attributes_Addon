[Home](Home) > NBTデータ構造

[English](NBT-Structure) | **日本語** | [中文](NBT-Structure-cn) | [한국어](NBT-Structure-kr)

---

## NBTデータ構造（開発者向け）

銃アイテムのNBTに以下の構造で属性データが格納されます。

```
ItemStack NBT → TaczAddon: {
  Modifiers: [                    // ランダム属性
    {Attr: "tacz_attributes:gun_damage", Val: 0.15, Op: 1},
    {Attr: "tacz_attributes:recoil", Val: -0.10, Op: 1}
  ],
  FixedModifiers: [               // 固定属性
    {Attr: "tacz_attributes:reload_speed", Val: 0.05, Op: 1}
  ],
  Score: 42,                      // レアリティスコア
  Rarity: 2,                     // 0=COMMON, 1=UNCOMMON, 2=RARE, 3=EPIC
  Sealed: 1,                     // 再生成防止フラグ（boolean）
  RerollCount: 3                  // リロール回数
}
```

### Opの値

| Op | 名前 | 説明 |
|----|------|------|
| 0 | ADDITION | 加算 |
| 1 | MULTIPLY_BASE | 基本値乗算 |
| 2 | MULTIPLY_TOTAL | 合計値乗算 |

---

| | |
|:---|---:|
| [前へ: Apotheosis](Apotheosis-ja) | |
