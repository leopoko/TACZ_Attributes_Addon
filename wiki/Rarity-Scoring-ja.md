[Home](Home) > レアリティスコア閾値

[English](Rarity-Scoring) | **日本語** | [中文](Rarity-Scoring-cn) | [한국어](Rarity-Scoring-kr)

---

## [rarity] レアリティスコア閾値

属性のスコア合計値に基づいてレアリティを決定します。

### スコア計算方法

```
スコア = Σ (属性値 × scoreWeight)
```

- **通常属性**（ダメージ等、scoreWeight=+100）: バフ値が大きいほど高スコア
- **反転属性**（リコイル等、scoreWeight=-90）: リコイル減少（バフ）で高スコア

### `uncommonThreshold` (デフォルト: `100`)
スコアがこの値以上でUNCOMMON（黄色）。

### `rareThreshold` (デフォルト: `300`)
スコアがこの値以上でRARE（水色）。

### `epicThreshold` (デフォルト: `600`)
スコアがこの値以上でEPIC（紫）。

| スコア | レアリティ | アイテム名の色 |
|--------|-----------|----------------|
| 0〜99 | COMMON | 白 |
| 100〜299 | UNCOMMON | 黄色 |
| 300〜599 | RARE | 水色 |
| 600以上 | EPIC | 紫 |

> **調整のヒント:** 閾値を下げるとEPICが出やすくなります。
> 属性の数や値範囲と合わせて調整してください。
> 例: maxAttributes=2 なら閾値を下げる、maxAttributes=8 なら閾値を上げる

---

[前へ: ランダム属性生成](Random-Attribute-Generation-ja) | [次へ: Attribute Station](Attribute-Station-ja)
