[Home](Home) > 機能トグル

[English](Feature-Toggles) | **日本語** | [中文](Feature-Toggles-cn) | [한국어](Feature-Toggles-kr)

---

## [general] 機能トグル

各機能の有効/無効を個別に設定できます。

### `enableRandomOnObtain` (デフォルト: `true`)
銃をインベントリに入れた時、自動的にランダム属性を付与します。

- `true`: プレイヤーが銃アイテムを取得するとランダム属性が自動付与される
- `false`: 自動付与しない（Attribute Stationでの手動付与のみ）

> **注意:** 無効にしても、既に属性が付いた銃には影響しません。新しく取得する銃にのみ適用されます。

### `enableWeaponTypeAttributes` (デフォルト: `true`)
銃モデルごとの固定属性を有効にします。固定属性は `config/tacz_attributes_addon/weapon_attributes.json` で設定します。

- `true`: weapon_attributes.json の設定に基づき固定属性を適用
- `false`: 固定属性を一切適用しない

### `enableAttributeStation` (デフォルト: `true`)
Attribute Station ブロックの機能を有効にします。

- `true`: ブロックに銃を入れて属性の付与/リロールが可能
- `false`: ブロックは設置可能だが加工処理が行われない

### `enableApotheosis` (デフォルト: `true`)
Apotheosis MOD との連携機能を有効にします。Apotheosisがインストールされている場合、銃にソケットスロットが追加され、銃専用ジェムの挿入が可能になります。

- `true`: Apotheosisが存在する場合、ソケット/ジェム統合を有効化
- `false`: Apotheosisが存在してもソケット/ジェム機能を無効化

### `enableRarityScoring` (デフォルト: `true`)
属性に基づくレアリティスコアリングシステムを有効にします。

- `true`: 付与された属性の値とscoreWeightからスコアを計算し、レアリティ（COMMON/UNCOMMON/RARE/EPIC）を決定。銃アイテムの名前色がレアリティに応じて変化する
- `false`: レアリティ計算をスキップ。全ての銃がCOMMON表示

### `showEmptySlots` (デフォルト: `false`)
ツールチップに空き属性スロットを表示します。銃のランダム属性数が `maxAttributes` より少ない場合に表示されます。

- `true`: 未使用スロットを `[ ] 空き属性スロット` として表示。銃別の `maxAttributes` オーバーライドがあればそれを使用、なければグローバル値
- `false`: 空きスロット表示なし

---

| | |
|:---|---:|
| | [次へ: ランダム属性生成](Random-Attribute-Generation-ja) |
