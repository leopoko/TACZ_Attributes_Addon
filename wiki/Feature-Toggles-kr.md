[Home](Home) > 기능 토글

[English](Feature-Toggles) | [日本語](Feature-Toggles-ja) | [中文](Feature-Toggles-cn) | **한국어**

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

 | [다음 >: 랜덤 속성 생성](Random-Attribute-Generation-kr)
