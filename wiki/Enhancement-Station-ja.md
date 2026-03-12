[Home](Home) > Enhancement Station

[English](Enhancement-Station) | **日本語** | [中文](Enhancement-Station-cn) | [한국어](Enhancement-Station-kr)

---

## [enhancement] Enhancement Station ブロック

### `maxTypes` (デフォルト: `0`, 範囲: 0〜100)
銃ごとの強化属性タイプ数の上限。異なる強化属性の種類がこの上限に達すると、既に強化済みの属性のみが選択肢として表示されます（値は増加するが新しいタイプは追加されない）。

- `0`: 無制限（タイプ制限なし）
- `>0`: 異なるタイプ数がこの値に達すると、選択肢は既存の強化属性のみに制限される

> **注意:** これは `maxEnhancements`（強化適用回数の上限）とは独立した設定です。`maxTypes` は属性の種類を制御し、合計回数は制御しません。

### `existingOnly` (デフォルト: `false`)
有効にすると、Enhancement Stationの選択肢が銃に既に存在する属性のみに制限されます（ランダム+固定+強化属性）。

- `true`: 銃に既にある属性のみが選択肢に表示される
- `false`: プールの全対象属性が選択肢に表示される

> **注意:** `gun_attribute_overrides.json` の銃別 `maxEnhancement` オーバーライドでも、タイプ上限に達した時にこの制限が自動的に適用されます。

---

[前へ: Attribute Station](Attribute-Station-ja) | [次へ: 銃モデル別固定属性](Weapon-Attributes-ja)
