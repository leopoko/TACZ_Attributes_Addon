[Home](Home) > 총기별 속성 오버라이드

[English](Gun-Attribute-Overrides) | [日本語](Gun-Attribute-Overrides-ja) | [中文](Gun-Attribute-Overrides-cn) | **한국어**

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

### 총기별 속성 그룹 (오버라이드)

총기별 오버라이드 설정에 `attributeGroups`를 추가하면 총기마다 속성 그룹 제한을 커스터마이즈할 수 있습니다. 같은 이름의 그룹은 글로벌 설정을 덮어쓰고, 새로운 이름의 그룹은 추가됩니다.

```json
{
  "tacz:ak47": {
    "minAttributes": 3,
    "maxAttributes": 5,
    "attributeGroups": [
      {
        "name": "damage",
        "maxFromGroup": 2
      },
      {
        "name": "speed",
        "maxFromGroup": 1,
        "attributes": [
          "tacz_attributes:reload_speed",
          "tacz_attributes:draw_speed",
          "tacz_attributes:ads_speed"
        ]
      }
    ]
  }
}
```

위 예시 (AK47):
- **damage 그룹**: 글로벌 설정은 `maxFromGroup: 1`이지만, AK47에서는 `2`로 완화. `attributes` 생략 시 글로벌 그룹의 속성 목록을 상속
- **speed 그룹**: AK47 전용 새 그룹. 속도 관련 속성은 최대 1개만 출현

> **참고:** `attributes` 필드를 생략하면 같은 이름의 글로벌 그룹의 속성 목록을 상속합니다. 총기별로만 존재하는 새 그룹에는 `attributes` 지정이 필요합니다.

---

[이전: 속성 풀 설정](Attribute-Pool-kr) | [다음: 명령어](Commands-kr)
