[Home](Home) > 枪型号固定属性

[English](Weapon-Attributes) | [日本語](Weapon-Attributes-ja) | **中文** | [한국어](Weapon-Attributes-kr)

---

## 枪型号固定属性（weapon_attributes.json）

文件：`config/tacz_attributes_addon/weapon_attributes.json`

首次启动时会生成一个空的 JSON 文件 `{}`，可以为每个枪 ID 配置固定属性。

### 格式

```json
{
  "枪ID": [
    {
      "attribute": "属性ID",
      "value": 数值,
      "operation": "运算符"
    }
  ]
}
```

### 运算符（operation）

| 运算符 | 说明 | 示例 |
|--------|------|------|
| `MULTIPLY_BASE` | 对基础值进行乘算。0.10 = +10% | 大多数属性使用此运算符 |
| `ADDITION` | 加到基础值上 | knockback_base、ammo_recovery_amount 等 |
| `MULTIPLY_TOTAL` | 对最终值进行乘算 | 仅用于特殊情况 |

### 配置示例

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

> **提示：** 枪 ID 格式为 `tacz:枪名`。请确保与 TACZ 数据包中定义的枪 ID 完全一致。

---

[上一页: Enhancement Station](Enhancement-Station-cn) | [下一页: 属性池配置](Attribute-Pool-cn)
