[Home](Home) > 희귀도 점수 임계값

[English](Rarity-Scoring) | [日本語](Rarity-Scoring-ja) | [中文](Rarity-Scoring-cn) | **한국어**

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

| | |
|:---|---:|
| [이전: 랜덤 속성 생성](Random-Attribute-Generation-kr) | [다음: 속성 작업대](Attribute-Station-kr) |
