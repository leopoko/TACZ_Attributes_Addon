# CLAUDE.md - TACZ Attributes Addon 開発ガイド

## プロジェクト概要

Minecraft 1.20.1 Forge MODアドオン。TACZ（銃MOD）とTACZ Attributes（属性MOD）を統合し、銃アイテムにランダム属性・レアリティシステムを追加する。

## ビルド・実行・公開

```bash
# ビルド
./gradlew build

# クライアント起動（テスト用）
./gradlew runClient

# サーバー起動
./gradlew runServer

# CurseForgeへ公開（ビルド→アップロード）
./gradlew publishMods
# 環境変数 CURSEFORGE_TOKEN が必要
```

成果物: `build/libs/` 配下の `.jar`ファイル（`-sources`なし）

### CurseForge公開設定
- **プラグイン**: `me.modmuss50.mod-publish-plugin` v0.8.4
- **プロジェクトID**: `1482794`（`gradle.properties` の `curseforge_project_id`）
- **GitHub Actions**: `.github/workflows/publish.yml`（Release作成 or 手動トリガーで自動公開）
- **チェンジログ**: `CHANGELOG.md` の最新 `## [x.xx]` セクションを自動抽出

## 技術スタック

- **Minecraft** 1.20.1 / **Forge** 47.4.16 / **Java** 17
- **Mixin** 0.8.5（SpongePowered）
- **依存MOD**: TACZ (必須), TACZ Attributes (必須), Apotheosis (任意)
- **マッピング**: official (Mojang公式)

## アーキテクチャ上の重要事項

### TACZ銃の仕組み
- 全ての銃は同一アイテム `ModernKineticGunItem` のインスタンス
- 銃の種別はNBTタグ `GunId`（ResourceLocation, 例: `tacz:ak47`）で区別
- 銃データは `TimelessAPI.getCommonGunIndex(gunId)` でJSON DataPackから取得
- 銃タイプ: PISTOL, SNIPER, RIFLE, SHOTGUN, SMG, RPG, MG（`GunTabType`列挙型）

### TACZ Attributesの仕組み
- Forgeのエンティティ属性をプレイヤーに適用（アイテムではなくプレイヤー側）
- 41個のグローバル属性 + 238個の銃種別属性（7タイプ × 34属性）
- Mixinで銃の射撃・ダメージ処理をインターセプトして `entity.getAttributeValue()` を参照
- 属性名前空間: `tacz_attributes:`

### ブリッジパターン（本MODの核心設計）
属性はアイテムNBTに格納 → プレイヤーが銃を持つと `AttributeModifier` としてプレイヤーに適用 → TACZ Attributesの既存Mixinが自動的にその値を参照。銃を持ち替えると旧属性を除去し新属性を適用。

### NBTデータ構造
```
ItemStack NBT → TaczAddon: {
  Modifiers: [{Attr: "tacz_attributes:gun_damage", Val: 0.15, Op: 1}, ...],
  FixedModifiers: [{Attr: "tacz_attributes:reload_speed", Val: 0.10, Op: 1}, ...],
  Score: 42,        // レアリティスコア
  Rarity: 2,        // 0=COMMON, 1=UNCOMMON, 2=RARE, 3=EPIC
  Sealed: 1,         // 再生成防止フラグ
  RerollCount: 3     // リロール回数
}
```

## ディレクトリ構造

