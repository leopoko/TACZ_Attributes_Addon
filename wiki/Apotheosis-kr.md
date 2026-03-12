[Home](Home) > Apotheosis 연동

[English](Apotheosis) | [日本語](Apotheosis-ja) | [中文](Apotheosis-cn) | **한국어**

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

---

[이전: 명령어](Commands-kr) | [다음: NBT 데이터 구조](NBT-Structure-kr)
