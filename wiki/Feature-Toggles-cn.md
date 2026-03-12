[Home](Home) > 功能开关

[English](Feature-Toggles) | [日本語](Feature-Toggles-ja) | **中文** | [한국어](Feature-Toggles-kr)

---

## [general] 功能开关

可以单独启用或禁用各项功能。

### `enableRandomOnObtain`（默认：`true`）
当枪械进入物品栏时，自动附加随机属性。

- `true`：玩家获得枪械时自动附加随机属性
- `false`：不自动附加（仅可通过属性工作台手动附加）

> **注意：** 禁用此设置不会影响已经附有属性的枪械，仅对新获得的枪械有效。

### `enableWeaponTypeAttributes`（默认：`true`）
启用每把枪型号对应的固定属性。固定属性在 `config/tacz_attributes_addon/weapon_attributes.json` 中配置。

- `true`：根据 weapon_attributes.json 的设置应用固定属性
- `false`：不应用任何固定属性

### `enableAttributeStation`（默认：`true`）
启用属性工作台方块的功能。

- `true`：将枪放入方块中，可以附加或重掷属性
- `false`：方块可以放置，但不会进行任何处理

### `enableApotheosis`（默认：`true`）
启用与 Apotheosis MOD 的联动功能。安装 Apotheosis 后，枪械将获得宝石插槽，可以插入枪械专用宝石。

- `true`：当 Apotheosis 存在时，启用插槽/宝石集成
- `false`：即使 Apotheosis 存在也禁用插槽/宝石功能

### `enableRarityScoring`（默认：`true`）
启用基于属性的稀有度评分系统。

- `true`：根据附加属性的数值和 scoreWeight 计算评分，并决定稀有度（COMMON/UNCOMMON/RARE/EPIC）。枪械物品的名称颜色随稀有度变化。
- `false`：跳过稀有度计算，所有枪械均显示为 COMMON。

### `showEmptySlots`（默认：`false`）
当枪械的随机属性数量少于 `maxAttributes` 时，在工具提示中显示空属性槽位。

- `true`：将未使用的槽位显示为 `[ ] Empty Attribute Slot`。优先使用每枪 `maxAttributes` 覆盖值，否则使用全局值。
- `false`：不显示空槽位

---

[下一页: 随机属性生成](Random-Attribute-Generation-cn)