```
src/main/java/com/github/leopoko/tacz_attributes_addon/
├── TaczAttributesAddon.java          # MODエントリポイント、全DeferredRegister登録
├── config/
│   └── CommonConfig.java             # ForgeConfigSpec（機能トグル、ランダム設定、レアリティ閾値）
├── data/
│   ├── GunModifier.java              # 単一修飾子レコード（属性ID, 値, 演算子）
│   ├── GunAttributeData.java         # アイテムNBT読み書きユーティリティ
│   ├── AttributeEntry.java           # 属性プールエントリ定義
│   ├── AttributeRegistry.java        # 全属性登録（デフォルト~40属性）
│   └── GunAttributeOverrides.java    # 銃別属性オーバーライド（個数・種類・値範囲）
├── random/
│   ├── AttributeGenerator.java       # ランダム生成コア（4モード対応）
│   ├── GunTypeFilter.java            # 銃タイプ・射撃モードによるフィルタリング
│   └── ValueDistribution.java        # 値分布曲線（LINEAR/EXPONENTIAL/QUADRATIC）
├── bridge/
│   └── AttributeBridge.java          # アイテムNBT → プレイヤーAttributeModifier同期
├── handler/
│   ├── GunObtainHandler.java         # 取得時自動付与
│   ├── WeaponTypeHandler.java        # 銃モデル別固定属性
│   ├── RarityHandler.java            # スコア計算→レアリティ変換
│   └── TooltipHandler.java           # ツールチップ表示
├── block/
│   ├── AttributeStationBlock.java    # Attribute Station ブロック
│   ├── AttributeStationBlockEntity.java
│   ├── AttributeStationMenu.java
│   ├── AttributeStationScreen.java
│   ├── EnhancementStationBlock.java  # Enhancement Station ブロック
│   ├── EnhancementStationBlockEntity.java
│   ├── EnhancementStationMenu.java
│   └── EnhancementStationScreen.java
├── item/
│   ├── BarrageItem.java              # オフハンドRPM倍増アイテム
│   └── GunIngredient.java            # 銃アイテム用カスタムレシピ材料
├── init/
│   ├── ModBlocks.java                # ブロック登録
│   ├── ModItems.java                 # アイテム登録
│   ├── ModBlockEntities.java         # BlockEntity登録
│   └── ModMenuTypes.java             # メニュー登録
├── network/
│   ├── ModNetwork.java               # パケット登録・送信
│   ├── EnhancementChoicesPacket.java  # サーバー→クライアント: 選択肢送信
│   └── EnhancementActionPacket.java   # クライアント→サーバー: 選択結果送信
├── command/
│   └── ModCommands.java              # /taczaddon コマンド
├── mixin/
│   ├── GunRarityMixin.java           # Item.getRarity()インジェクト（レアリティ色分け）
│   └── LootControllerMixin.java      # 銃ルートシステムフック
└── compat/apotheosis/
    ├── ApotheosisCompat.java         # Apotheosis統合メイン
    ├── GunLootCategory.java          # 銃をApotheosisルートカテゴリに登録
    ├── GunSocketHandler.java         # ソケット/ジェム装着処理
    ├── GemBridgeHelper.java          # ジェム→銃属性ブリッジ
    ├── GemInfoHelper.java            # ジェムステータス解析
    └── GemTooltipHelper.java         # ジェム情報ツールチップ表示
```

### リソース構造
```
src/main/resources/
├── META-INF/mods.toml
├── tacz_attributes_addon.mixins.json
├── assets/tacz_attributes_addon/
│   ├── lang/{en_us,ja_jp}.json
│   ├── blockstates/{attribute_station,enhancement_station}.json
│   ├── models/block/{attribute_station,enhancement_station}.json
│   ├── models/item/{attribute_station,enhancement_station,barrage}.json
│   └── textures/items/gems/*.png     # ジェムテクスチャ（11種）
├── assets/apotheosis/models/item/gems/*.json  # ジェムモデル
└── data/tacz_attributes_addon/
    ├── recipes/{attribute_station,enhancement_station,barrage}.json
    ├── affixes/gun/*.json             # アフィックス（11種）
    └── gems/*.json                    # ジェム定義（11種）
```

## ランダム生成の4モード

| モード | 説明 |
|--------|------|
| `FULL_RANDOM` | 全属性プールから完全ランダム選択・値生成 |
| `ADAPTIVE` | 銃タイプ・射撃モードで対象属性をフィルタリング |
| `RARITY_ADAPTIVE` | フィルタ + 重み付き選択 + レアリティによるスキュー |
| `BALANCED` | フィルタ + 重み + バフ/デバフ比率バランス調整 |

## 固定属性モード（WeaponTypeHandler + GunObtainHandler の関係）

| モード | 説明 |
|--------|------|
| `FIXED_ONLY` | 固定属性のみ（ランダム無効） |
| `RANDOM_ONLY` | ランダムのみ（固定無効） |
| `BOTH_STACKING` | 固定とランダムの両方を独立適用 |
| `FIXED_INFLUENCES_RANDOM` | 固定属性がランダム生成に影響 |

## Mixin注意事項

- `GunRarityMixin`: `Item.class`の`getRarity`をインジェクト（`ModernKineticGunItem`ではなく親クラスの`Item`を対象）
- `IGun.getIGunOrNull(stack)` でTACZ銃アイテムのみに制限
- `remap = true`（デフォルト）。`getRarity`はバニラメソッドなのでSRGリマッピングが必要
- `defaultRequire: 0`（コンパイル時の検証警告を抑制）
- `LootControllerMixin`: 銃ルートシステムにフック

## 設定ファイルの場所

- **Forge Config**: `config/tacz_attributes_addon-common.toml`（自動生成）
- **属性プール**: `config/tacz_attributes_addon/attribute_pool.json`（初回起動時にデフォルト生成）
- **銃別固定属性**: `config/tacz_attributes_addon/weapon_attributes.json`（初回起動時に空ファイル生成）
- **銃別属性オーバーライド**: `config/tacz_attributes_addon/gun_attribute_overrides.json`（初回起動時に空ファイル生成）

