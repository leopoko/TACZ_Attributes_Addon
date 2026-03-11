# TACZ Attributes Addon - 설정 상세 가이드

설정 파일: `config/tacz_attributes_addon-common.toml` (게임 첫 실행 시 자동 생성)

---

## [general] 기능 토글

각 기능을 개별적으로 활성화/비활성화할 수 있습니다.

### `enableRandomOnObtain` (기본값: `true`)
총기를 인벤토리에 넣을 때 자동으로 랜덤 속성을 부여합니다.

- `true`: 플레이어가 총기 아이템을 획득하면 랜덤 속성이 자동 부여됨
- `false`: 자동 부여하지 않음 (속성 작업대에서 수동 부여만 가능)

> **주의:** 비활성화해도 이미 속성이 부여된 총기에는 영향이 없습니다. 새로 획득하는 총기에만 적용됩니다.

### `enableWeaponTypeAttributes` (기본값: `true`)
총기 모델별 고정 속성을 활성화합니다. 고정 속성은 `config/tacz_attributes_addon/weapon_attributes.json`에서 설정합니다.

- `true`: weapon_attributes.json 설정에 따라 고정 속성을 적용
- `false`: 고정 속성을 일체 적용하지 않음

### `enableAttributeStation` (기본값: `true`)
속성 작업대 블록의 기능을 활성화합니다.

- `true`: 블록에 총기를 넣어 속성 부여/리롤 가능
- `false`: 블록은 설치 가능하지만 가공 처리가 수행되지 않음

### `enableApotheosis` (기본값: `true`)
Apotheosis MOD와의 연동 기능을 활성화합니다. Apotheosis가 설치된 경우 총기에 소켓 슬롯이 추가되어 총기 전용 보석 삽입이 가능해집니다.

- `true`: Apotheosis가 존재할 경우 소켓/보석 통합을 활성화
- `false`: Apotheosis가 존재해도 소켓/보석 기능을 비활성화

### `enableRarityScoring` (기본값: `true`)
속성 기반 희귀도 스코어링 시스템을 활성화합니다.

- `true`: 부여된 속성의 값과 scoreWeight로 점수를 계산하여 희귀도(COMMON/UNCOMMON/RARE/EPIC)를 결정. 총기 아이템의 이름 색상이 희귀도에 따라 변경됨
- `false`: 희귀도 계산을 건너뜀. 모든 총기가 COMMON으로 표시

### `showEmptySlots` (기본값: `false`)
총기의 랜덤 속성 수가 `maxAttributes`보다 적을 때 툴팁에 빈 속성 슬롯을 표시합니다.

- `true`: 미사용 슬롯을 `[ ] Empty Attribute Slot`으로 표시. 총기별 `maxAttributes` 오버라이드가 있으면 해당 값 사용, 없으면 글로벌 값 사용
- `false`: 빈 슬롯 표시 없음

---

## [random] 랜덤 속성 생성

랜덤 속성 생성 알고리즘을 세밀하게 조정할 수 있습니다.

### `randomMode` (기본값: `RARITY_ADAPTIVE`)

랜덤 속성의 선택 알고리즘.

| 모드 | 설명 | 권장 용도 |
|------|------|-----------|
| `FULL_RANDOM` | 전체 속성 풀에서 완전 랜덤. 총기 유형을 고려하지 않음. SMG에 스나이퍼 전용 속성이 붙을 수 있음 | 캐주얼 / 카오스 |
| `ADAPTIVE` | 총기 유형과 사격 모드로 속성을 필터링. 해당하지 않는 속성은 선택되지 않음 | 밸런스 중시 |
| `RARITY_ADAPTIVE` | ADAPTIVE + 희귀도 가중치. 높은 희귀도 속성일수록 선택되기 어려움 + 값 편향(낮은 값이 나오기 쉬움) | **권장 (기본값)** |
| `BALANCED` | RARITY_ADAPTIVE + 버프와 디버프 비율을 자동 조정. buffDebuffRatio로 제어 | 공정성 중시 |

### `fixedAttributeMode` (기본값: `BOTH_STACKING`)

