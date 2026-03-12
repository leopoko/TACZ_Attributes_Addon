[Home](Home) > Feature Toggles

**English** | [日本語](Feature-Toggles-ja) | [中文](Feature-Toggles-cn) | [한국어](Feature-Toggles-kr)

---

## [general] Feature Toggles

Enable or disable each feature independently.

### `enableRandomOnObtain` (Default: `true`)
Automatically assigns random attributes to a gun when it enters inventory.

- `true`: Random attributes are automatically applied when a player obtains a gun
- `false`: No automatic assignment (manual generation via Attribute Station only)

> **Note:** Disabling this setting does not affect guns that already have attributes. It only applies to newly obtained guns.

### `enableWeaponTypeAttributes` (Default: `true`)
Enables fixed attributes per gun model, configured in `config/tacz_attributes_addon/weapon_attributes.json`.

- `true`: Applies fixed attributes based on weapon_attributes.json
- `false`: No fixed attributes are applied

### `enableAttributeStation` (Default: `true`)
Enables the Attribute Station block functionality.

- `true`: Players can insert guns into the block to generate or reroll attributes
- `false`: The block can still be placed but performs no processing

### `enableApotheosis` (Default: `true`)
Enables integration with the Apotheosis mod. When Apotheosis is installed, guns gain socket slots and gun-specific gems can be inserted.

- `true`: When Apotheosis is present, socket/gem integration is enabled
- `false`: Socket/gem features are disabled even if Apotheosis is present

### `enableRarityScoring` (Default: `true`)
Enables the rarity scoring system based on applied attributes.

- `true`: Calculates a score from attribute values and scoreWeights, then determines rarity (COMMON/UNCOMMON/RARE/EPIC). The gun item's name color changes to match its rarity.
- `false`: Skips rarity calculation. All guns display as COMMON.

### `showEmptySlots` (Default: `false`)
Shows empty attribute slots in tooltips when the gun has fewer random attributes than `maxAttributes`.

- `true`: Displays `[ ] Empty Attribute Slot` lines in the tooltip for unfilled slots. Uses per-gun `maxAttributes` override if set, otherwise the global value.
- `false`: No empty slot indication

---

 | [Next >: Random Attribute Generation](Random-Attribute-Generation)
