[Home](Home) > 속성 풀 설정

[English](Attribute-Pool) | [日本語](Attribute-Pool-ja) | [中文](Attribute-Pool-cn) | **한국어**

---

## 속성 풀 설정 (attribute_pool.json)

파일: `config/tacz_attributes_addon/attribute_pool.json`

첫 실행 시 기본 속성 풀을 포함한 JSON 파일이 자동 생성됩니다.
각 속성의 출현율, 값 범위, 희귀도 점수 등을 자유롭게 커스터마이즈할 수 있습니다.

### 형식

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

### 각 필드 설명

| 필드 | 설명 | 예시 |
|------|------|------|
| `attributeId` | TACZ Attributes의 속성 ID | `"tacz_attributes:gun_damage"` |
| `minValue` | 랜덤 생성의 최솟값 | `-0.20` (= -20%) |
| `maxValue` | 랜덤 생성의 최댓값 | `0.30` (= +30%) |
| `operation` | 연산자 (`MULTIPLY_BASE`, `ADDITION`, `MULTIPLY_TOTAL`) | `"MULTIPLY_BASE"` |
| `weight` | 선택 빈도 (클수록 나오기 쉬움) | `20` |
| `rarityTier` | 희귀도 티어 (1=보통, 2=약간 드묾, 3=레어, 4=매우 드묾) | `1` |
| `applicableGunTypes` | 대상 총기 유형 (빈 배열=전체). `pistol`, `sniper`, `rifle`, `shotgun`, `smg`, `rpg`, `mg` | `["rifle", "smg"]` |
| `buffThreshold` | 이 값 이상이 버프 (보통 0.0) | `0.0` |
| `scoreWeight` | 희귀도 점수 기여도 (마이너스=반동 같은 반전 속성) | `100` 또는 `-90` |
| `linkedAttribute` | (선택) 쌍으로 생성해야 할 파트너 속성 ID. 설정하면 이 속성이 선택될 때 파트너도 자동 추가됨 | `"tacz_attributes:ammo_recovery_amount"` |
| `weaponBlacklist` | (선택) 이 속성을 제외할 무기 ID 목록. 지정된 무기에는 절대 부여되지 않음 | `["tacz:rpg7"]` |
| `weaponWhitelist` | (선택) 이 속성을 추가로 허용할 무기 ID 목록. `applicableGunTypes`와 관계없이 허용됨 | `["tacz:desert_eagle"]` |

### 무기별 필터링 (weaponBlacklist / weaponWhitelist)

`applicableGunTypes`가 무기 **종류** (pistol, rifle 등)로 필터링하는 반면, `weaponBlacklist`와 `weaponWhitelist`는 개별 무기 **ID** (tacz:ak47 등)로 제어합니다.

**필터 판정 순서:**
1. `weaponBlacklist`에 포함됨 → **항상 제외** (최우선)
2. `weaponWhitelist`가 비어있지 않고 포함됨 → **항상 허용** (`applicableGunTypes`와 관계없이)
3. `applicableGunTypes` → 기존대로 무기 종류 체크

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

위 예시:
- `tacz:rpg7` → 블랙리스트로 제외
- `tacz:desert_eagle` → pistol이지만 화이트리스트로 허용
- 스나이퍼 또는 라이플 유형 총기 → `applicableGunTypes`로 허용
- 그 외 (샷건, SMG 등) → 불가

> **주의:** 두 필드 모두 생략 가능합니다. 생략 시 필터 없음 (기존 동작과 동일).
> `FULL_RANDOM` 모드에서도 블랙리스트/화이트리스트는 적용됩니다.

### 연결 속성 (쌍 생성)

`linkedAttribute` 필드를 사용하면 속성의 쌍 생성을 설정할 수 있습니다. 예를 들어 `ammo_recovery_chance`가 선택되면 `ammo_recovery_amount`도 자동으로 함께 부여됩니다.

기본 연결 쌍:

| 속성 | 연결 대상 | 설명 |
|------|-----------|------|
| `ammo_recovery_chance` | `ammo_recovery_amount` | 탄약 회복 확률 → 수량 |
| `ammo_recovery_amount` | `ammo_recovery_chance` | 탄약 회복 수량 → 확률 |
| `ammo_recovery_percent` | `ammo_recovery_chance` | 탄약 회복 비율 → 확률 |
| `bonus_ammo_chance` | `bonus_ammo_amount` | 추가 탄약 확률 → 수량 |
| `bonus_ammo_amount` | `bonus_ammo_chance` | 추가 탄약 수량 → 확률 |

### 커스터마이즈 예시

새 속성 추가:
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

커스텀 연결 속성:
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

> **주의:** `attributeId`는 TACZ Attributes MOD에 등록된 속성 ID와 일치시켜야 합니다.
> 파일 편집 후 게임 내에서 `/taczaddon reload` 명령어로 리로드할 수 있습니다.

### 속성 그룹 (배타 제어)

`attributeGroups` 필드를 사용하면 유사한 속성을 그룹화하고, 같은 그룹에서 하나의 총기에 동시에 나타날 수 있는 속성 수를 제한할 수 있습니다. 이를 통해 비슷한 유형의 속성이 대량으로 중복되는 것을 방지할 수 있습니다 (예: 여러 데미지 속성이 동시에 출현).

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

| 필드 | 설명 |
|------|------|
| `name` | 그룹 이름 (식별용) |
| `maxFromGroup` | 이 그룹에서 하나의 총기에 동시에 부여될 수 있는 최대 속성 수 |
| `attributes` | 그룹에 포함되는 속성 ID 목록 |

위 예시:
- **damage 그룹**: 데미지 관련 4개 속성 중 최대 1개만 출현 가능
- **recoil 그룹**: 반동 관련 3개 속성 중 최대 2개까지 출현 가능

> **참고:** 하나의 속성을 여러 그룹에 포함시킬 수 있으며, 이 경우 가장 제한이 엄격한 그룹이 우선됩니다. `attributeGroups`를 생략하거나 빈 배열로 설정하면 제한 없음 (기존 동작과 동일). 연결 속성 (쌍 생성)에 의한 추가는 그룹 제한의 대상이 아닙니다.

---

[< 이전: 고정 속성](Weapon-Attributes-kr) | [다음 >: 총기별 오버라이드](Gun-Attribute-Overrides-kr)