고정 속성(weapon_attributes.json)과 랜덤 속성의 관계.

| 모드 | 고정 속성 | 랜덤 속성 | 설명 |
|------|-----------|-----------|------|
| `FIXED_ONLY` | 적용함 | **생성하지 않음** | 고정 속성만. 랜덤성 없음 |
| `RANDOM_ONLY` | **적용하지 않음** | 생성함 | 랜덤만. 고정 설정을 무시 |
| `BOTH_STACKING` | 적용함 | 생성함 | 양쪽을 독립적으로 적용(각각 별도의 NBT 태그에 저장). **권장** |
| `FIXED_INFLUENCES_RANDOM` | 적용함 | 생성함(영향 있음) | 고정 속성이 랜덤 생성의 가중치에 영향을 줌 |

### `minAttributes` (기본값: `1`, 범위: 0~20)
하나의 총기에 부여되는 랜덤 속성의 최소 수. 0으로 설정하면 랜덤 속성이 부여되지 않는 경우가 있음.

### `maxAttributes` (기본값: `4`, 범위: 0~20)
하나의 총기에 부여되는 랜덤 속성의 최대 수. 실제 수는 min~max 사이에서 랜덤으로 결정.

> **예시:** min=2, max=5 → 총기마다 2~5개의 랜덤 속성이 부여됨

### `valueDistribution` (기본값: `EXPONENTIAL`)

속성 값의 분포 곡선. min~max 범위 내에서 값이 어떻게 분포되는지를 제어.

| 분포 | 특성 | 설명 |
|------|------|------|
| `LINEAR` | 균등 분포 | 모든 값이 동일한 확률로 출현 |
| `EXPONENTIAL` | 낮은 값에 편향 | 작은 값이 나오기 쉽고 큰 값은 드묾. 지수: `distributionExponent`로 제어. **권장** |
| `QUADRATIC` | 이차 곡선 | EXPONENTIAL보다 완만하게 낮은 값에 편향 |

### `distributionExponent` (기본값: `2.0`, 범위: 1.0~10.0)
EXPONENTIAL 분포의 지수. 값이 클수록 낮은 값에 편향됨.

- `1.0`: LINEAR과 동일 (편향 없음)
- `2.0`: 적당히 낮은 값 쪽으로 편향 (**권장**)
- `5.0`: 매우 낮은 값에 편향 (높은 값은 거의 나오지 않음)
- `10.0`: 극단적으로 낮은 값에 편향

### `raritySpreadFactor` (기본값: `2.0`, 범위: 1.0~10.0)
RARITY_ADAPTIVE/BALANCED 모드에서 희귀도 티어에 따른 속성 선택의 가중치 분산.

- `1.0`: 모든 희귀도 티어가 동일한 확률로 선택됨
- `2.0`: 높은 희귀도일수록 선택되기 어려움 (**권장**)
- `5.0`: 희귀도 티어 4의 속성은 매우 드묾

> **계산식:** weight = baseWeight / (rarityTier ^ raritySpreadFactor)

### `buffDebuffRatio` (기본값: `1.0`, 범위: 0.1~5.0)
BALANCED 모드 전용. 버프와 디버프의 목표 비율.

- `0.5`: 디버프가 2배 많음 (하드코어용)
- `1.0`: 버프와 디버프가 동수 (**권장**)
- `2.0`: 버프가 2배 많음 (플레이어 유리)

---

## [rarity] 희귀도 점수 임계값

속성의 점수 합계값에 따라 희귀도를 결정합니다.

### 점수 계산 방법

```
점수 = Σ (속성 값 × scoreWeight)
```

- **일반 속성** (데미지 등, scoreWeight=+100): 버프 값이 클수록 높은 점수
- **반전 속성** (반동 등, scoreWeight=-90): 반동 감소(버프)로 높은 점수

### `uncommonThreshold` (기본값: `100`)
점수가 이 값 이상이면 UNCOMMON (노란색).

### `rareThreshold` (기본값: `300`)
점수가 이 값 이상이면 RARE (하늘색).

### `epicThreshold` (기본값: `600`)
점수가 이 값 이상이면 EPIC (보라색).

