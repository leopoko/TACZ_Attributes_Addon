[Home](Home) > 랜덤 속성 생성

[English](Random-Attribute-Generation) | [日本語](Random-Attribute-Generation-ja) | [中文](Random-Attribute-Generation-cn) | **한국어**

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

| | |
|:---|---:|
| [이전: 기능 토글](Feature-Toggles-kr) | [다음: 희귀도 점수](Rarity-Scoring-kr) |
