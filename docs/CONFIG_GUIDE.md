# TACZ Attributes Addon - コンフィグ詳細ガイド

設定ファイル: `config/tacz_attributes_addon-common.toml`（ゲーム初回起動時に自動生成）

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

---

## [random] ランダム属性生成

ランダム属性の生成アルゴリズムを詳細に調整できます。

### `randomMode` (デフォルト: `RARITY_ADAPTIVE`)

ランダム属性の選択アルゴリズム。

| モード | 説明 | 推奨用途 |
|--------|------|----------|
| `FULL_RANDOM` | 全属性プールから完全ランダム。銃タイプを考慮しない。SMGにスナイパー専用属性が付く場合がある | カジュアル・カオス |
| `ADAPTIVE` | 銃タイプと射撃モードで属性をフィルタリング。対象外の属性は選ばれない | バランス重視 |
| `RARITY_ADAPTIVE` | ADAPTIVE + レアリティ重み付け。高レアリティ属性ほど選ばれにくい + 値のスキュー（低い値が出やすい） | **推奨（デフォルト）** |
| `BALANCED` | RARITY_ADAPTIVE + バフとデバフの比率を自動調整。buffDebuffRatio で制御 | 公平性重視 |

### `fixedAttributeMode` (デフォルト: `BOTH_STACKING`)

固定属性（weapon_attributes.json）とランダム属性の関係。

| モード | 固定属性 | ランダム属性 | 説明 |
|--------|---------|-------------|------|
| `FIXED_ONLY` | 適用する | **生成しない** | 固定属性のみ。ランダム性なし |
| `RANDOM_ONLY` | **適用しない** | 生成する | ランダムのみ。固定設定を無視 |
| `BOTH_STACKING` | 適用する | 生成する | 両方を独立して適用（それぞれ別のNBTタグに格納）。**推奨** |
| `FIXED_INFLUENCES_RANDOM` | 適用する | 生成する（影響あり） | 固定属性がランダム生成の重みに影響を与える |

### `minAttributes` (デフォルト: `1`, 範囲: 0〜20)
1つの銃に付与されるランダム属性の最小数。0にするとランダム属性が付かない場合がある。

### `maxAttributes` (デフォルト: `4`, 範囲: 0〜20)
1つの銃に付与されるランダム属性の最大数。実際の数は min〜max の間でランダムに決定。

> **例:** min=2, max=5 → 銃ごとに2〜5個のランダム属性が付与される

### `valueDistribution` (デフォルト: `EXPONENTIAL`)

属性値の分布曲線。min〜maxの範囲内でどのように値が分布するかを制御。

| 分布 | 特性 | 説明 |
|------|------|------|
| `LINEAR` | 均等分布 | 全ての値が同じ確率で出現 |
| `EXPONENTIAL` | 低い値に偏る | 小さい値が出やすく、大きい値はまれ。指数: `distributionExponent` で制御。**推奨** |
| `QUADRATIC` | 二次曲線 | EXPONENTIALより緩やかに低い値に偏る |

### `distributionExponent` (デフォルト: `2.0`, 範囲: 1.0〜10.0)
EXPONENTIAL分布の指数。値が大きいほど低い値に偏る。

- `1.0`: LINEAR と同等（偏りなし）
- `2.0`: 適度に低い値寄り（**推奨**）
- `5.0`: 非常に低い値に偏る（高い値はほぼ出ない）
- `10.0`: 極端に低い値に偏る

### `raritySpreadFactor` (デフォルト: `2.0`, 範囲: 1.0〜10.0)
RARITY_ADAPTIVE/BALANCEDモードで、レアリティティアによる属性選択の重み分散。

- `1.0`: 全てのレアリティティアが同じ確率で選ばれる
- `2.0`: 高レアリティほど選ばれにくい（**推奨**）
- `5.0`: レアリティティア4の属性は非常にまれ

> **計算式:** weight = baseWeight / (rarityTier ^ raritySpreadFactor)

### `buffDebuffRatio` (デフォルト: `1.0`, 範囲: 0.1〜5.0)
BALANCEDモード専用。バフとデバフの目標比率。

- `0.5`: デバフが2倍多い（ハードコア向け）
- `1.0`: バフとデバフが同数（**推奨**）
- `2.0`: バフが2倍多い（プレイヤー有利）

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

