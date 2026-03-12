[Home](Home) > コマンド

[English](Commands) | **日本語** | [中文](Commands-cn) | [한국어](Commands-kr)

---

## コマンド

OP権限（レベル2）が必要です。

### `/taczaddon clear`
手持ちの銃から全てのアドオン属性（ランダム・固定・スコア・レアリティ）を削除します。

### `/taczaddon clear random`
ランダム属性のみを削除します。固定属性は残ります。

### `/taczaddon clear fixed`
固定属性のみを削除します。ランダム属性は残ります。

### `/taczaddon clear enhanced`
強化属性のみを削除します。ランダム属性と固定属性は残ります。

### `/taczaddon add <attribute> <value> [operation]`
手持ちの銃の強化属性に属性を手動追加します。同じ属性が既に強化属性に存在する場合、値がマージ（加算）されます。

- `attribute`: 完全な属性ID（例: `tacz_attributes:gun_damage`）。タブ補完対応
- `value`: 数値（例: `0.15`, `-0.10`）
- `operation`: 任意。デフォルト: `MULTIPLY_BASE`。選択肢: `ADDITION`, `MULTIPLY_BASE`, `MULTIPLY_TOTAL`

> **ヒント:** KubeJS連携に便利です。Modpack開発者はこのコマンドを使って、特定の属性を銃に追加するカスタムアイテムを作成できます。

### `/taczaddon reroll`
手持ちの銃のランダム属性を再生成します。リロール回数制限を無視します。

### `/taczaddon reload`
`attribute_pool.json`、`weapon_attributes.json`、`gun_attribute_overrides.json` をリロードします。
ゲームを再起動せずに設定変更を反映できます。

### `/taczaddon info`
手持ちの銃に付与されているアドオンデータの詳細を表示します。Apotheosis連携有効時は、ソケット/ジェム情報も表示されます。

### `/taczaddon config get <key>`
設定値を取得します。タブ補完で設定キーが表示されます。

### `/taczaddon config set <key> <value>`
設定値を一時的に変更します。サーバー再起動でリセットされます。

**設定可能なキー:**
- `enableRandomOnObtain`, `enableWeaponTypeAttributes`, `enableAttributeStation`
- `enableApotheosis`, `enableRarityScoring`, `showEmptySlots`
- `randomMode` (FULL_RANDOM / ADAPTIVE / RARITY_ADAPTIVE / BALANCED)
- `fixedAttributeMode` (FIXED_ONLY / RANDOM_ONLY / BOTH_STACKING / FIXED_INFLUENCES_RANDOM)
- `minAttributes`, `maxAttributes`
- `valueDistribution` (LINEAR / EXPONENTIAL / QUADRATIC)
- `distributionExponent`, `raritySpreadFactor`, `buffDebuffRatio`
- `uncommonThreshold`, `rareThreshold`, `epicThreshold`
- `processingTime`, `consumeItem`, `consumeItemId`, `consumeCount`
- `allowReroll`, `maxRerolls`
- `gunBaseSockets`, `socketsScaleWithRarity`
- `commonSockets`, `uncommonSockets`, `rareSockets`, `epicSockets`
- `enhancementMaxTypes`, `enhancementExistingOnly`

---

## 固定属性のリロール独立性

固定属性（weapon_attributes.json で定義）はリロール時に再生成されません。

- 初回取得時のみ固定属性が適用される
- リロールはランダム属性のみを再生成
- 固定属性はそのまま保持される

**例:** AKに `gun_damage: 0.95` が固定設定されている場合:
1. 初回取得時: 固定 `gun_damage: 0.95` + ランダム属性が付与
2. リロール後: 固定 `gun_damage: 0.95` は維持、ランダム属性のみ変更
3. ランダムで `gun_damage: 1.2` が付いても、固定の `0.95` は消えず両立

---

[< 前へ: 銃別オーバーライド](Gun-Attribute-Overrides-ja) | [次へ >: Apotheosis](Apotheosis-ja)