| 점수 | 희귀도 | 아이템 이름 색상 |
|------|--------|-----------------|
| 0~99 | COMMON | 흰색 |
| 100~299 | UNCOMMON | 노란색 |
| 300~599 | RARE | 하늘색 |
| 600 이상 | EPIC | 보라색 |

> **조정 팁:** 임계값을 낮추면 EPIC이 나오기 쉬워집니다.
> 속성 수와 값 범위에 맞춰 조정하세요.
> 예시: maxAttributes=2이면 임계값을 낮추고, maxAttributes=8이면 임계값을 높이세요.

---

## [station] 속성 작업대 블록

### `processingTime` (기본값: `200`, 범위: 1~72000)
가공에 걸리는 시간 (틱 단위). 20 틱 = 1초.

- `200`: 10초 (**기본값**)
- `100`: 5초 (빠름)
- `1200`: 1분 (느림)
- `72000`: 1시간 (최대)

### `consumeItem` (기본값: `false`)
가공 시 아이템을 소비할지 여부.

- `false`: 아이템 소비 없음. 총기를 넣는 것만으로 가공 (**기본값**)
- `true`: 지정 아이템을 소비하여 가공

### `consumeItemId` (기본값: `"minecraft:diamond"`)
소비할 아이템의 ID. `consumeItem`이 `true`일 때만 유효.

**설정 예시:**
```toml
consumeItemId = "minecraft:diamond"          # 다이아몬드
consumeItemId = "minecraft:netherite_ingot"  # 네더라이트 주괴
consumeItemId = "minecraft:emerald"          # 에메랄드
consumeItemId = "tacz:gunsmith_table"        # TACZ MOD의 아이템
```

### `consumeCount` (기본값: `1`, 범위: 1~64)
1회 가공에 소비하는 아이템 수.

### `allowReroll` (기본값: `true`)
이미 속성이 부여된 총기의 리롤(속성 재생성)을 허용할지 여부.

- `true`: 몇 번이든 리롤 가능 (상한은 `maxRerolls`로 제어)
- `false`: 한 번 속성이 부여된 총기는 재가공 불가

### `maxRerolls` (기본값: `0`, 범위: 0~1000)
하나의 총기에 대한 리롤 횟수 상한.

- `0`: **무제한** (몇 번이든 리롤 가능)
- `3`: 3회까지 리롤 가능
- `10`: 10회까지 리롤 가능

> 리롤 횟수는 툴팁에 표시됩니다.
> 상한에 도달한 총기는 속성 작업대에서 가공할 수 없게 됩니다.

---

## [apotheosis] Apotheosis 연동 설정

Apotheosis MOD가 설치된 경우에 활성화되는 소켓/보석 기능의 설정입니다.

### `gunBaseSockets` (기본값: `2`, 범위: 0~6)
희귀도와 연동하지 않을 경우 (`socketsScaleWithRarity = false`)의 고정 소켓 수.

### `socketsScaleWithRarity` (기본값: `true`)
소켓 수를 희귀도와 연동할지 여부.

- `true`: 희귀도에 따라 소켓 수가 변동 (아래 설정값을 사용)
- `false`: 모든 총기가 `gunBaseSockets`로 지정된 고정 소켓 수를 가짐

### `commonSockets` (기본값: `1`, 범위: 0~6)
COMMON 희귀도 총기의 소켓 수.

### `uncommonSockets` (기본값: `2`, 범위: 0~6)
UNCOMMON 희귀도 총기의 소켓 수.

### `rareSockets` (기본값: `3`, 범위: 0~6)
RARE 희귀도 총기의 소켓 수.

### `epicSockets` (기본값: `4`, 범위: 0~6)
EPIC 희귀도 총기의 소켓 수.

---

## [enhancement] Enhancement Station 블록

### `maxTypes` (기본값: `0`, 범위: 0~100)
총기당 강화 속성 유형의 최대 수. 서로 다른 강화 속성 유형 수가 이 한도에 도달하면, 이미 강화된 속성만 선택지로 표시됩니다(값은 증가하지만 새 유형은 추가되지 않음).