## [station] Attribute Station ブロック

### `processingTime` (デフォルト: `200`, 範囲: 1〜72000)
加工にかかる時間（tick単位）。20 tick = 1秒。

- `200`: 10秒（**デフォルト**）
- `100`: 5秒（速い）
- `1200`: 1分（遅い）
- `72000`: 1時間（最大）

### `consumeItem` (デフォルト: `false`)
加工時にアイテムを消費するかどうか。

- `false`: アイテム消費なし。銃を入れるだけで加工（**デフォルト**）
- `true`: 指定アイテムを消費して加工

### `consumeItemId` (デフォルト: `"minecraft:diamond"`)
消費するアイテムのID。`consumeItem` が `true` の時のみ有効。

**設定例:**
```toml
consumeItemId = "minecraft:diamond"          # ダイヤモンド
consumeItemId = "minecraft:netherite_ingot"  # ネザライトインゴット
consumeItemId = "minecraft:emerald"          # エメラルド
consumeItemId = "tacz:gunsmith_table"        # TACZ MODのアイテム
```

### `consumeCount` (デフォルト: `1`, 範囲: 1〜64)
1回の加工で消費するアイテムの数。

### `allowReroll` (デフォルト: `true`)
既に属性が付いている銃のリロール（属性の再生成）を許可するか。

- `true`: 何度でもリロール可能（上限は `maxRerolls` で制御）
- `false`: 一度属性が付いた銃は再加工できない

### `maxRerolls` (デフォルト: `0`, 範囲: 0〜1000)
1つの銃に対するリロール回数の上限。

- `0`: **無制限**（何度でもリロール可能）
- `3`: 3回までリロール可能
- `10`: 10回までリロール可能

> リロール回数はツールチップに表示されます。
> 上限に達した銃はAttribute Stationで加工できなくなります。

---

## [apotheosis] Apotheosis連携設定

Apotheosis MODがインストールされている場合に有効になるソケット/ジェム機能の設定です。

### `gunBaseSockets` (デフォルト: `2`, 範囲: 0〜6)
レアリティに連動しない場合（`socketsScaleWithRarity = false`）の固定ソケット数。

### `socketsScaleWithRarity` (デフォルト: `true`)
ソケット数をレアリティに連動させるかどうか。

- `true`: レアリティに応じてソケット数が変動（下記の設定値を使用）
- `false`: 全ての銃が `gunBaseSockets` で指定された固定数のソケットを持つ

### `commonSockets` (デフォルト: `1`, 範囲: 0〜6)
COMMONレアリティ銃のソケット数。

### `uncommonSockets` (デフォルト: `2`, 範囲: 0〜6)
UNCOMMONレアリティ銃のソケット数。

### `rareSockets` (デフォルト: `3`, 範囲: 0〜6)
RAREレアリティ銃のソケット数。

### `epicSockets` (デフォルト: `4`, 範囲: 0〜6)
EPICレアリティ銃のソケット数。

---

## 銃モデル別固定属性（weapon_attributes.json）

ファイル: `config/tacz_attributes_addon/weapon_attributes.json`

初回起動時に空のJSONファイル `{}` が生成されます。銃IDごとに固定属性を設定できます。

### 書式

```json
{
  "銃ID": [
    {
      "attribute": "属性ID",
      "value": 数値,
      "operation": "演算子"
    }
  ]
}
```

### 演算子（operation）

| 演算子 | 説明 | 例 |
|--------|------|-----|
| `MULTIPLY_BASE` | 基本値に対する乗算。0.10 = +10% | ほとんどの属性に使用 |
| `ADDITION` | 基本値に加算 | knockback_base, ammo_recovery_amount 等 |
| `MULTIPLY_TOTAL` | 最終値に対する乗算 | 特殊な場合のみ |

### 設定例

```json
{
  "tacz:ak47": [
    {
      "attribute": "tacz_attributes:gun_damage",
      "value": 0.05,
      "operation": "MULTIPLY_BASE"
    },
    {
      "attribute": "tacz_attributes:recoil",
      "value": 0.15,
      "operation": "MULTIPLY_BASE"
    }
  ],
  "tacz:m4a1": [
    {
      "attribute": "tacz_attributes:ads_accuracy",
      "value": 0.08,
      "operation": "MULTIPLY_BASE"
    },
    {
      "attribute": "tacz_attributes:reload_speed",
      "value": 0.05,
      "operation": "MULTIPLY_BASE"
    }
  ],
  "tacz:glock_17": [
    {
      "attribute": "tacz_attributes:draw_speed",
      "value": 0.20,
      "operation": "MULTIPLY_BASE"
    }
  ]
}
```

