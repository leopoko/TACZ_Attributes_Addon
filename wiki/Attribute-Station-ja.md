[Home](Home) > Attribute Station

[English](Attribute-Station) | **日本語** | [中文](Attribute-Station-cn) | [한국어](Attribute-Station-kr)

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

### 素材設定（station_materials.json）

ファイル: `config/tacz_attributes_addon/station_materials.json`

`consumeItem = true` の場合、このJSONファイルで複数の素材タイプを登録できます。各素材にはレアリティ制約を設定可能です。初回起動時にデフォルトファイルが自動生成されます。

```json
[
  {
    "item": "minecraft:diamond",
    "count": 1
  },
  {
    "item": "minecraft:emerald",
    "count": 2,
    "minRarity": 2
  },
  {
    "item": "minecraft:nether_star",
    "count": 1,
    "targetRarity": 3
  },
  {
    "item": "minecraft:amethyst_shard",
    "count": 4,
    "maxRarity": 1
  }
]
```

| フィールド | 説明 |
|-----------|------|
| `item` | アイテムID（必須） |
| `count` | 消費数（デフォルト: 1） |
| `targetRarity` | 確定レアリティ（0-3, 最優先） |
| `minRarity` | 最低レアリティ保証（0-3） |
| `maxRarity` | 最大レアリティ制限（0-3） |

レアリティ制約フィールドはすべてオプションです。省略時は制約なし（通常ランダム）。`/taczaddon reload` でホットリロード対応。

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

[< 前へ: レアリティスコア](Rarity-Scoring-ja) | [次へ >: Enhancement Station](Enhancement-Station-ja)