- `0`: 무제한(유형 제한 없음)
- `>0`: 서로 다른 유형 수가 이 값에 도달하면, 선택지가 기존 강화 속성으로 제한됨

> **참고:** 이것은 `maxEnhancements`(총 강화 적용 횟수의 상한)와는 독립적인 설정입니다. `maxTypes`는 속성의 종류를 제어하며, 총 횟수는 제어하지 않습니다.

### `existingOnly` (기본값: `false`)
활성화하면 Enhancement Station의 선택지가 이미 총기에 존재하는 속성으로만 제한됩니다(랜덤 + 고정 + 강화 속성).

- `true`: 총기에 이미 있는 속성만 선택지로 표시됨
- `false`: 풀의 모든 대상 속성이 선택지로 표시됨

> **참고:** `gun_attribute_overrides.json`의 총기별 `maxEnhancement` 오버라이드도 유형 한도에 도달했을 때 이 제한이 자동으로 적용됩니다.

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

## 호퍼 지원

속성 작업대는 호퍼를 지원합니다.

| 호퍼 방향 | 접근하는 슬롯 |
|-----------|-------------|
| 위에서 | 총기 슬롯 (0), 재료 슬롯 (1) |
| 옆에서 | 총기 슬롯 (0), 재료 슬롯 (1) |
| 아래에서 | 출력 슬롯 (2) |

### 자동 가공 라인 구축 예시

```
[호퍼 (위)] → 총기 투입
[속성 작업대] ← 자동 가공
[호퍼 (아래)] → 완성품 회수
```

재료 소비를 활성화한 경우 옆에서 호퍼로 재료를 공급할 수 있습니다.

---

## NBT 데이터 구조 (개발자용)

총기 아이템의 NBT에 다음 구조로 속성 데이터가 저장됩니다.

```
ItemStack NBT → TaczAddon: {
  Modifiers: [                    // 랜덤 속성
    {Attr: "tacz_attributes:gun_damage", Val: 0.15, Op: 1},
    {Attr: "tacz_attributes:recoil", Val: -0.10, Op: 1}
  ],
  FixedModifiers: [               // 고정 속성
    {Attr: "tacz_attributes:reload_speed", Val: 0.05, Op: 1}
  ],
  Score: 42,                      // 희귀도 점수
  Rarity: 2,                      // 0=COMMON, 1=UNCOMMON, 2=RARE, 3=EPIC
  Sealed: 1,                      // 재생성 방지 플래그 (불리언)
  RerollCount: 3                  // 리롤 횟수
}
```

### Op 값

| Op | 이름 | 설명 |
|----|------|------|
| 0 | ADDITION | 가산 |
| 1 | MULTIPLY_BASE | 기본값 곱연산 |
| 2 | MULTIPLY_TOTAL | 합계값 곱연산 |

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

---

## 총기별 속성 오버라이드 (gun_attribute_overrides.json)

파일: `config/tacz_attributes_addon/gun_attribute_overrides.json`

첫 실행 시 빈 JSON 파일이 생성됩니다. 총기 ID별로 랜덤 속성의 생성 규칙을 오버라이드할 수 있습니다. 루터 슈터 스타일의 모드팩에서 총기마다 다른 빌드를 만들 때 유용합니다.

### 형식

```json
{
  "총기ID": {
    "minAttributes": 최소_속성_수,
    "maxAttributes": 최대_속성_수,
    "minAttributesPos": 양성_속성_최소_수,
    "maxAttributesPos": 양성_속성_최대_수,
    "minAttributesNeg": 음성_속성_최소_수,
    "maxAttributesNeg": 음의_속성_최대_수,
    "maxEnhancement": 강화_유형_상한,
    "attributes": [
      {"attribute": "속성ID", "minValue": 최솟값, "maxValue": 최댓값}
    ]
  }
}
```

### 각 필드 설명

