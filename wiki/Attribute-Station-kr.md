[Home](Home) > 속성 작업대

[English](Attribute-Station) | [日本語](Attribute-Station-ja) | [中文](Attribute-Station-cn) | **한국어**

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

### 소재 설정 (station_materials.json)

파일: `config/tacz_attributes_addon/station_materials.json`

`consumeItem = true`인 경우, 이 JSON 파일로 여러 소재 유형을 등록할 수 있습니다. 각 소재에는 희귀도 제약을 설정할 수 있습니다. 첫 실행 시 기본 파일이 자동 생성됩니다.

```json
[
  {
    "item": "minecraft:diamond",
    "count": 1
  },
  {
    "item": "minecraft:emerald",
    "count": 2,
    "minRarity": 2
  },
  {
    "item": "minecraft:nether_star",
    "count": 1,
    "targetRarity": 3
  },
  {
    "item": "minecraft:amethyst_shard",
    "count": 4,
    "maxRarity": 1
  }
]
```

| 필드 | 설명 |
|------|------|
| `item` | 아이템 ID (필수) |
| `count` | 소비 수량 (기본값: 1) |
| `targetRarity` | 확정 희귀도 (0-3, 최우선) |
| `minRarity` | 최소 희귀도 보장 (0-3) |
| `maxRarity` | 최대 희귀도 제한 (0-3) |

모든 희귀도 제약 필드는 선택사항입니다. 생략 시 제약 없음 (일반 랜덤). `/taczaddon reload`로 핫 리로드 가능합니다.

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

[< 이전: 희귀도 점수](Rarity-Scoring-kr) | [다음 >: Enhancement Station](Enhancement-Station-kr)
