package com.github.leopoko.tacz_attributes_addon.data;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Per-gun attribute overrides for random generation.
 * Allows modpack developers to control per-gun:
 * - Min/max number of random attributes
 * - Which attributes can appear (whitelist)
 * - Custom min/max value ranges per attribute
 * - Custom weight, rarityTier, scoreWeight, and operation per attribute
 *
 * Reads from config/tacz_attributes_addon/gun_attribute_overrides.json.
 *
 * All attribute-level fields are optional. If omitted, the value from
 * attribute_pool.json is used as fallback.
 */
public class GunAttributeOverrides {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, GunOverride> OVERRIDES = new HashMap<>();
    private static boolean loaded = false;

    /**
     * Override entry for a single attribute within a gun override.
     * All fields are optional — sentinel values indicate "not set".
     */
    public static class AttributeOverrideEntry {
        private final double minValue;      // NaN = not set
        private final double maxValue;      // NaN = not set
        private final int weight;           // -1 = not set
        private final int rarityTier;       // -1 = not set
        private final double scoreWeight;   // NaN = not set
        private final AttributeModifier.Operation operation; // null = not set

        public AttributeOverrideEntry(double minValue, double maxValue, int weight,
                                       int rarityTier, double scoreWeight,
                                       AttributeModifier.Operation operation) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.weight = weight;
            this.rarityTier = rarityTier;
            this.scoreWeight = scoreWeight;
            this.operation = operation;
        }

        public boolean hasMinValue() { return !Double.isNaN(minValue); }
        public boolean hasMaxValue() { return !Double.isNaN(maxValue); }
        public boolean hasWeight() { return weight >= 0; }
        public boolean hasRarityTier() { return rarityTier >= 0; }
        public boolean hasScoreWeight() { return !Double.isNaN(scoreWeight); }
        public boolean hasOperation() { return operation != null; }

