# Changelog

## [1.6]
### バグ修正
- Attribute Station / Enhancement Station をツルハシで正常に採掘できず、壊しても何もドロップしなかった問題を修正
  - `requiresCorrectToolForDrops()` が指定されているのに `minecraft:mineable/pickaxe` タグとルートテーブルが無かったため、どのツールでも「不適切なツール」扱いになっていた
  - ブロックタグとルートテーブルを追加
- Apotheosis Modern Ragnarok: Zero（ApoMR）と併用すると、本MODのジェムかApoMRの銃用ジェム・アフィックスのどちらかが銃に効かなくなる問題を修正
  - Apotheosis 1.20.1 の `LootCategory.forItem()` は登録順で最初に一致したカテゴリを返す。本MODの `gun` とApoMRの `full_auto` / `semi_auto` / `shotgun` / `bolt_action` はどちらも共通セットアップで登録されるため、起動ごとに順序が変わり、先に登録された側がすべての銃を取っていた
  - `gun` カテゴリはApoMRのカテゴリに該当しない銃だけにマッチするよう変更（ApoMRの分類を優先）
  - `GemMixin` を追加し、ApoMRの銃カテゴリでボーナスが定義されていないジェムは `gun` のボーナスを使うようにした（`Gem#getBonus` と、ソケット可否判定の `Gem#isValidIn` の両方）
  - ApoMR 6.0.0 環境で、AK47 / M700 / M870 / Glock17 に本MODのジェム11種すべてとApoMRのジェムが装着可能なことを確認

## [1.5]
### バグ修正
- Split Modeでリコイル系属性（recoil, vertical_recoil, horizontal_recoil）の正負判定が逆になっていた問題を修正
  - 負のscoreWeightを持つ属性は自動的に反転属性として扱い、負の値をバフ/正の値をデバフとして正しく分類
- gun_attribute_overridesの`minValueNeg`/`maxValueNeg`がattribute_poolの`minValue`/`maxValue`範囲外の場合、負の属性が生成されない問題を修正
  - オーバーライドで`minValuePos`/`maxValuePos`/`minValueNeg`/`maxValueNeg`が指定されている場合、ベース範囲に関係なくプールに追加されるように変更
- `/taczaddon add`コマンドで`tacz_attributes:属性名`形式が入力できない問題を修正
  - `StringArgumentType.word()`を`ResourceLocationArgument.id()`に変更し、コロンを含むリソースロケーションを正しくパース

## [1.4]
### 変更
- Apotheosisソケット数を入手時に固定するように変更（従来はレアリティに連動して動的に変化）
- ソケット拡張にApotheosisのSigil of Socketingを使用可能に
- 既存の銃に対するソケット移行処理を追加（初回ロード時に自動適用）

### 技術的変更
- `GetItemSocketsEvent`のイベントハンドラを廃止し、`SocketHelper.setSockets()`でApotheosisネイティブNBTに直接書き込むように変更
- リロール時・Attribute Station処理時にソケット数が変化しなくなった

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