## コマンド

| コマンド | 説明 | 権限 |
|----------|------|------|
| `/taczaddon clear` | 手持ちの銃から全アドオン属性を削除 | OP |
| `/taczaddon clear random` | ランダム属性のみ削除 | OP |
| `/taczaddon clear fixed` | 固定属性のみ削除 | OP |
| `/taczaddon clear enhanced` | 強化属性のみ削除 | OP |
| `/taczaddon add <attr> <value> [op]` | 手持ちの銃に属性を手動追加（強化属性に追加） | OP |
| `/taczaddon reroll` | 手持ちの銃のランダム属性をリロール | OP |
| `/taczaddon reload` | attribute_pool.json, weapon_attributes.json, gun_attribute_overrides.json をリロード | OP |
| `/taczaddon info` | 手持ちの銃のアドオンデータを詳細表示 | OP |
| `/taczaddon config get <key>` | 設定値を取得 | OP |
| `/taczaddon config set <key> <value>` | 設定値を一時変更（再起動でリセット） | OP |

## 関連プロジェクトのソースコード参照先

- TACZ本体: `H:\work\TACZ\TACZ\`
- TACZ Attributes: `H:\work\TACZ\TaCZ_Attributes\`

## Attribute Station の実装詳細

- `WorldlyContainer`実装でホッパー対応（上/横=入力, 下=出力）
- `ContainerData`でサーバー→クライアント進捗同期
- GUIはプログラム描画（外部テクスチャ不要）。進捗バーとパーセンテージ表示あり
- リロール回数をNBT `RerollCount` で追跡。`maxRerolls` 設定で上限制御（0=無制限）
- 素材スロット（slot 1）は銃アイテムを拒否する（`mayPlace` + `canPlaceItemThroughFace`）

## Enhancement Station の実装詳細

- 銃を入れると3つのランダム属性強化選択肢を生成
- プレイヤーが1つ選択すると銃に適用
- `EnhancementChoicesPacket`（S→C）で選択肢送信、`EnhancementActionPacket`（C→S）で選択送信
- 選択肢はプレイヤーごとにサーバー側の`playerChoices`マップで管理（transient）
- 銃の同一性はGunId比較で判定（NBT全体ではなく）→ 選択肢のフラッシュ防止
- **属性タイプ制限**: `maxTypes`（グローバル）または銃別 `maxEnhancement`（`gun_attribute_overrides.json`）で強化属性の種類数を制限。上限到達時は既存の強化属性のみが選択肢に表示
- **既存属性限定モード**: `existingOnly` config で銃に既にある属性のみを選択肢に制限

## Barrage アイテム

- Epic レアリティのオフハンドアイテム
- 保持中、銃のRPM倍増 + ADS速度・リロード速度向上
- エンチャントグリント常時表示、耐火、スタック上限1
- `tacz_attributes:rpm_multiplier` 等の属性を `addTransientModifier` で適用

## Apotheosis統合

- **ジェム11種**: ammo_recovery, bonus_ammo, conservation, extended_mag, knockback, marksman, quickloader, rapid_fire, sharpshooter, stabilizer, tactical
- **アフィックス11種**: accurate, agile, capacious, deadly, economical, focused, forceful, precise, rapid, steadfast, swift
- `GunLootCategory` で銃をApotheosisのルートカテゴリに登録
- `GunSocketHandler` でジェムの装着処理
- `GemBridgeHelper` でジェムステータスを銃属性に変換
- `GemTooltipHelper` でツールチップにジェム情報表示

## 固定属性とランダム属性の両立（Coexistence）

- 固定属性（FixedModifiers）とランダム属性（Modifiers）は**独立して両立**する
- 同じ属性ID（例: `gun_damage`）が両方に存在しても、両方ともプレイヤーに適用される
- リロール時: ランダム属性のみ再生成。固定属性はコンフィグから常に再適用される
- 例: AKにダメージ×0.95が固定設定 → リロールでダメージ×1.2がつく → 両方適用されプレイヤーは0.95 + 1.2の効果を受ける
- `AttributeBridge` はインデックスベースのUUID生成で同一属性IDの複数モディファイアを区別する
- NBTでは `Modifiers`（ランダム）と `FixedModifiers`（固定）に分離格納される

## マルチプレイ対応

- `AttributeBridge`: `WeakHashMap<Player, ...>` でプレイヤーごとに独立管理
- `GunObtainHandler`: `player.tickCount` でプレイヤーごとに独立したチェック間隔
- 属性データはアイテムNBTに格納 → Minecraftの標準アイテム同期で自動的にマルチプレイ対応
- `addTransientModifier` 使用 → プレイヤーNBTには保存されず、銃を持っている間のみ適用
- BlockEntityは `getUpdateTag()`/`getUpdatePacket()` でクライアントGUI同期
- コマンド実行後は `AttributeBridge.refreshPlayer()` で即座にモディファイア再同期

## スコアリング計算式

```
スコア = Σ (value × scoreWeight)
```
- **Math.absは使わない**（以前のバグ）。符号が自然にバフ/デバフを処理する
- 通常属性: value=+0.15, scoreWeight=+100 → +15（バフで正のスコア）
- リコイル: value=-0.30, scoreWeight=-90 → +27（リコイル減少で正のスコア）

## 銃別属性オーバーライド（Per-Gun Attribute Overrides）

- `gun_attribute_overrides.json` で銃IDごとにランダム属性の生成ルールをオーバーライド可能
- 設定のない銃はグローバル設定（CommonConfig + attribute_pool.json）に従う
- オーバーライド可能な項目:
  - `minAttributes`/`maxAttributes`: 銃ごとの属性個数制限（省略時はグローバル値を使用）
  - `minAttributesPos`/`maxAttributesPos`: 正の属性（バフ）個数を個別制御
  - `minAttributesNeg`/`maxAttributesNeg`: 負の属性（デバフ）個数を個別制御
  - `maxEnhancement`: Enhancement Stationの強化属性タイプ数上限（省略時はグローバル `maxTypes` を使用。0=無制限）
  - `attributes`: 許可属性のホワイトリスト。各属性に以下のオプションフィールドを指定可能（全て省略時は `attribute_pool.json` の値を使用）:
    - `minValue`/`maxValue`: カスタム値範囲
    - `minValuePos`/`maxValuePos`: 正の値（バフ）専用の値範囲
    - `minValueNeg`/`maxValueNeg`: 負の値（デバフ）専用の値範囲
    - `weight`: 選択確率（出現頻度）
    - `rarityTier`: レアリティティア（RARITY_ADAPTIVE/BALANCEDモードでの重み付け）
    - `scoreWeight`: レアリティスコアへの寄与度
    - `operation`: 演算子（`MULTIPLY_BASE`/`ADDITION`/`MULTIPLY_TOTAL`）
- **スプリットモード**: `minAttributesPos`/`maxAttributesPos`/`minAttributesNeg`/`maxAttributesNeg` のいずれかを設定すると、正負の属性を独立して選択・生成する（`AttributeGenerator.generateSplit()`）。属性プールを `buffThreshold` で正・負対応に分割し、それぞれから独立して重み付き選択を行う
- `AttributeGenerator.generate()` でオーバーライドを参照し、プールフィルタリング後に追加フィルタとして適用
- `RarityHandler.calculateScore()` でオーバーライドの `scoreWeight` を参照
- `/taczaddon reload` でホットリロード対応

## 属性プールの武器別フィルタリング（Per-Weapon Filtering）

`attribute_pool.json` の各属性エントリに `weaponBlacklist` と `weaponWhitelist` を設定することで、個別の武器ID単位（例: `tacz:ak47`）で属性の付与可否を制御できる。既存の `applicableGunTypes`（武器種フィルタ）と併用可能。

### フィルタ判定順序
1. **`weaponBlacklist`**: 武器IDが含まれる → **常に除外**（最優先）
2. **`weaponWhitelist`**: 武器IDが含まれる → **常に許可**（`applicableGunTypes` に関係なく）
3. **`applicableGunTypes`**: 従来通りの武器種チェック（空 = 全種許可）

### JSON設定例
```json
{
  "attributeId": "tacz_attributes:headshot_multiplier",
  "minValue": -0.15,
  "maxValue": 0.50,
  "applicableGunTypes": ["sniper", "rifle"],
  "weaponBlacklist": ["tacz:rpg7"],
  "weaponWhitelist": ["tacz:desert_eagle"]
}
```
上記の例では:
- `tacz:rpg7` → ブラックリストで除外（たとえ他条件を満たしても不可）
- `tacz:desert_eagle` → ホワイトリストで許可（pistolだが `applicableGunTypes` を無視して許可）
- sniperまたはrifle型の銃 → `applicableGunTypes` で許可
- それ以外のpistol/shotgun/smg等 → 不可

### 実装詳細
- `AttributeEntry.isApplicable(gunType, gunId)`: 3段階判定メソッド
- `GunTypeFilter.filter()`: `gunId`（`ResourceLocation`）パラメータでフィルタリング
- `FULL_RANDOM` モードでもブラックリスト/ホワイトリストは適用される
- 両フィールドともオプション（省略時 = 空リスト = フィルタなし）
- `/taczaddon reload` でホットリロード対応

## 未実装・今後の課題

- **ブロックテクスチャ**: 独自テクスチャ未作成（バニラ鍛冶台テクスチャを借用）
