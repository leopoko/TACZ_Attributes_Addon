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

### レアリティプリセット（TaczPreset）

`/give`コマンドやルートテーブルで銃にレアリティを事前指定できます。`TaczAddon`とは別の独立したNBTタグです。属性生成完了後に自動的に除去されます。

```
ItemStack NBT → TaczPreset: {
  MinRarity: 2,                   // 最低レアリティ保証（0-3, 任意）
  TargetRarity: 3                 // 確定レアリティ（0-3, 任意、MinRarityより優先）
}
```

- **MinRarity**: 指定レアリティ以上を保証。最大50回の再生成を試行します
- **TargetRarity**: 指定レアリティを確定。MinRarityより優先されます
- 両方省略した場合は通常の生成と同じ動作になります

**使用例:**
```
/give @p tacz:modern_kinetic_gun{GunId:"tacz:ak47",TaczPreset:{MinRarity:2}}
/give @p tacz:modern_kinetic_gun{GunId:"tacz:ak47",TaczPreset:{TargetRarity:3}}
```

---

[< 前へ: Apotheosis](Apotheosis-ja) |
