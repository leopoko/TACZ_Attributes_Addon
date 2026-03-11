# TACZ Attributes Addon

**Minecraft 1.20.1 Forge MOD** — TACZ（銃MOD）の銃アイテムにランダム属性とレアリティシステムを追加するアドオン。

> [English](#english) | [日本語](#日本語) | [中文](#中文)

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
- クラフトレシピ: 鉄インゴット×5 + ダイヤモンド + 木製ハーフブロック×3

#### Feature 4: Apotheosis ソケット統合（任意）
Apotheosis MODがインストールされている場合、銃にソケットスロットが追加されます。

- ソケット数はレアリティに連動（COMMON=1, UNCOMMON=2, RARE=3, EPIC=4）
- ソケット数の固定値設定も可能
- 銃専用の`gun`カテゴリとして認識

#### Feature 5: 銃専用ジェム（Apotheosis連携）
11種類の銃専用ジェムが追加されます。ジェムをソケットに挿入すると、対応する属性が銃を持っている間プレイヤーに適用されます。

| ジェム | 効果 |
|--------|------|
| マークスマンの宝石 | 銃ダメージ上昇 |
| 安定の宝石 | リコイル軽減 |
| 速装填の宝石 | リロード速度上昇 |
| 精密射撃の宝石 | ヘッドショット倍率上昇 |
| 拡張弾倉の宝石 | マガジン容量増加 |
| 速射の宝石 | 発射レート上昇 |
| 戦術の宝石 | ADS速度+ADS精度（複合効果） |
| 弾薬節約の宝石 | 弾薬節約確率 |
| 弾薬回復の宝石 | 弾薬回復確率+弾薬回復量（複合効果） |
| 追加弾薬の宝石 | 追加弾薬確率+追加弾薬量（複合効果） |
| ノックバックの宝石 | ノックバック基本値増加 |

さらに11種類の銃専用アフィックスも追加され、Apotheosisのリフォージ等で銃に付与されます。

#### Feature 6: レアリティスコアリング
付与された属性のスコアに基づいてアイテムのレアリティが決定されます。

| レアリティ | スコア閾値（デフォルト） | 色 |
|-----------|----------------------|-----|
| COMMON | 0〜99 | 白 |
| UNCOMMON | 100〜299 | 黄 |
| RARE | 300〜599 | 水色 |
| EPIC | 600以上 | 紫 |

閾値はコンフィグで自由に変更可能です。

#### リンク属性（ペア生成）
弾薬回復や追加弾薬のような「確率＋量」で機能する属性ペアは、ランダム生成時に必ずセットで付与されます。この連動関係は `attribute_pool.json` の `linkedAttribute` フィールドでカスタマイズ可能です。

### インストール

1. Minecraft Forge 1.20.1 (47.4.16+) をインストール
2. TACZ、TACZ Attributesをmodsフォルダに配置
3. 本MODの`.jar`ファイルをmodsフォルダに配置
4. ゲームを起動
5. （任意）Apotheosisを導入するとソケット/ジェム機能が有効化

### コンフィグ

初回起動後、`config/tacz_attributes_addon-common.toml` に設定ファイルが生成されます。詳細は [CONFIG_GUIDE.md](docs/CONFIG_GUIDE.md) を参照してください。

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

[apotheosis]
gunBaseSockets = 2                # レアリティ非連動時のデフォルトソケット数
socketsScaleWithRarity = true     # ソケット数をレアリティに連動させるか
commonSockets = 1                 # COMMONのソケット数
uncommonSockets = 2               # UNCOMMONのソケット数
rareSockets = 3                   # RAREのソケット数
epicSockets = 4                   # EPICのソケット数
```

</details>

### 銃別属性オーバーライド（Per-Gun Attribute Overrides）

`config/tacz_attributes_addon/gun_attribute_overrides.json` を編集して、銃IDごとにランダム属性の個数制限・許可属性・値範囲をオーバーライドできます（初回起動で空ファイルが生成されます）。Looter-shooter系モッドパックで銃ごとに異なるビルドを作る際に便利です。

```json
{
  "tacz:hk416d": {
    "minAttributes": 1,
    "maxAttributes": 3,
    "attributes": [
      {"attribute": "tacz_attributes:reload_speed", "minValue": -0.20, "maxValue": 0.20},
      {"attribute": "tacz_attributes:gun_damage", "minValue": -0.10, "maxValue": 0.15}
    ]
  }
}
```

- `minAttributes`/`maxAttributes` — 省略するとグローバル設定を使用
- `minAttributesPos`/`maxAttributesPos`/`minAttributesNeg`/`maxAttributesNeg` — 正負属性の個数を独立制御（スプリットモード）
- `attributes` — 省略すると通常のプールフィルタリングを使用。指定するとそのリストの属性のみがこの銃に付与可能
- 設定のない銃は従来通りの動作
- 各属性エントリに `weight`、`rarityTier`、`scoreWeight`、`operation` も指定可能（省略時は `attribute_pool.json` の値を使用）
- 各属性エントリに `minValuePos`/`maxValuePos`/`minValueNeg`/`maxValueNeg` で正負それぞれの値範囲を個別指定可能

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
- `bonus_ammo_chance` — ボーナス弾薬確率（`bonus_ammo_amount` とペア）
- `bonus_ammo_amount` — ボーナス弾薬量（`bonus_ammo_chance` とペア）

**キル回復系**
- `ammo_recovery_chance` — キル時弾薬回復確率（`ammo_recovery_amount` とペア）
- `ammo_recovery_amount` — キル時弾薬回復量（`ammo_recovery_chance` とペア）
- `ammo_recovery_percent` — キル時弾薬回復割合（`ammo_recovery_chance` とペア）

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

Crafting recipe: 5 iron ingots + 1 diamond + 3 wooden slabs.

#### Feature 4: Apotheosis Socket Integration (Optional)
When Apotheosis is installed, guns gain socket slots for gems.

- Socket count scales with rarity (COMMON=1, UNCOMMON=2, RARE=3, EPIC=4)
- Fixed socket count mode also available
- Recognized as the `gun` loot category

#### Feature 5: Gun-Specific Gems (Apotheosis Integration)
11 gun-specific gems are added. Inserting gems into sockets applies the corresponding attribute to the player while holding the gun.

| Gem | Effect |
|-----|--------|
| Marksman Gem | Gun damage boost |
| Stabilizer Gem | Recoil reduction |
| Quickloader Gem | Reload speed boost |
| Sharpshooter Gem | Headshot multiplier boost |
| Extended Mag Gem | Magazine capacity increase |
| Rapid Fire Gem | Fire rate boost |
| Tactical Gem | ADS speed + ADS accuracy (multi-attribute) |
| Conservation Gem | Ammo save chance |
| Ammo Recovery Gem | Ammo recovery chance + amount (multi-attribute) |
| Bonus Ammo Gem | Bonus ammo chance + amount (multi-attribute) |
| Knockback Gem | Knockback base increase |

Additionally, 11 gun-specific affixes are included for Apotheosis reforging.

#### Feature 6: Rarity Scoring
Item rarity is determined by the total attribute score.

| Rarity | Score Threshold (Default) | Color |
|--------|--------------------------|-------|
| COMMON | 0–99 | White |
| UNCOMMON | 100–299 | Yellow |
| RARE | 300–599 | Aqua |
| EPIC | 600+ | Purple |

#### Linked Attributes (Paired Generation)
Attribute pairs such as ammo recovery (chance + amount) are always generated together during random attribute assignment. This linking is configurable via the `linkedAttribute` field in `attribute_pool.json`.

### Installation

1. Install Minecraft Forge 1.20.1 (47.4.16+)
2. Place TACZ and TACZ Attributes in the mods folder
3. Place this mod's `.jar` file in the mods folder
4. Launch the game
5. (Optional) Install Apotheosis to enable socket/gem features

### Configuration

After first launch, a config file is generated at `config/tacz_attributes_addon-common.toml`. All features can be toggled on/off, and parameters such as random mode, attribute counts, value distributions, rarity thresholds, station settings, and Apotheosis socket counts are fully configurable.

Per-weapon fixed attributes are configured in `config/tacz_attributes_addon/weapon_attributes.json`.

Per-gun attribute overrides (attribute count limits, allowed attributes, custom value ranges, selection weights, rarity tiers, score weights, and operations per gun ID) are configured in `config/tacz_attributes_addon/gun_attribute_overrides.json`. This is ideal for looter-shooter modpacks where each gun needs distinct attribute builds. Supports split positive/negative attribute count control (`minAttributesPos`/`maxAttributesPos`/`minAttributesNeg`/`maxAttributesNeg`) with separate value ranges for buff and debuff rolls.

The random attribute pool is configured in `config/tacz_attributes_addon/attribute_pool.json`, including linked attribute pairs.

See [CONFIG_GUIDE_EN.md](docs/CONFIG_GUIDE_EN.md) for the full configuration reference.

### License

MIT License

---

## 中文

### 概述

TACZ Attributes Addon 整合了 [TACZ (Timeless and Classics Zero)](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero) 与 [TACZ Attributes](https://www.curseforge.com/minecraft/mc-mods/tacz-attributes)，为枪械添加 RPG 风格的随机属性和稀有度系统。

获得枪械时，系统会自动赋予随机属性并决定稀有度（COMMON / UNCOMMON / RARE / EPIC）。同款 AK-47，每把个体的性能都可能有所不同。

### 必需 MOD

| MOD | 版本 | 是否必需 |
|-----|------|----------|
| [Minecraft Forge](https://files.minecraftforge.net/) | 47.4.16+ | 必需 |
| [TACZ](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero) | 1.0+ | 必需 |
| [TACZ Attributes](https://www.curseforge.com/minecraft/mc-mods/tacz-attributes) | 1.0+ | 必需 |
| [Apotheosis](https://www.curseforge.com/minecraft/mc-mods/apotheosis) | — | 可选 |

### 功能列表

#### 功能 1：获得枪械时自动附加随机属性
获得枪械后自动附加随机属性。从约 40 种属性池中随机选取，包括伤害倍率、后坐力减少、换弹速度等。

提供 4 种随机模式可供选择：
- **FULL_RANDOM** — 从全部属性中完全随机
- **ADAPTIVE** — 仅选取适合该枪械类型的属性（如 SMG 不会获得狙击枪专属属性）
- **RARITY_ADAPTIVE** — 自适应 + 稀有度权重 + 数值偏移
- **BALANCED** — 自适应 + 自动调整增益与减益的比率

#### 功能 2：枪型号固定属性
可通过 JSON 文件为每个枪 ID 配置固定属性。例如，"所有 AK-47 的伤害 +5%"。

固定属性与随机属性的关系支持 4 种模式配置：
- **FIXED_ONLY** — 仅使用固定属性
- **RANDOM_ONLY** — 仅使用随机属性
- **BOTH_STACKING** — 两者独立叠加应用（默认）
- **FIXED_INFLUENCES_RANDOM** — 固定属性影响随机生成

#### 功能 3：属性工作台（Attribute Station）
专用方块，可将枪放入其中随机生成/重掷属性。

- 处理时间可通过配置文件设置（默认 10 秒）
- 是否消耗物品同样可在配置中设置
- 消耗物品的种类和数量均可自定义
- 合成配方：铁锭×5 + 钻石 + 木质台阶×3

#### 功能 4：Apotheosis 插槽集成（可选）
安装 Apotheosis MOD 后，枪械将获得宝石插槽。

- 插槽数量随稀有度变化（COMMON=1, UNCOMMON=2, RARE=3, EPIC=4）
- 也可设置为固定插槽数量
- 被识别为 `gun` 战利品类别

#### 功能 5：枪械专用宝石（Apotheosis 联动）
新增 11 种枪械专用宝石。将宝石插入插槽后，持枪时对应属性将应用于玩家。

| 宝石 | 效果 |
|------|------|
| 神射手宝石 | 枪械伤害提升 |
| 稳定宝石 | 后坐力降低 |
| 快速装填宝石 | 换弹速度提升 |
| 精确射击宝石 | 爆头倍率提升 |
| 扩容弹匣宝石 | 弹匣容量增加 |
| 速射宝石 | 射速提升 |
| 战术宝石 | 瞄准速度+瞄准精度（复合效果） |
| 节约宝石 | 弹药节省概率 |
| 弹药回复宝石 | 弹药回复概率+弹药回复量（复合效果） |
| 额外弹药宝石 | 额外弹药概率+额外弹药量（复合效果） |
| 击退宝石 | 击退基础值增加 |

此外还包含 11 种枪械专用词缀，可通过 Apotheosis 的锻造系统附加到枪械上。

#### 功能 6：稀有度评分
根据附加属性的总评分决定物品稀有度。

| 稀有度 | 评分阈值（默认） | 颜色 |
|--------|----------------|------|
| COMMON | 0～99 | 白色 |
| UNCOMMON | 100～299 | 黄色 |
| RARE | 300～599 | 青色 |
| EPIC | 600 以上 | 紫色 |

阈值可在配置文件中自由修改。

#### 关联属性（成对生成）
弹药回复（概率+数量）等"概率+数量"类属性对在随机生成时会始终成对出现。此关联关系可通过 `attribute_pool.json` 的 `linkedAttribute` 字段自定义配置。

### 安装方法

1. 安装 Minecraft Forge 1.20.1（47.4.16+）
2. 将 TACZ 和 TACZ Attributes 放入 mods 文件夹
3. 将本 MOD 的 `.jar` 文件放入 mods 文件夹
4. 启动游戏
5. （可选）安装 Apotheosis 以启用插槽/宝石功能

### 配置

首次启动后，配置文件将生成于 `config/tacz_attributes_addon-common.toml`。

<details>
<summary>主要配置项</summary>

```toml
[general]
enableRandomOnObtain = true       # 获得枪械时自动附加随机属性
enableWeaponTypeAttributes = true # 启用枪型号固定属性
enableAttributeStation = true     # 启用属性工作台方块
enableApotheosis = true           # 启用 Apotheosis 联动
enableRarityScoring = true        # 启用稀有度评分

[random]
randomMode = "RARITY_ADAPTIVE"    # 随机模式
fixedAttributeMode = "BOTH_STACKING" # 固定属性模式
minAttributes = 1                 # 最小属性数量
maxAttributes = 4                 # 最大属性数量
valueDistribution = "EXPONENTIAL" # 数值分布曲线
distributionExponent = 2.0        # 分布指数
raritySpreadFactor = 2.0          # 稀有度分散系数
buffDebuffRatio = 1.0             # 增益/减益比率（BALANCED 模式专用）

[rarity]
uncommonThreshold = 100           # UNCOMMON 阈值
rareThreshold = 300               # RARE 阈值
epicThreshold = 600               # EPIC 阈值

[station]
stationProcessingTime = 200       # 处理时间（tick，20 tick = 1 秒）
stationConsumeItem = false        # 是否消耗物品
stationConsumeItemId = "minecraft:diamond" # 消耗物品 ID
stationConsumeCount = 1           # 消耗数量
stationAllowReroll = true         # 允许对已有属性的枪重掷

[apotheosis]
gunBaseSockets = 2                # 固定插槽数（socketsScaleWithRarity 为 false 时使用）
socketsScaleWithRarity = true     # 插槽数是否随稀有度变化
commonSockets = 1                 # COMMON 插槽数
uncommonSockets = 2               # UNCOMMON 插槽数
rareSockets = 3                   # RARE 插槽数
epicSockets = 4                   # EPIC 插槽数
```

</details>

详细配置说明请参阅 [CONFIG_GUIDE_CN.md](docs/CONFIG_GUIDE_CN.md)。

### 枪械属性覆盖（Per-Gun Attribute Overrides）

编辑 `config/tacz_attributes_addon/gun_attribute_overrides.json` 可为每个枪 ID 单独设置随机属性的数量限制、允许的属性类型和自定义数值范围（首次启动时自动生成空文件）。适用于类似 The Division 2 的 Looter-shooter 风格模组包。

```json
{
  "tacz:hk416d": {
    "minAttributes": 1,
    "maxAttributes": 3,
    "attributes": [
      {"attribute": "tacz_attributes:reload_speed", "minValue": -0.20, "maxValue": 0.20},
      {"attribute": "tacz_attributes:gun_damage", "minValue": -0.10, "maxValue": 0.15}
    ]
  }
}
```

- `minAttributes`/`maxAttributes` — 省略时使用全局设置
- `minAttributesPos`/`maxAttributesPos`/`minAttributesNeg`/`maxAttributesNeg` — 独立控制正负属性数量（分离模式）
- `attributes` — 省略时使用常规属性池过滤。指定后，仅列出的属性可出现在该枪上
- 未配置的枪继续使用原有行为
- 各属性条目还可指定 `weight`、`rarityTier`、`scoreWeight`、`operation`（省略时使用 `attribute_pool.json` 的值）
- 各属性条目还可指定 `minValuePos`/`maxValuePos`/`minValueNeg`/`maxValueNeg` 分别设置正负值范围

### 枪型号固定属性配置

编辑 `config/tacz_attributes_addon/weapon_attributes.json` 为各枪 ID 设置固定属性（首次启动时自动生成空文件）。

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

### 属性列表

<details>
<summary>可用属性一览（点击展开）</summary>

**伤害类**
- `gun_damage` — 枪械伤害倍率
- `headshot_multiplier` — 爆头倍率（狙击枪/步枪）

**精准度类**
- `hip_fire_accuracy` — 腰射精准度
- `ads_accuracy` — 瞄准精准度

**后坐力类**
- `recoil` — 综合后坐力
- `vertical_recoil` — 垂直后坐力
- `horizontal_recoil` — 水平后坐力

**速度类**
- `reload_speed` — 换弹速度
- `ads_speed` — 瞄准速度
- `draw_speed` — 出枪速度
- `bolt_action_speed` — 栓动速度
- `rpm_multiplier` — 射速倍率
- `gun_movement_speed` — 持枪移动速度

**弹匣与弹药类**
- `magazine_capacity` — 弹匣容量
- `ammo_save_chance` — 弹药节省概率
- `reload_ammo_save_chance` — 换弹时弹药节省
- `bonus_ammo_chance` — 额外弹药概率（与 `bonus_ammo_amount` 成对）
- `bonus_ammo_amount` — 额外弹药数量（与 `bonus_ammo_chance` 成对）

**击杀回复类**
- `ammo_recovery_chance` — 击杀时弹药回复概率（与 `ammo_recovery_amount` 成对）
- `ammo_recovery_amount` — 击杀时弹药回复数量（与 `ammo_recovery_chance` 成对）
- `ammo_recovery_percent` — 击杀时弹药回复比例（与 `ammo_recovery_chance` 成对）

**战斗类**
- `knockback_multiplier` — 击退倍率
- `knockback_base` — 基础击退
- `pierce_multiplier` — 穿透倍率

**射击模式别**
- `auto_damage`、`auto_accuracy`、`auto_bullet_amount` — 全自动
- `semi_damage`、`semi_accuracy`、`semi_bullet_amount` — 半自动
- `burst_damage`、`burst_accuracy`、`burst_speed` — 点射

**姿势别**
- `hip_fire_damage` — 腰射伤害（手枪/霰弹枪/SMG）
- `ads_damage` — 瞄准伤害（狙击枪/步枪）

所有属性名称均带有 `tacz_attributes:` 前缀。

</details>

### 许可证

MIT License