        public double getMinValue() { return minValue; }
        public double getMaxValue() { return maxValue; }
        public int getWeight() { return weight; }
        public int getRarityTier() { return rarityTier; }
        public double getScoreWeight() { return scoreWeight; }
        public AttributeModifier.Operation getOperation() { return operation; }
    }

    /**
     * Per-gun override configuration.
     */
    public static class GunOverride {
        private final int minAttributes;  // -1 = not set (use global)
        private final int maxAttributes;  // -1 = not set (use global)
        private final Map<String, AttributeOverrideEntry> attributeOverrides;

        public GunOverride(int minAttributes, int maxAttributes,
                           Map<String, AttributeOverrideEntry> attributeOverrides) {
            this.minAttributes = minAttributes;
            this.maxAttributes = maxAttributes;
            this.attributeOverrides = attributeOverrides;
        }

        public boolean hasMinAttributes() { return minAttributes >= 0; }
        public boolean hasMaxAttributes() { return maxAttributes >= 0; }
        public int getMinAttributes() { return minAttributes; }
        public int getMaxAttributes() { return maxAttributes; }

        /** Whether this override specifies an attribute whitelist. */
        public boolean hasAttributeList() { return !attributeOverrides.isEmpty(); }

        /** Whether the given attribute is in the whitelist. */
        public boolean hasAttribute(String attributeId) { return attributeOverrides.containsKey(attributeId); }

        /** Get overridden min value for an attribute, or fallback if not overridden. */
        public double getMinValue(String attributeId, double fallback) {
            AttributeOverrideEntry entry = attributeOverrides.get(attributeId);
            return entry != null && entry.hasMinValue() ? entry.getMinValue() : fallback;
        }

        /** Get overridden max value for an attribute, or fallback if not overridden. */
        public double getMaxValue(String attributeId, double fallback) {
            AttributeOverrideEntry entry = attributeOverrides.get(attributeId);
            return entry != null && entry.hasMaxValue() ? entry.getMaxValue() : fallback;
        }

        /** Get overridden weight for an attribute, or fallback if not overridden. */
        public int getWeight(String attributeId, int fallback) {
            AttributeOverrideEntry entry = attributeOverrides.get(attributeId);
            return entry != null && entry.hasWeight() ? entry.getWeight() : fallback;
        }

        /** Get overridden rarityTier for an attribute, or fallback if not overridden. */
        public int getRarityTier(String attributeId, int fallback) {
            AttributeOverrideEntry entry = attributeOverrides.get(attributeId);
            return entry != null && entry.hasRarityTier() ? entry.getRarityTier() : fallback;
        }

        /** Get overridden scoreWeight for an attribute, or fallback if not overridden. */
        public double getScoreWeight(String attributeId, double fallback) {
            AttributeOverrideEntry entry = attributeOverrides.get(attributeId);
            return entry != null && entry.hasScoreWeight() ? entry.getScoreWeight() : fallback;
        }

        /** Get overridden operation for an attribute, or fallback if not overridden. */
        public AttributeModifier.Operation getOperation(String attributeId, AttributeModifier.Operation fallback) {
            AttributeOverrideEntry entry = attributeOverrides.get(attributeId);
            return entry != null && entry.hasOperation() ? entry.getOperation() : fallback;
        }
    }

    /**
     * Load overrides from config file.
     */
    public static void loadConfig(Path configDir) {
        OVERRIDES.clear();
        Path configFile = configDir.resolve("tacz_attributes_addon").resolve("gun_attribute_overrides.json");

        if (!Files.exists(configFile)) {
            createDefaultConfig(configFile);
            loaded = true;
            return;
        }

        try (Reader reader = Files.newBufferedReader(configFile)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                String key = entry.getKey();
                // Skip comment fields
                if (key.startsWith("_")) continue;

                try {
                    GunOverride override = parseOverride(entry.getValue().getAsJsonObject());
                    OVERRIDES.put(key, override);
                } catch (Exception e) {
                    LOGGER.warn("Skipping invalid gun override for '{}': {}", key, e.getMessage());
                }
            }

            LOGGER.info("Loaded gun attribute overrides for {} guns", OVERRIDES.size());
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.error("Failed to load gun_attribute_overrides.json", e);
        }

        loaded = true;
    }

    /**
     * Get override for a specific gun ID.
     * @return the override, or null if no override exists for this gun
     */
    public static GunOverride getOverride(String gunId) {
        if (!loaded) return null;
        return OVERRIDES.get(gunId);
    }

    private static GunOverride parseOverride(JsonObject obj) {
        int minAttributes = obj.has("minAttributes") ? obj.get("minAttributes").getAsInt() : -1;
        int maxAttributes = obj.has("maxAttributes") ? obj.get("maxAttributes").getAsInt() : -1;

        Map<String, AttributeOverrideEntry> attributeOverrides = new LinkedHashMap<>();
        if (obj.has("attributes") && obj.get("attributes").isJsonArray()) {
            JsonArray attrs = obj.getAsJsonArray("attributes");
            for (JsonElement elem : attrs) {
                JsonObject attrObj = elem.getAsJsonObject();
                String attrId = attrObj.get("attribute").getAsString();

                double minVal = attrObj.has("minValue") ? attrObj.get("minValue").getAsDouble() : Double.NaN;
                double maxVal = attrObj.has("maxValue") ? attrObj.get("maxValue").getAsDouble() : Double.NaN;
                int weight = attrObj.has("weight") ? attrObj.get("weight").getAsInt() : -1;
                int rarityTier = attrObj.has("rarityTier") ? attrObj.get("rarityTier").getAsInt() : -1;
                double scoreWeight = attrObj.has("scoreWeight") ? attrObj.get("scoreWeight").getAsDouble() : Double.NaN;
                AttributeModifier.Operation operation = attrObj.has("operation")
                        ? parseOperation(attrObj.get("operation").getAsString()) : null;

                attributeOverrides.put(attrId, new AttributeOverrideEntry(
                        minVal, maxVal, weight, rarityTier, scoreWeight, operation));
            }
        }

        return new GunOverride(minAttributes, maxAttributes, attributeOverrides);
    }

    private static AttributeModifier.Operation parseOperation(String opStr) {
        return switch (opStr.toUpperCase()) {
            case "ADDITION" -> AttributeModifier.Operation.ADDITION;
            case "MULTIPLY_BASE" -> AttributeModifier.Operation.MULTIPLY_BASE;
            case "MULTIPLY_TOTAL" -> AttributeModifier.Operation.MULTIPLY_TOTAL;
            default -> {
                LOGGER.warn("Unknown operation '{}', ignoring", opStr);
                yield null;
            }
        };
    }

    private static void createDefaultConfig(Path configFile) {
        try {
            Files.createDirectories(configFile.getParent());
            JsonObject root = new JsonObject();
            root.addProperty("_comment",
                    "Per-gun attribute overrides. Define which attributes can appear on specific guns, "
                    + "with custom min/max attribute counts, value ranges, selection weights, and more. "
                    + "Guns not listed here use the global attribute pool settings.");
            root.addProperty("_format",
                    "Each key is a gun ID (e.g. 'tacz:hk416d'). "
                    + "minAttributes/maxAttributes: (optional) override global attribute count limits. "
                    + "attributes: (optional) whitelist of allowed attributes. "
                    + "Each attribute entry supports optional fields: "
                    + "minValue, maxValue, weight, rarityTier, scoreWeight, operation. "
                    + "Omitted fields fall back to attribute_pool.json values.");
            root.addProperty("_example",
                    "{ \"tacz:hk416d\": { \"minAttributes\": 1, \"maxAttributes\": 3, "
                    + "\"attributes\": [ "
                    + "{\"attribute\": \"tacz_attributes:reload_speed\", \"minValue\": -0.20, \"maxValue\": 0.20, \"weight\": 30}, "
                    + "{\"attribute\": \"tacz_attributes:gun_damage\", \"minValue\": -0.10, \"maxValue\": 0.15, \"weight\": 50, \"rarityTier\": 2} ] } }");

            String json = new GsonBuilder().setPrettyPrinting().create().toJson(root);
            Files.writeString(configFile, json);
            LOGGER.info("Created default gun_attribute_overrides.json");
        } catch (IOException e) {
            LOGGER.error("Failed to create default gun_attribute_overrides.json", e);
        }
    }
}
