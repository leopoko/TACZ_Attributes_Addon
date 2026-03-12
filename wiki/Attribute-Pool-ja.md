[Home](Home) > 属性プール設定

[English](Attribute-Pool) | **日本語** | [中文](Attribute-Pool-cn) | [한국어](Attribute-Pool-kr)

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

### 属性グループ（排他制御）

`attributeGroups` フィールドを使用すると、類似した属性をグループ化し、同じグループから同時に出現できる属性の数を制限できます。これにより、ダメージ系の属性が大量に重複するのを防ぐなど、属性の組み合わせバランスを調整できます。

```json
{
  "attributes": [ ... ],
  "attributeGroups": [
    {
      "name": "damage",
      "maxFromGroup": 1,
      "attributes": [
        "tacz_attributes:gun_damage",
        "tacz_attributes:headshot_multiplier",
        "tacz_attributes:ads_damage",
        "tacz_attributes:hip_fire_damage"
      ]
    },
    {
      "name": "recoil",
      "maxFromGroup": 2,
      "attributes": [
        "tacz_attributes:recoil",
        "tacz_attributes:vertical_recoil",
        "tacz_attributes:horizontal_recoil"
      ]
    }
  ]
}
```

| フィールド | 説明 |
|-----------|------|
| `name` | グループ名（識別用） |
| `maxFromGroup` | このグループから1つの銃に同時に付与できる属性の最大数 |
| `attributes` | グループに含まれる属性IDのリスト |

上記の例:
- **damage グループ**: ダメージ系4属性のうち最大1つのみ出現可能
- **recoil グループ**: リコイル系3属性のうち最大2つまで出現可能

> **注意:** 1つの属性を複数のグループに含めることができます。その場合、最も制限が厳しいグループが優先されます。`attributeGroups` を省略するか空配列にすると制限なし（従来通り）です。リンク属性（ペア生成）による追加はグループ制限の対象外です。

---

[前へ: 銃モデル別固定属性](Weapon-Attributes-ja) | [次へ: 銃別属性オーバーライド](Gun-Attribute-Overrides-ja)
