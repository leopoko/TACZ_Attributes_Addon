[Home](Home) > ランダム属性生成

[English](Random-Attribute-Generation) | **日本語** | [中文](Random-Attribute-Generation-cn) | [한국어](Random-Attribute-Generation-kr)

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

[前へ: 機能トグル](Feature-Toggles-ja) | [次へ: レアリティスコア閾値](Rarity-Scoring-ja)