| 필드 | 필수 여부 | 설명 |
|------|-----------|------|
| `minAttributes` | 선택 | 랜덤 속성의 최소 수. 생략 시 글로벌 설정값을 사용 |
| `maxAttributes` | 선택 | 랜덤 속성의 최대 수. 생략 시 글로벌 설정값을 사용 |
| `minAttributesPos` | 선택 | 양성 속성(버프)의 최소 수. 분리 모드 활성화 |
| `maxAttributesPos` | 선택 | 양성 속성(버프)의 최대 수. 분리 모드 활성화 |
| `minAttributesNeg` | 선택 | 음성 속성(디버프)의 최소 수. 분리 모드 활성화 |
| `maxAttributesNeg` | 선택 | 음성 속성(디버프)의 최대 수. 분리 모드 활성화 |
| `maxEnhancement` | 선택 | 강화 속성 유형 수의 상한. 상한 도달 시 Enhancement Station은 이미 강화된 속성만 표시. 0=무제한. 생략 시 글로벌 `maxTypes` 사용 |
| `attributes` | 선택 | 허용 속성의 화이트리스트. 지정하면 이 목록의 속성만 부여 가능. 생략 시 일반 풀 필터링 |

`attributes` 내 각 항목 (`attribute` 외 모든 필드는 선택 사항. 생략 시 `attribute_pool.json`의 값을 사용):

| 필드 | 타입 | 설명 |
|------|------|------|
| `attribute` | string | **필수**. 속성 ID (`tacz_attributes:` 프리픽스 포함) |
| `minValue` | double | 이 총기에서의 커스텀 최솟값 |
| `maxValue` | double | 이 총기에서의 커스텀 최댓값 |
| `minValuePos` | double | 양성 값(버프)의 최솟값. 분리 모드용 |
| `maxValuePos` | double | 양성 값(버프)의 최댓값. 분리 모드용 |
| `minValueNeg` | double | 음성 값(디버프)의 최솟값. 분리 모드용 |
| `maxValueNeg` | double | 음성 값(디버프)의 최댓값. 분리 모드용 |
| `weight` | int | 이 총기에서의 선택 확률 (클수록 나오기 쉬움) |
| `rarityTier` | int | 이 총기에서의 희귀도 티어 (RARITY_ADAPTIVE/BALANCED 모드의 가중치에 영향) |
| `scoreWeight` | double | 이 총기에서의 희귀도 점수 기여도 |
| `operation` | string | 이 총기에서의 연산자 (`MULTIPLY_BASE`, `ADDITION`, `MULTIPLY_TOTAL`) |

### 설정 예시

```json
{
  "tacz:hk416d": {
    "minAttributes": 1,
    "maxAttributes": 3,
    "attributes": [
      {"attribute": "tacz_attributes:reload_speed", "minValue": -0.20, "maxValue": 0.20, "weight": 30},
      {"attribute": "tacz_attributes:gun_damage", "minValue": -0.10, "maxValue": 0.15, "weight": 50, "rarityTier": 2},
      {"attribute": "tacz_attributes:recoil", "minValue": -0.30, "maxValue": 0.10, "scoreWeight": -120}
    ]
  },
  "tacz:rpg7": {
    "minAttributes": 0,
    "maxAttributes": 1
  }
}
```

위 예시:
- **HK416D**: 1~3개의 랜덤 속성. reload_speed, gun_damage, recoil 3종류만 부여 가능하며 값 범위도 커스텀
- **RPG-7**: 0~1개의 랜덤 속성. 속성 종류는 일반 풀 필터링에 따름

> **팁:** `attributes`를 생략하면 속성 개수만 제어할 수 있습니다.
> 설정이 없는 총기는 기존대로 글로벌 설정에 따릅니다.
> 파일 편집 후 게임 내에서 `/taczaddon reload` 명령어로 리로드할 수 있습니다.

### 선택 확률에 대하여

각 속성 항목에서 `weight`나 `rarityTier`를 지정하면 **이 총기에 한해** 선택 확률을 커스터마이즈할 수 있습니다. 생략하면 `attribute_pool.json`의 값이 그대로 사용됩니다.

예를 들어, `attribute_pool.json`에서 `gun_damage`의 weight=20, `reload_speed`의 weight=15로 설정되어 있을 때, `gun_attribute_overrides.json`에서 `gun_damage`의 weight를 50으로 오버라이드하면 이 총기에서의 선택 확률은 50/(50+15)=77%와 15/(50+15)=23%가 됩니다.

