[Home](Home) > 명령어

[English](Commands) | [日本語](Commands-ja) | [中文](Commands-cn) | **한국어**

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

[이전: 총기별 속성 오버라이드](Gun-Attribute-Overrides-kr) | [다음: Apotheosis 연동](Apotheosis-kr)
