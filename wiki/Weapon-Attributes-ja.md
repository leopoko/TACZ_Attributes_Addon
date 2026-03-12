[Home](Home) > 銃モデル別固定属性

[English](Weapon-Attributes) | **日本語** | [中文](Weapon-Attributes-cn) | [한국어](Weapon-Attributes-kr)

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

[< 前へ: Enhancement Station](Enhancement-Station-ja) | [次へ >: 属性プール](Attribute-Pool-ja)
