# Changelog

## [1.3]
### 新機能
- 属性グループ（排他制御）機能追加 - 類似属性のグループ化と同時出現数の制限
  - `attribute_pool.json` に `attributeGroups` フィールド追加（グローバル設定）
  - `gun_attribute_overrides.json` に銃別 `attributeGroups` オーバーライド追加
  - 同名グループはグローバル設定を上書き、`attributes` 省略時はグローバルの属性リストを継承

### 設定追加
- `attribute_pool.json` に `attributeGroups` 配列（name, maxFromGroup, attributes）
- `gun_attribute_overrides.json` の各銃に `attributeGroups` 配列（グローバルとマージ）

## [1.2]
### 新機能
- `/taczaddon add <attribute> <value> [operation]` コマンド追加（手動で属性を追加・KubeJS連携用）
- `/taczaddon clear enhanced` コマンド追加（強化属性のクリア）
- 空き属性スロット表示（`showEmptySlots` config、デフォルト: off）
- Enhancement Stationの属性タイプ上限（`maxTypes` config + 銃別 `maxEnhancement` オーバーライド）
- Enhancement Stationの既存属性限定モード（`existingOnly` config）
- `/taczaddon info` でEnhanced Modifiers表示を追加

### 設定追加
- `[general] showEmptySlots` - ツールチップに空き属性スロットを表示
- `[enhancement] maxTypes` - Enhancement属性タイプ数の上限（0=無制限）
- `[enhancement] existingOnly` - 既に銃についている属性のみ選択肢に表示
- `gun_attribute_overrides.json` に `maxEnhancement` フィールド追加（銃別のタイプ制限）

## [1.0]
### 機能
- ランダム属性生成システム（4モード: FULL_RANDOM, ADAPTIVE, RARITY_ADAPTIVE, BALANCED）
- レアリティシステム（COMMON, UNCOMMON, RARE, EPIC）とアイテム名の色分け
- 銃モデル別の固定属性設定（weapon_attributes.json）
- 銃別の属性プールフィルタリング（ブラックリスト/ホワイトリスト）
- Attribute Station ブロック（ランダム属性のリロール）
- Enhancement Station ブロック（3択から属性強化を選択）
- Barrage アイテム（オフハンドで銃のRPM倍増）
- Shift展開式ツールチップ表示
- Apotheosis統合（銃専用ジェム11種、ソケットシステム、アフィックス11種）
- 日本語/英語ローカライゼーション

### コマンド
- `/taczaddon clear [random|fixed]` - 属性削除
- `/taczaddon reroll` - ランダム属性リロール
- `/taczaddon reload` - 設定ファイルリロード
- `/taczaddon info` - 銃データ詳細表示
- `/taczaddon config get/set` - 設定値の取得/変更
