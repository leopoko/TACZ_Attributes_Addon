[Home](Home) > Enhancement Station

[English](Enhancement-Station) | [日本語](Enhancement-Station-ja) | [中文](Enhancement-Station-cn) | **한국어**

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

[< 이전: 속성 작업대](Attribute-Station-kr) | [다음 >: 고정 속성](Weapon-Attributes-kr)
