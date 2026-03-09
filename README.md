# TACZ Attributes Addon

**Minecraft 1.20.1 Forge MOD** — TACZ（銃MOD）の銃アイテムにランダム属性とレアリティシステムを追加するアドオン。

> [English](#english) | [日本語](#日本語)

---

## 日本語

### 概要

TACZ Attributes Addonは、[TACZ (Timeless and Classics Zero)](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero)と[TACZ Attributes](https://www.curseforge.com/minecraft/mc-mods/tacz-attributes)を統合し、銃にRPG風のランダム属性・レアリティシステムを追加します。

銃を入手すると自動的にランダムな属性が付与され、COMMON / UNCOMMON / RARE / EPIC のレアリティが決定されます。同じAK-47でも個体ごとに異なる性能を持つようになります。

### 必須MOD

| MOD | バージョン | 必須 |
|-----|-----------|------|
| [Minecraft Forge](https://files.minecraftforge.net/) | 47.4.16+ | 必須 |
| [TACZ](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero) | 1.0+ | 必須 |
| [TACZ Attributes](https://www.curseforge.com/minecraft/mc-mods/tacz-attributes) | 1.0+ | 必須 |
| [Apotheosis](https://www.curseforge.com/minecraft/mc-mods/apotheosis) | — | 任意 |

### 機能一覧

#### Feature 1: 銃取得時のランダム属性付与
銃を入手すると自動的にランダムな属性が付与されます。ダメージ倍率、リコイル軽減、リロード速度など約40種類の属性プールからランダムに選ばれます。

4つのランダムモードから選択可能:
- **FULL_RANDOM** — 全属性から完全ランダム
- **ADAPTIVE** — 銃タイプに応じた属性のみ（SMGにスナイパー属性が付かない等）
- **RARITY_ADAPTIVE** — 適応型 + レアリティによる重み付け + 値のスキュー
- **BALANCED** — 適応型 + バフとデバフの比率を自動調整

#### Feature 2: 銃モデル別の固定属性
JSONファイルで銃IDごとに固定属性を設定可能。例えば「全てのAK-47はダメージ+5%」のような設定ができます。

固定属性とランダム属性の関係は4モードから設定:
- **FIXED_ONLY** — 固定属性のみ
- **RANDOM_ONLY** — ランダムのみ
- **BOTH_STACKING** — 両方を独立して適用（デフォルト）
- **FIXED_INFLUENCES_RANDOM** — 固定属性がランダム生成に影響

#### Feature 3: Attribute Station（属性付与ブロック）
専用ブロックに銃をセットして属性をランダム生成/リロールできます。

- 加工時間はコンフィグで設定可能（デフォルト10秒）
- アイテム消費の有無もコンフィグで設定可能
- 消費アイテムの種類と数量も変更可能

#### Feature 6: レアリティスコアリング
付与された属性のスコアに基づいてアイテムのレアリティが決定されます。

| レアリティ | スコア閾値（デフォルト） | 色 |
|-----------|----------------------|-----|
| COMMON | 0〜99 | 白 |
| UNCOMMON | 100〜299 | 黄 |
| RARE | 300〜599 | 水色 |
| EPIC | 600以上 | 紫 |

閾値はコンフィグで自由に変更可能です。

### インストール

1. Minecraft Forge 1.20.1 (47.4.16+) をインストール
2. TACZ、TACZ Attributesをmodsフォルダに配置
3. 本MODの`.jar`ファイルをmodsフォルダに配置
4. ゲームを起動

### コンフィグ

初回起動後、`config/tacz_attributes_addon-common.toml` に設定ファイルが生成されます。

<details>
<summary>主な設定項目</summary>

```toml
[general]
enableRandomOnObtain = true      # 取得時にランダム属性を自動付与
enableWeaponTypeAttributes = true # 銃モデル別固定属性を有効化
enableAttributeStation = true     # Attribute Stationブロックを有効化
enableApotheosis = true           # Apotheosis連携を有効化
enableRarityScoring = true        # レアリティスコアリングを有効化

[random]
randomMode = "RARITY_ADAPTIVE"    # ランダムモード
fixedAttributeMode = "BOTH_STACKING" # 固定属性モード
minAttributes = 1                 # 最小属性数
maxAttributes = 4                 # 最大属性数
valueDistribution = "EXPONENTIAL" # 値分布曲線
distributionExponent = 2.0        # 分布指数
raritySpreadFactor = 2.0          # レアリティ分散係数
buffDebuffRatio = 1.0             # バフ/デバフ比率（BALANCEDモード用）

[rarity]
uncommonThreshold = 100           # UNCOMMONの閾値
rareThreshold = 300               # RAREの閾値
epicThreshold = 600               # EPICの閾値

[station]
stationProcessingTime = 200       # 加工時間（tick、20tick=1秒）
stationConsumeItem = false        # アイテムを消費するか
stationConsumeItemId = "minecraft:diamond" # 消費アイテムID
stationConsumeCount = 1           # 消費数
stationAllowReroll = true         # 既に属性がある銃のリロールを許可
```

</details>

### 銃モデル別固定属性の設定

`config/tacz_attributes_addon/weapon_attributes.json` を編集して銃IDごとの固定属性を設定します（初回起動で空ファイルが生成されます）。

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
      "value": 0.10,
      "operation": "MULTIPLY_BASE"
    }
  ],
  "tacz:m4a1": [
    {
      "attribute": "tacz_attributes:ads_accuracy",
      "value": 0.08,
      "operation": "MULTIPLY_BASE"
    }
  ]
}
```

### 属性リスト

<details>
<summary>利用可能な属性一覧（クリックして展開）</summary>

**ダメージ系**
- `gun_damage` — 銃ダメージ倍率
- `headshot_multiplier` — ヘッドショット倍率（スナイパー/ライフル）

**精度系**
- `hip_fire_accuracy` — 腰撃ち精度
- `ads_accuracy` — ADS精度

**リコイル系**
- `recoil` — 総合リコイル
- `vertical_recoil` — 垂直リコイル
- `horizontal_recoil` — 水平リコイル

**速度系**
- `reload_speed` — リロード速度
- `ads_speed` — ADS速度
- `draw_speed` — 取り出し速度
- `bolt_action_speed` — ボルトアクション速度
- `rpm_multiplier` — 発射レート倍率
- `gun_movement_speed` — 銃装備時移動速度

**マガジン・弾薬系**
- `magazine_capacity` — マガジン容量
- `ammo_save_chance` — 弾薬節約確率
- `reload_ammo_save_chance` — リロード時弾薬節約
- `bonus_ammo_chance` — ボーナス弾薬確率
- `bonus_ammo_amount` — ボーナス弾薬量

**キル回復系**
- `ammo_recovery_chance` — キル時弾薬回復確率
- `ammo_recovery_amount` — キル時弾薬回復量
- `ammo_recovery_percent` — キル時弾薬回復割合

**戦闘系**
- `knockback_multiplier` — ノックバック倍率
- `knockback_base` — 基礎ノックバック
- `pierce_multiplier` — 貫通倍率

**射撃モード別**
- `auto_damage`, `auto_accuracy`, `auto_bullet_amount` — フルオート
- `semi_damage`, `semi_accuracy`, `semi_bullet_amount` — セミオート
- `burst_damage`, `burst_accuracy`, `burst_speed` — バースト

**姿勢別**
- `hip_fire_damage` — 腰撃ちダメージ（ピストル/ショットガン/SMG）
- `ads_damage` — ADSダメージ（スナイパー/ライフル）

全ての属性名には `tacz_attributes:` プレフィックスが付きます。

</details>

### ライセンス

MIT License

---

## English

### Overview

TACZ Attributes Addon integrates [TACZ (Timeless and Classics Zero)](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero) with [TACZ Attributes](https://www.curseforge.com/minecraft/mc-mods/tacz-attributes) to add an RPG-style random attribute and rarity system to guns.

When you obtain a gun, it automatically receives random attributes and a rarity tier (COMMON / UNCOMMON / RARE / EPIC). Each individual gun of the same model can have different stats.

### Requirements

| Mod | Version | Required |
|-----|---------|----------|
| [Minecraft Forge](https://files.minecraftforge.net/) | 47.4.16+ | Required |
| [TACZ](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero) | 1.0+ | Required |
| [TACZ Attributes](https://www.curseforge.com/minecraft/mc-mods/tacz-attributes) | 1.0+ | Required |
| [Apotheosis](https://www.curseforge.com/minecraft/mc-mods/apotheosis) | — | Optional |

### Features

#### Feature 1: Random Attributes on Gun Obtain
Guns automatically receive random attributes when obtained. Attributes are drawn from a pool of ~40 types including damage multipliers, recoil reduction, reload speed, and more.

Four random modes available:
- **FULL_RANDOM** — Fully random from entire pool
- **ADAPTIVE** — Filtered by gun type (no sniper attributes on SMGs, etc.)
- **RARITY_ADAPTIVE** — Adaptive + rarity-weighted selection + value skewing
- **BALANCED** — Adaptive + automatic buff/debuff ratio balancing

#### Feature 2: Per-Weapon Fixed Attributes
Configure fixed attributes per gun ID via JSON. For example, "all AK-47s get +5% damage."

#### Feature 3: Attribute Station Block
A dedicated block where players can generate or reroll gun attributes. Processing time, item consumption, and other settings are fully configurable.

#### Feature 6: Rarity Scoring
Item rarity is determined by the total attribute score.

| Rarity | Score Threshold (Default) | Color |
|--------|--------------------------|-------|
| COMMON | 0–99 | White |
| UNCOMMON | 100–299 | Yellow |
| RARE | 300–599 | Aqua |
| EPIC | 600+ | Purple |

### Installation

1. Install Minecraft Forge 1.20.1 (47.4.16+)
2. Place TACZ and TACZ Attributes in the mods folder
3. Place this mod's `.jar` file in the mods folder
4. Launch the game

### Configuration

After first launch, a config file is generated at `config/tacz_attributes_addon-common.toml`. All features can be toggled on/off, and parameters such as random mode, attribute counts, value distributions, rarity thresholds, and station settings are fully configurable.

Per-weapon fixed attributes are configured in `config/tacz_attributes_addon/weapon_attributes.json`.

### License

MIT License
