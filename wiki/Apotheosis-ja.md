[Home](Home) > Apotheosis連携

[English](Apotheosis) | **日本語** | [中文](Apotheosis-cn) | [한국어](Apotheosis-kr)

---

## [apotheosis] Apotheosis連携設定

Apotheosis MODがインストールされている場合に有効になるソケット/ジェム機能の設定です。

### `gunBaseSockets` (デフォルト: `2`, 範囲: 0〜6)
レアリティに連動しない場合（`socketsScaleWithRarity = false`）の固定ソケット数。

### `socketsScaleWithRarity` (デフォルト: `true`)
ソケット数をレアリティに連動させるかどうか。

- `true`: レアリティに応じてソケット数が変動（下記の設定値を使用）
- `false`: 全ての銃が `gunBaseSockets` で指定された固定数のソケットを持つ

### `commonSockets` (デフォルト: `1`, 範囲: 0〜6)
COMMONレアリティ銃のソケット数。

### `uncommonSockets` (デフォルト: `2`, 範囲: 0〜6)
UNCOMMONレアリティ銃のソケット数。

### `rareSockets` (デフォルト: `3`, 範囲: 0〜6)
RAREレアリティ銃のソケット数。

### `epicSockets` (デフォルト: `4`, 範囲: 0〜6)
EPICレアリティ銃のソケット数。

---

## Apotheosis 銃専用ジェム一覧

Apotheosis連携が有効な場合、以下の銃専用ジェムが利用可能です。

| ジェム | 属性 | タイプ |
|--------|------|--------|
| マークスマンの宝石 | gun_damage | 単体属性 |
| 安定の宝石 | recoil（軽減） | 単体属性 |
| 速装填の宝石 | reload_speed | 単体属性 |
| 精密射撃の宝石 | headshot_multiplier | 単体属性 |
| 拡張弾倉の宝石 | magazine_capacity | 単体属性 |
| 速射の宝石 | rpm_multiplier | 単体属性 |
| 戦術の宝石 | ads_speed + ads_accuracy | 複合属性 |
| 弾薬節約の宝石 | ammo_save_chance | 単体属性 |
| 弾薬回復の宝石 | ammo_recovery_chance + ammo_recovery_amount | 複合属性 |
| 追加弾薬の宝石 | bonus_ammo_chance + bonus_ammo_amount | 複合属性 |
| ノックバックの宝石 | knockback_base | 単体属性 |

## Apotheosis 銃専用アフィックス一覧

| アフィックス | 属性 |
|-------------|------|
| 殺傷の | gun_damage |
| 安定の | recoil（軽減） |
| 迅速の | reload_speed |
| 精密の | headshot_multiplier |
| 大容量の | magazine_capacity |
| 速射の | rpm_multiplier |
| 集中の | ads_speed |
| 節約の | ammo_save_chance |
| 命中の | ads_accuracy |
| 敏捷の | draw_speed |
| 強打の | knockback_base |

---

[前へ: コマンド](Commands-ja) | [次へ: NBTデータ構造](NBT-Structure-ja)