> **ヒント:** 銃IDは `tacz:銃名` の形式です。TACZのデータパックで定義されている銃IDと一致させてください。

---

## ホッパー対応

Attribute Station はホッパーに対応しています。

| ホッパー方向 | アクセスするスロット |
|-------------|---------------------|
| 上から | 銃スロット（0）、素材スロット（1） |
| 横から | 銃スロット（0）、素材スロット（1） |
| 下から | 出力スロット（2） |

### 自動加工ライン構築例

```
[ホッパー(上)] → 銃を投入
[Attribute Station] ← 自動加工
[ホッパー(下)] → 完成品を回収
```

素材消費を有効にしている場合は、横からホッパーで素材を供給できます。

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

---

## 属性プール設定（attribute_pool.json）

ファイル: `config/tacz_attributes_addon/attribute_pool.json`

初回起動時にデフォルトの属性プールを含むJSONファイルが自動生成されます。
各属性の出現率、値範囲、レアリティスコアなどを自由にカスタマイズできます。

### 書式

```json
{
  "attributes": [
    {
      "attributeId": "tacz_attributes:gun_damage",
      "minValue": -0.20,
      "maxValue": 0.30,
      "operation": "MULTIPLY_BASE",
      "weight": 20,
      "rarityTier": 1,
      "applicableGunTypes": [],
      "buffThreshold": 0.0,
      "scoreWeight": 100,
      "linkedAttribute": "tacz_attributes:some_other_attribute"
    }
  ]
}
```

### 各フィールドの説明

| フィールド | 説明 | 例 |
|------------|------|-----|
| `attributeId` | TACZ Attributes の属性ID | `"tacz_attributes:gun_damage"` |
| `minValue` | ランダム生成の最小値 | `-0.20` (= -20%) |
| `maxValue` | ランダム生成の最大値 | `0.30` (= +30%) |
| `operation` | 演算子 (`MULTIPLY_BASE`, `ADDITION`, `MULTIPLY_TOTAL`) | `"MULTIPLY_BASE"` |
| `weight` | 選択頻度（大きいほど出やすい） | `20` |
| `rarityTier` | レアリティティア（1=普通, 2=やや珍しい, 3=レア, 4=超レア） | `1` |
| `applicableGunTypes` | 対象銃タイプ（空=全種類）。`pistol`, `sniper`, `rifle`, `shotgun`, `smg`, `rpg`, `mg` | `["rifle", "smg"]` |
| `buffThreshold` | この値以上がバフ（通常は0.0） | `0.0` |
| `scoreWeight` | レアリティスコアへの寄与度（マイナス=リコイルのような反転属性） | `100` or `-90` |
| `linkedAttribute` | （任意）ペアで生成すべきパートナー属性のID。設定するとこの属性が選ばれた際、パートナーも自動で追加される | `"tacz_attributes:ammo_recovery_amount"` |
| `weaponBlacklist` | （任意）この属性を除外する武器IDリスト。指定された武器には絶対に付与されない | `["tacz:rpg7"]` |
| `weaponWhitelist` | （任意）この属性を追加で許可する武器IDリスト。`applicableGunTypes` に関係なく許可される | `["tacz:desert_eagle"]` |

### 武器別フィルタリング（weaponBlacklist / weaponWhitelist）

`applicableGunTypes` が武器**種**（pistol, rifle等）でフィルタリングするのに対し、`weaponBlacklist` と `weaponWhitelist` は個別の武器**ID**（tacz:ak47等）で制御します。

**フィルタ判定順序:**
1. `weaponBlacklist` に含まれる → **常に除外**（最優先）
2. `weaponWhitelist` が非空で含まれている → **常に許可**（`applicableGunTypes` に関係なく）
3. `applicableGunTypes` → 従来通りの武器種チェック

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

上記の例:
- `tacz:rpg7` → ブラックリストで除外
- `tacz:desert_eagle` → pistolだがホワイトリストで許可
- sniperまたはrifle型の銃 → `applicableGunTypes` で許可
- それ以外（shotgun, smg等） → 不可