`scoreWeight`를 오버라이드하면 이 총기의 희귀도 점수 계산에 커스텀 기여도가 적용됩니다.

### 양성/음성 속성 개별 제어 (분리 모드)

`minAttributesPos`/`maxAttributesPos`와 `minAttributesNeg`/`maxAttributesNeg`를 사용하면 양성 속성(버프)과 음성 속성(디버프)의 개수를 독립적으로 제어할 수 있습니다. 이를 통해 「버프 3개 + 디버프 1개」와 같은 정확한 구성을 보장할 수 있습니다.

```json
{
  "tacz:hk416d": {
    "minAttributesPos": 2,
    "maxAttributesPos": 3,
    "minAttributesNeg": 1,
    "maxAttributesNeg": 1,
    "attributes": [
      {
        "attribute": "tacz_attributes:reload_speed",
        "minValuePos": 0.10, "maxValuePos": 0.30,
        "minValueNeg": -0.30, "maxValueNeg": -0.10
      },
      {
        "attribute": "tacz_attributes:gun_damage",
        "minValuePos": 0.05, "maxValuePos": 0.15,
        "minValueNeg": -0.20, "maxValueNeg": -0.05
      }
    ]
  }
}
```

위 예시:
- **HK416D**: 양성 속성 2~3개와 음성 속성 1개를 보장 (합계 3~4개)
- 속성이 양성으로 선택되면 `minValuePos`~`maxValuePos` 범위에서 값을 생성
- 속성이 음성으로 선택되면 `minValueNeg`~`maxValueNeg` 범위에서 값을 생성
- `minValuePos`/`maxValuePos`/`minValueNeg`/`maxValueNeg`는 모두 선택 사항. 생략 시 속성의 전체 값 범위를 `buffThreshold`(보통 0.0)로 분할하여 사용

> **참고:** `minAttributesPos`/`maxAttributesPos`/`minAttributesNeg`/`maxAttributesNeg` 중 하나라도 설정하면 분리 모드가 자동으로 활성화됩니다. `minAttributes`/`maxAttributes`와 함께 사용하면 총 개수 상한으로 기능합니다. 모든 생성 모드(FULL_RANDOM, ADAPTIVE, RARITY_ADAPTIVE, BALANCED)에서 작동합니다.

---

## 명령어

OP 권한 (레벨 2)이 필요합니다.

### `/taczaddon clear`
들고 있는 총기에서 모든 애드온 속성 (랜덤, 고정, 점수, 희귀도)을 삭제합니다.

### `/taczaddon clear random`
랜덤 속성만 삭제합니다. 고정 속성은 유지됩니다.

### `/taczaddon clear fixed`
고정 속성만 삭제합니다. 랜덤 속성은 유지됩니다.

### `/taczaddon clear enhanced`
강화 속성만 삭제합니다. 랜덤 및 고정 속성은 유지됩니다.

### `/taczaddon add <attribute> <value> [operation]`
보유 중인 총기의 강화 속성에 속성을 수동으로 추가합니다. 동일한 속성이 이미 강화 속성에 존재하면 값이 병합(합산)됩니다.

- `attribute`: 전체 속성 ID (예: `tacz_attributes:gun_damage`). 탭 자동완성 지원
- `value`: 수치 (예: `0.15`, `-0.10`)
- `operation`: 선택사항. 기본값: `MULTIPLY_BASE`. 옵션: `ADDITION`, `MULTIPLY_BASE`, `MULTIPLY_TOTAL`

> **팁:** KubeJS 연동에 유용합니다. 모드팩 개발자가 이 명령어를 사용하여 특정 속성을 총기에 추가하는 커스텀 아이템을 만들 수 있습니다.

### `/taczaddon reroll`
들고 있는 총기의 랜덤 속성을 재생성합니다. 리롤 횟수 제한을 무시합니다.

### `/taczaddon reload`
`attribute_pool.json`, `weapon_attributes.json`, `gun_attribute_overrides.json`을 리로드합니다.
게임을 재시작하지 않고 설정 변경을 반영할 수 있습니다.

