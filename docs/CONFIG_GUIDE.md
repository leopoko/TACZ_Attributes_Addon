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
Apotheosis MOD との連携機能を有効にします（現在はスタブ実装）。

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
      "scoreWeight": 100
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

出現率を変更（weightを下げると出にくくなる）:
```json
{
  "attributeId": "tacz_attributes:gun_damage",
  "weight": 5
}
```

> **注意:** `attributeId` は TACZ Attributes MODに登録されている属性IDと一致させてください。
> ファイルを編集後、ゲーム内で `/taczaddon reload` コマンドでリロードできます。

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
`attribute_pool.json` と `weapon_attributes.json` をリロードします。
ゲームを再起動せずに設定変更を反映できます。

### `/taczaddon info`
手持ちの銃に付与されているアドオンデータの詳細を表示します。

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
