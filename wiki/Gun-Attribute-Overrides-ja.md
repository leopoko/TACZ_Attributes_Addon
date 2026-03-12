[Home](Home) > 銃別属性オーバーライド

[English](Gun-Attribute-Overrides) | **日本語** | [中文](Gun-Attribute-Overrides-cn) | [한국어](Gun-Attribute-Overrides-kr)

---

## 銃別属性オーバーライド（gun_attribute_overrides.json）

ファイル: `config/tacz_attributes_addon/gun_attribute_overrides.json`

初回起動時に空のJSONファイルが生成されます。銃IDごとにランダム属性の生成ルールをオーバーライドできます。Looter-shooter系モッドパックで銃ごとに異なるビルドを作る際に便利です。

### 書式

```json
{
  "銃ID": {
    "minAttributes": 最小属性数,
    "maxAttributes": 最大属性数,
    "minAttributesPos": 正属性の最小数,
    "maxAttributesPos": 正属性の最大数,
    "minAttributesNeg": 負属性の最小数,
    "maxAttributesNeg": 負属性の最大数,
    "maxEnhancement": 強化タイプ上限,
    "attributes": [
      {"attribute": "属性ID", "minValue": 最小値, "maxValue": 最大値}
    ]
  }
}
```

### 各フィールドの説明

| フィールド | 必須 | 説明 |
|------------|------|------|
| `minAttributes` | 任意 | ランダム属性の最小数。省略時はグローバル設定値を使用 |
| `maxAttributes` | 任意 | ランダム属性の最大数。省略時はグローバル設定値を使用 |
| `minAttributesPos` | 任意 | 正の属性（バフ）の最小数。スプリットモード用 |
| `maxAttributesPos` | 任意 | 正の属性（バフ）の最大数。スプリットモード用 |
| `minAttributesNeg` | 任意 | 負の属性（デバフ）の最小数。スプリットモード用 |
| `maxAttributesNeg` | 任意 | 負の属性（デバフ）の最大数。スプリットモード用 |
| `maxEnhancement` | 任意 | 強化属性タイプ数の上限。上限に達するとEnhancement Stationは既に強化済みの属性のみ表示。0=無制限。省略時はグローバル `maxTypes` を使用 |
| `attributes` | 任意 | 許可属性のホワイトリスト。指定するとこのリストの属性のみが付与可能。省略時は通常のプールフィルタリング |

`attributes` 内の各エントリ（全フィールドは任意。省略時は `attribute_pool.json` の値を使用）:

| フィールド | 型 | 説明 |
|------------|-----|------|
| `attribute` | string | **必須**。属性ID（`tacz_attributes:` プレフィックス付き） |
| `minValue` | double | この銃でのカスタム最小値 |
| `maxValue` | double | この銃でのカスタム最大値 |
| `minValuePos` | double | 正の値（バフ）用の最小値。スプリットモード用 |
| `maxValuePos` | double | 正の値（バフ）用の最大値。スプリットモード用 |
| `minValueNeg` | double | 負の値（デバフ）用の最小値。スプリットモード用 |
| `maxValueNeg` | double | 負の値（デバフ）用の最大値。スプリットモード用 |
| `weight` | int | この銃での選択確率（大きいほど出やすい） |
| `rarityTier` | int | この銃でのレアリティティア（RARITY_ADAPTIVE/BALANCEDモードの重み付けに影響） |
| `scoreWeight` | double | この銃でのレアリティスコアへの寄与度 |
| `operation` | string | この銃での演算子（`MULTIPLY_BASE`、`ADDITION`、`MULTIPLY_TOTAL`） |

### 設定例

```json
{
  "tacz:hk416d": {
    "minAttributes": 1,
    "maxAttributes": 3,
    "attributes": [
      {"attribute": "tacz_attributes:reload_speed", "minValue": -0.20, "maxValue": 0.20, "weight": 30},
      {"attribute": "tacz_attributes:gun_damage", "minValue": -0.10, "maxValue": 0.15, "weight": 50, "rarityTier": 2},
      {"attribute": "tacz_attributes:recoil", "minValue": -0.30, "maxValue": 0.10, "scoreWeight": -120}
    ]
  },
  "tacz:rpg7": {
    "minAttributes": 0,
    "maxAttributes": 1
  }
}
```

