package com.github.leopoko.tacz_attributes_addon.data;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
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
 *
 * Reads from config/tacz_attributes_addon/gun_attribute_overrides.json.
 *
 * JSON format:
 * {
 *   "tacz:hk416d": {
 *     "minAttributes": 1,
 *     "maxAttributes": 3,
 *     "attributes": [
 *       {"attribute": "tacz_attributes:reload_speed", "minValue": -0.20, "maxValue": 0.20},
 *       {"attribute": "tacz_attributes:gun_damage", "minValue": -0.10, "maxValue": 0.15}
 *     ]
 *   }
 * }
 */
public class GunAttributeOverrides {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, GunOverride> OVERRIDES = new HashMap<>();
    private static boolean loaded = false;

    /**
     * Per-gun override configuration.
     */
    public static class GunOverride {
        private final int minAttributes;  // -1 = not set (use global)
        private final int maxAttributes;  // -1 = not set (use global)
        private final Map<String, double[]> attributeRanges; // attributeId -> [min, max]

        public GunOverride(int minAttributes, int maxAttributes, Map<String, double[]> attributeRanges) {
            this.minAttributes = minAttributes;
            this.maxAttributes = maxAttributes;
            this.attributeRanges = attributeRanges;
        }

        public boolean hasMinAttributes() { return minAttributes >= 0; }
        public boolean hasMaxAttributes() { return maxAttributes >= 0; }
        public int getMinAttributes() { return minAttributes; }
        public int getMaxAttributes() { return maxAttributes; }

        /** Whether this override specifies an attribute whitelist. */
        public boolean hasAttributeList() { return !attributeRanges.isEmpty(); }

        /** Whether the given attribute is in the whitelist. */
        public boolean hasAttribute(String attributeId) { return attributeRanges.containsKey(attributeId); }

        /** Get overridden min value for an attribute, or fallback if not overridden. */
        public double getMinValue(String attributeId, double fallback) {
            double[] range = attributeRanges.get(attributeId);
            return range != null ? range[0] : fallback;
        }

        /** Get overridden max value for an attribute, or fallback if not overridden. */
        public double getMaxValue(String attributeId, double fallback) {
            double[] range = attributeRanges.get(attributeId);
            return range != null ? range[1] : fallback;
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

        Map<String, double[]> attributeRanges = new LinkedHashMap<>();
        if (obj.has("attributes") && obj.get("attributes").isJsonArray()) {
            JsonArray attrs = obj.getAsJsonArray("attributes");
            for (JsonElement elem : attrs) {
                JsonObject attrObj = elem.getAsJsonObject();
                String attrId = attrObj.get("attribute").getAsString();
                double minVal = attrObj.get("minValue").getAsDouble();
                double maxVal = attrObj.get("maxValue").getAsDouble();
                attributeRanges.put(attrId, new double[]{minVal, maxVal});
            }
        }

        return new GunOverride(minAttributes, maxAttributes, attributeRanges);
    }

    private static void createDefaultConfig(Path configFile) {
        try {
            Files.createDirectories(configFile.getParent());
            JsonObject root = new JsonObject();
            root.addProperty("_comment",
                    "Per-gun attribute overrides. Define which attributes can appear on specific guns, "
                    + "with custom min/max attribute counts and value ranges. "
                    + "Guns not listed here use the global attribute pool settings.");
            root.addProperty("_format",
                    "Each key is a gun ID (e.g. 'tacz:hk416d'). "
                    + "minAttributes/maxAttributes: (optional) override global attribute count limits. "
                    + "attributes: (optional) whitelist of allowed attributes with custom value ranges. "
                    + "If 'attributes' is specified, ONLY listed attributes can appear on this gun.");
            root.addProperty("_example",
                    "{ \"tacz:hk416d\": { \"minAttributes\": 1, \"maxAttributes\": 3, "
                    + "\"attributes\": [ "
                    + "{\"attribute\": \"tacz_attributes:reload_speed\", \"minValue\": -0.20, \"maxValue\": 0.20}, "
                    + "{\"attribute\": \"tacz_attributes:gun_damage\", \"minValue\": -0.10, \"maxValue\": 0.15} ] } }");

            String json = new GsonBuilder().setPrettyPrinting().create().toJson(root);
            Files.writeString(configFile, json);
            LOGGER.info("Created default gun_attribute_overrides.json");
        } catch (IOException e) {
            LOGGER.error("Failed to create default gun_attribute_overrides.json", e);
        }
    }
}
