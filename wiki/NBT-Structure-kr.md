[Home](Home) > NBT 데이터 구조

[English](NBT-Structure) | [日本語](NBT-Structure-ja) | [中文](NBT-Structure-cn) | **한국어**

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

### 희귀도 프리셋 (TaczPreset)

`/give` 명령어나 전리품 테이블을 통해 총기의 희귀도를 사전 지정할 수 있습니다. `TaczAddon`과는 별도의 독립적인 NBT 태그이며, 속성 생성 완료 후 자동으로 제거됩니다.

```
ItemStack NBT → TaczPreset: {
  MinRarity: 2,                   // 최소 희귀도 보장 (0-3, 선택사항)
  TargetRarity: 3                 // 확정 희귀도 (0-3, 선택사항, MinRarity보다 우선)
}
```

- **MinRarity**: 지정한 희귀도 이상을 보장합니다. 최대 50회 재생성을 시도합니다
- **TargetRarity**: 지정한 희귀도를 확정합니다. MinRarity보다 우선됩니다
- 둘 다 생략하면 일반 생성과 동일하게 동작합니다

**사용 예시:**
```
/give @p tacz:modern_kinetic_gun{GunId:"tacz:ak47",TaczPreset:{MinRarity:2}}
/give @p tacz:modern_kinetic_gun{GunId:"tacz:ak47",TaczPreset:{TargetRarity:3}}
```

---

[< 이전: Apotheosis](Apotheosis-kr) |
