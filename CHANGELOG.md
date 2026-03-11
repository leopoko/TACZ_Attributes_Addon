# Changelog

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