上記の例:
- **HK416D**: 1〜3個のランダム属性。reload_speed、gun_damage、recoilの3種類のみ付与可能で、値範囲もカスタム
- **RPG-7**: 0〜1個のランダム属性。属性の種類は通常のプールフィルタリングに従う

> **ヒント:** `attributes` を省略すると属性の個数のみを制御できます。
> 設定のない銃は従来通りグローバル設定に従います。
> ファイルを編集後、ゲーム内で `/taczaddon reload` コマンドでリロードできます。

### 選択確率について

各属性エントリで `weight` や `rarityTier` を指定すると、**この銃に限り**選択確率をカスタマイズできます。省略した場合は `attribute_pool.json` の値がそのまま使用されます。

例えば、`attribute_pool.json` で `gun_damage` の weight=20、`reload_speed` の weight=15 と設定されている場合に、`gun_attribute_overrides.json` で `gun_damage` の weight を 50 にオーバーライドすると、この銃での選択確率は 50/(50+15)=77% と 15/(50+15)=23% になります。

`scoreWeight` をオーバーライドすると、この銃でのレアリティスコア計算にカスタム寄与度が適用されます。

### 正負属性の個別制御（スプリットモード）

`minAttributesPos`/`maxAttributesPos` と `minAttributesNeg`/`maxAttributesNeg` を使うと、正の属性（バフ）と負の属性（デバフ）の個数を独立して制御できます。これにより「バフ3個 + デバフ1個」のような正確な構成を保証できます。

```json
{
  "tacz:hk416d": {
    "minAttributesPos": 2,
    "maxAttributesPos": 3,
    "minAttributesNeg": 1,
    "maxAttributesNeg": 1,
    "attributes": [
      {
        "attribute": "tacz_attributes:reload_speed",
        "minValuePos": 0.10, "maxValuePos": 0.30,
        "minValueNeg": -0.30, "maxValueNeg": -0.10
      },
      {
        "attribute": "tacz_attributes:gun_damage",
        "minValuePos": 0.05, "maxValuePos": 0.15,
        "minValueNeg": -0.20, "maxValueNeg": -0.05
      }
    ]
  }
}
```

上記の例:
- **HK416D**: 正の属性を2〜3個、負の属性を1個、合計3〜4個を保証
- 正の属性が選ばれた場合は `minValuePos`〜`maxValuePos` の範囲で値を生成
- 負の属性が選ばれた場合は `minValueNeg`〜`maxValueNeg` の範囲で値を生成
- `minValuePos`/`maxValuePos`/`minValueNeg`/`maxValueNeg` は省略可能。省略時は属性の全体値範囲を `buffThreshold`（通常0.0）で分割した範囲を使用

> **注意:** スプリットモードは `minAttributesPos`/`maxAttributesPos`/`minAttributesNeg`/`maxAttributesNeg` のいずれかが設定されると自動的に有効になります。`minAttributes`/`maxAttributes` と併用する場合は合計数の上限として機能します。全生成モード（FULL_RANDOM、ADAPTIVE、RARITY_ADAPTIVE、BALANCED）で動作します。

### 銃別属性グループ（排他制御オーバーライド）

`attributeGroups` フィールドを銃別設定に追加すると、グローバルの属性グループ設定を銃ごとにオーバーライドできます。同名のグループはグローバル設定を上書きし、新しい名前のグループは追加されます。

```json
{
  "tacz:ak47": {
    "minAttributes": 3,
    "maxAttributes": 5,
    "attributeGroups": [
      {
        "name": "damage",
        "maxFromGroup": 2
      },
      {
        "name": "speed",
        "maxFromGroup": 1,
        "attributes": [
          "tacz_attributes:reload_speed",
          "tacz_attributes:draw_speed",
          "tacz_attributes:ads_speed"
        ]
      }
    ]
  }
}
```

上記の例（AK47の場合）:
- **damage グループ**: グローバル設定では `maxFromGroup: 1` だが、AK47では `2` に緩和。属性リストは `attributes` 省略時にグローバル設定を継承
- **speed グループ**: AK47専用の新しいグループ。速度系属性から最大1つのみ出現

> **注意:** `attributes` フィールドを省略すると、同名のグローバルグループの属性リストを継承します。銃別にのみ存在する新しいグループには `attributes` の指定が必要です。

---

[< 前へ: 属性プール](Attribute-Pool-ja) | [次へ >: コマンド](Commands-ja)
