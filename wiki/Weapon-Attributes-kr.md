[Home](Home) > 총기 모델별 고정 속성

[English](Weapon-Attributes) | [日本語](Weapon-Attributes-ja) | [中文](Weapon-Attributes-cn) | **한국어**

---

## 총기 모델별 고정 속성 (weapon_attributes.json)

파일: `config/tacz_attributes_addon/weapon_attributes.json`

첫 실행 시 빈 JSON 파일 `{}`이 생성됩니다. 총기 ID별로 고정 속성을 설정할 수 있습니다.

### 형식

```json
{
  "총기ID": [
    {
      "attribute": "속성ID",
      "value": 수치,
      "operation": "연산자"
    }
  ]
}
```

### 연산자 (operation)

| 연산자 | 설명 | 예시 |
|--------|------|------|
| `MULTIPLY_BASE` | 기본값에 대한 곱연산. 0.10 = +10% | 대부분의 속성에 사용 |
| `ADDITION` | 기본값에 가산 | knockback_base, ammo_recovery_amount 등 |
| `MULTIPLY_TOTAL` | 최종값에 대한 곱연산 | 특수한 경우에만 |

### 설정 예시

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

> **팁:** 총기 ID는 `tacz:총기명` 형식입니다. TACZ 데이터팩에서 정의된 총기 ID와 일치시켜 주세요.

---

| | |
|:---|---:|
| [이전: Enhancement Station](Enhancement-Station-kr) | [다음: 속성 풀](Attribute-Pool-kr) |