> **注意:** 両フィールドとも省略可能です。省略時はフィルタなし（従来通りの動作）。
> `FULL_RANDOM` モードでもブラックリスト/ホワイトリストは適用されます。

### リンク属性（ペア生成）

`linkedAttribute` フィールドを使用すると、属性のペア生成を設定できます。例えば `ammo_recovery_chance` が選ばれた場合、`ammo_recovery_amount` も自動的にセットで付与されます。

デフォルトでは以下のペアが設定されています：

| 属性 | リンク先 | 説明 |
|------|----------|------|
| `ammo_recovery_chance` | `ammo_recovery_amount` | 弾薬回復の確率→量 |
| `ammo_recovery_amount` | `ammo_recovery_chance` | 弾薬回復の量→確率 |
| `ammo_recovery_percent` | `ammo_recovery_chance` | 弾薬回復の割合→確率 |
| `bonus_ammo_chance` | `bonus_ammo_amount` | 追加弾薬の確率→量 |
| `bonus_ammo_amount` | `bonus_ammo_chance` | 追加弾薬の量→確率 |

### カスタマイズ例

新しい属性を追加:
```json
{
  "attributeId": "tacz_attributes:my_custom_attr",
  "minValue": -0.10,
  "maxValue": 0.20,
  "operation": "MULTIPLY_BASE",
  "weight": 10,
  "rarityTier": 2,
  "applicableGunTypes": ["rifle", "sniper"],
  "buffThreshold": 0.0,
  "scoreWeight": 80
}
```

リンク属性をカスタム設定:
```json
{
  "attributeId": "tacz_attributes:custom_chance",
  "minValue": 0.0,
  "maxValue": 0.20,
  "operation": "ADDITION",
  "weight": 5,
  "rarityTier": 3,
  "applicableGunTypes": [],
  "buffThreshold": 0.0,
  "scoreWeight": 150,
  "linkedAttribute": "tacz_attributes:custom_amount"
}
```

> **注意:** `attributeId` は TACZ Attributes MODに登録されている属性IDと一致させてください。
> ファイルを編集後、ゲーム内で `/taczaddon reload` コマンドでリロードできます。

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

---

## コマンド

OP権限（レベル2）が必要です。

### `/taczaddon clear`
手持ちの銃から全てのアドオン属性（ランダム・固定・スコア・レアリティ）を削除します。

### `/taczaddon clear random`
ランダム属性のみを削除します。固定属性は残ります。

### `/taczaddon clear fixed`
固定属性のみを削除します。ランダム属性は残ります。

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
- `enableApotheosis`, `enableRarityScoring`
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

## Apotheosis 銃専用ジェム一覧

Apotheosis連携が有効な場合、以下の銃専用ジェムが利用可能です。

| ジェム | 属性 | タイプ |
|--------|------|--------|
| マークスマンの宝石 | gun_damage | 単体属性 |
| 安定の宝石 | recoil（軽減） | 単体属性 |
| 速装填の宝石 | reload_speed | 単体属性 |
| 精密射撃の宝石 | headshot_multiplier | 単体属性 |
| 拡張弾倉の宝石 | magazine_capacity | 単体属性 |
| 速射の宝石 | rpm_multiplier | 単体属性 |
| 戦術の宝石 | ads_speed + ads_accuracy | 複合属性 |
| 弾薬節約の宝石 | ammo_save_chance | 単体属性 |
| 弾薬回復の宝石 | ammo_recovery_chance + ammo_recovery_amount | 複合属性 |
| 追加弾薬の宝石 | bonus_ammo_chance + bonus_ammo_amount | 複合属性 |
| ノックバックの宝石 | knockback_base | 単体属性 |

## Apotheosis 銃専用アフィックス一覧

| アフィックス | 属性 |
|-------------|------|
| 殺傷の | gun_damage |
| 安定の | recoil（軽減） |
| 迅速の | reload_speed |
| 精密の | headshot_multiplier |
| 大容量の | magazine_capacity |
| 速射の | rpm_multiplier |
| 集中の | ads_speed |
| 節約の | ammo_save_chance |
| 命中の | ads_accuracy |
| 敏捷の | draw_speed |
| 強打の | knockback_base |
