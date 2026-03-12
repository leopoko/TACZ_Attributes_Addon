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

---

[< 이전: Apotheosis](Apotheosis-kr) | 
