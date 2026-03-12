[Home](Home) > 属性池配置

[English](Attribute-Pool) | [日本語](Attribute-Pool-ja) | **中文** | [한국어](Attribute-Pool-kr)

---

## 属性池配置（attribute_pool.json）

文件：`config/tacz_attributes_addon/attribute_pool.json`

首次启动时会自动生成包含默认属性池的 JSON 文件。
可以自由自定义每个属性的出现率、数值范围、稀有度评分等。

### 格式

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

### 各字段说明

| 字段 | 说明 | 示例 |
|------|------|------|
| `attributeId` | TACZ Attributes 的属性 ID | `"tacz_attributes:gun_damage"` |
| `minValue` | 随机生成的最小值 | `-0.20`（= -20%） |
| `maxValue` | 随机生成的最大值 | `0.30`（= +30%） |
| `operation` | 运算符（`MULTIPLY_BASE`、`ADDITION`、`MULTIPLY_TOTAL`） | `"MULTIPLY_BASE"` |
| `weight` | 选择频率（越大越容易出现） | `20` |
| `rarityTier` | 稀有度等级（1=普通，2=较少见，3=稀有，4=极稀有） | `1` |
| `applicableGunTypes` | 适用枪械类型（空=全部）。可选值：`pistol`、`sniper`、`rifle`、`shotgun`、`smg`、`rpg`、`mg` | `["rifle", "smg"]` |
| `buffThreshold` | 该值以上视为增益（通常为 0.0） | `0.0` |
| `scoreWeight` | 对稀有度评分的贡献度（负数 = 后坐力等反转属性） | `100` 或 `-90` |
| `linkedAttribute` | （可选）必须成对选择的伙伴属性 ID。设置后，选中此属性时会自动添加伙伴属性。 | `"tacz_attributes:ammo_recovery_amount"` |
| `weaponBlacklist` | （可选）始终排除此属性的武器 ID 列表。列出的武器绝对不会获得此属性。 | `["tacz:rpg7"]` |
| `weaponWhitelist` | （可选）额外允许此属性的武器 ID 列表。无论 `applicableGunTypes` 如何，列出的武器都会被允许。 | `["tacz:desert_eagle"]` |

### 按武器过滤（weaponBlacklist / weaponWhitelist）

`applicableGunTypes` 按武器**类型**（pistol、rifle 等）进行过滤，而 `weaponBlacklist` 和 `weaponWhitelist` 则按单个武器 **ID**（如 tacz:ak47）进行精细控制。

**过滤优先级：**
1. `weaponBlacklist` 中包含该武器 → **始终排除**（最高优先级）
2. `weaponWhitelist` 非空且包含该武器 → **始终允许**（无论 `applicableGunTypes` 如何）
3. `applicableGunTypes` → 与之前相同的武器类型检查

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

以上示例：
- `tacz:rpg7` → 被黑名单排除
- `tacz:desert_eagle` → 虽然是手枪，但被白名单允许
- 任何狙击枪或步枪 → 通过 `applicableGunTypes` 允许
- 其他类型（霰弹枪、冲锋枪等） → 不允许

> **注意：** 两个字段都是可选的。省略时不进行武器级过滤（保持默认行为）。
> 即使在 `FULL_RANDOM` 模式下，黑名单/白名单过滤也会生效。

### 关联属性（成对生成）

`linkedAttribute` 字段可设置属性的成对生成。例如，当 `ammo_recovery_chance` 被选中时，`ammo_recovery_amount` 也会自动添加。

默认关联对：

| 属性 | 关联到 | 说明 |
|------|--------|------|
| `ammo_recovery_chance` | `ammo_recovery_amount` | 回复概率 → 回复数量 |
| `ammo_recovery_amount` | `ammo_recovery_chance` | 回复数量 → 回复概率 |
| `ammo_recovery_percent` | `ammo_recovery_chance` | 回复比例 → 回复概率 |
| `bonus_ammo_chance` | `bonus_ammo_amount` | 额外弹药概率 → 数量 |
| `bonus_ammo_amount` | `bonus_ammo_chance` | 额外弹药数量 → 概率 |

### 自定义示例

添加新属性：
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

自定义关联属性：
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

> **注意：** `attributeId` 必须与 TACZ Attributes MOD 中注册的属性 ID 完全一致。
> 编辑文件后，可在游戏内使用 `/taczaddon reload` 命令重新加载。

### 属性分组（互斥控制）

`attributeGroups` 字段可将相似属性分组，并限制同一分组中同时出现在一把枪上的属性数量。这可以防止大量相似类型的属性堆叠（例如多个伤害属性同时出现）。

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

| 字段 | 说明 |
|------|------|
| `name` | 分组名称（用于标识） |
| `maxFromGroup` | 此分组中最多可同时出现在一把枪上的属性数量 |
| `attributes` | 分组中包含的属性 ID 列表 |

上述示例：
- **damage 分组**：4个伤害类属性中最多只能出现1个
- **recoil 分组**：3个后坐力属性中最多可出现2个

> **注意：** 一个属性可以属于多个分组，此时最严格的限制生效。省略 `attributeGroups` 或使用空数组表示无限制（与旧版兼容）。关联属性（成对生成）不受分组限制影响。

---

[< 上一页: 固定属性](Weapon-Attributes-cn) | [下一页 >: 枪械覆盖](Gun-Attribute-Overrides-cn)