### `/taczaddon info`
들고 있는 총기에 부여된 애드온 데이터의 상세를 표시합니다. Apotheosis 연동 활성화 시 소켓/보석 정보도 표시됩니다.

### `/taczaddon config get <key>`
설정값을 조회합니다. 탭 자동완성으로 설정 키가 표시됩니다.

### `/taczaddon config set <key> <value>`
설정값을 일시적으로 변경합니다. 서버 재시작 시 리셋됩니다.

**설정 가능한 키:**
- `enableRandomOnObtain`, `enableWeaponTypeAttributes`, `enableAttributeStation`
- `enableApotheosis`, `enableRarityScoring`, `showEmptySlots`
- `randomMode` (FULL_RANDOM / ADAPTIVE / RARITY_ADAPTIVE / BALANCED)
- `fixedAttributeMode` (FIXED_ONLY / RANDOM_ONLY / BOTH_STACKING / FIXED_INFLUENCES_RANDOM)
- `minAttributes`, `maxAttributes`
- `valueDistribution` (LINEAR / EXPONENTIAL / QUADRATIC)
- `distributionExponent`, `raritySpreadFactor`, `buffDebuffRatio`
- `uncommonThreshold`, `rareThreshold`, `epicThreshold`
- `processingTime`, `consumeItem`, `consumeItemId`, `consumeCount`
- `allowReroll`, `maxRerolls`
- `gunBaseSockets`, `socketsScaleWithRarity`
- `commonSockets`, `uncommonSockets`, `rareSockets`, `epicSockets`
- `enhancementMaxTypes`, `enhancementExistingOnly`

---

## 고정 속성의 리롤 독립성

고정 속성 (weapon_attributes.json에서 정의)은 리롤 시 재생성되지 않습니다.

- 최초 획득 시에만 고정 속성이 적용됨
- 리롤은 랜덤 속성만 재생성
- 고정 속성은 그대로 유지됨

**예시:** AK에 `gun_damage: 0.95`가 고정 설정된 경우:
1. 최초 획득 시: 고정 `gun_damage: 0.95` + 랜덤 속성이 부여
2. 리롤 후: 고정 `gun_damage: 0.95`는 유지, 랜덤 속성만 변경
3. 랜덤으로 `gun_damage: 1.2`가 붙어도 고정 `0.95`는 사라지지 않고 양쪽 모두 적용

---

## Apotheosis 총기 전용 보석 목록

Apotheosis 연동이 활성화된 경우 다음 총기 전용 보석을 사용할 수 있습니다.

| 보석 | 속성 | 유형 |
|------|------|------|
| 명사수의 보석 | gun_damage | 단일 속성 |
| 안정의 보석 | recoil (감소) | 단일 속성 |
| 빠른 장전의 보석 | reload_speed | 단일 속성 |
| 정밀 사격의 보석 | headshot_multiplier | 단일 속성 |
| 확장 탄창의 보석 | magazine_capacity | 단일 속성 |
| 속사의 보석 | rpm_multiplier | 단일 속성 |
| 전술의 보석 | ads_speed + ads_accuracy | 복합 속성 |
| 탄약 절약의 보석 | ammo_save_chance | 단일 속성 |
| 탄약 회복의 보석 | ammo_recovery_chance + ammo_recovery_amount | 복합 속성 |
| 추가 탄약의 보석 | bonus_ammo_chance + bonus_ammo_amount | 복합 속성 |
| 넉백의 보석 | knockback_base | 단일 속성 |

## Apotheosis 총기 전용 접사 목록

| 접사 | 속성 |
|------|------|
| 살상의 | gun_damage |
| 안정의 | recoil (감소) |
| 신속의 | reload_speed |
| 정밀의 | headshot_multiplier |
| 대용량의 | magazine_capacity |
| 속사의 | rpm_multiplier |
| 집중의 | ads_speed |
| 절약의 | ammo_save_chance |
| 정확의 | ads_accuracy |
| 민첩의 | draw_speed |
| 강타의 | knockback_base |
