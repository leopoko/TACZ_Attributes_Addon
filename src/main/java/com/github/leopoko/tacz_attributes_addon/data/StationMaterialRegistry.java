package com.github.leopoko.tacz_attributes_addon.data;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registry for Attribute Station materials loaded from station_materials.json.
 * Each material can have rarity constraints (targetRarity, minRarity, maxRarity).
 */
public class StationMaterialRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<StationMaterial> MATERIALS = new ArrayList<>();
    private static boolean initialized = false;

    public static List<StationMaterial> getMaterials() {
        return Collections.unmodifiableList(MATERIALS);
    }

    /**
     * Find the first registered material that matches the given ItemStack.
     * @return the matching StationMaterial, or null if none matches
     */
    public static StationMaterial findMatchingMaterial(ItemStack stack) {
        for (StationMaterial material : MATERIALS) {
            if (material.matches(stack)) {
                return material;
            }
        }
        return null;
    }

    /**
     * Check if any registered material exists.
     */
    public static boolean hasRegisteredMaterials() {
        return !MATERIALS.isEmpty();
    }

    /**
     * Load materials from config file, or create the default file if it doesn't exist.
     */
    public static void loadConfig(Path configDir) {
        Path configFile = configDir.resolve("tacz_attributes_addon").resolve("station_materials.json");

        if (!Files.exists(configFile)) {
            initDefaults();
            initialized = true;
            saveConfig(configFile);
            LOGGER.info("Created default station_materials.json with {} entries", MATERIALS.size());
            return;
        }

        try (Reader reader = Files.newBufferedReader(configFile)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonArray()) {
                LOGGER.warn("station_materials.json must be a JSON array, using defaults");
                initDefaults();
                initialized = true;
                return;
            }

            MATERIALS.clear();
            JsonArray arr = root.getAsJsonArray();

            for (JsonElement elem : arr) {
                try {
                    StationMaterial material = parseMaterial(elem.getAsJsonObject());
                    if (material != null) {
                        MATERIALS.add(material);
                    }
                } catch (Exception e) {
                    LOGGER.warn("Skipping invalid station material entry: {}", e.getMessage());
                }
            }

            initialized = true;
            LOGGER.info("Loaded {} station materials from station_materials.json", MATERIALS.size());
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.error("Failed to load station_materials.json, using defaults", e);
            initDefaults();
            initialized = true;
        }
    }

    /**
     * Reload from config file (for /taczaddon reload command).
     */
    public static void reloadConfig(Path configDir) {
        MATERIALS.clear();
        initialized = false;
        loadConfig(configDir);
    }

    private static StationMaterial parseMaterial(JsonObject obj) {
        String itemId = obj.get("item").getAsString();
        int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
        int targetRarity = obj.has("targetRarity") ? obj.get("targetRarity").getAsInt() : -1;
        int minRarity = obj.has("minRarity") ? obj.get("minRarity").getAsInt() : -1;
        int maxRarity = obj.has("maxRarity") ? obj.get("maxRarity").getAsInt() : -1;

        // Validate rarity values
        if (targetRarity > 3) targetRarity = 3;
        if (minRarity > 3) minRarity = 3;
        if (maxRarity > 3) maxRarity = 3;

        return new StationMaterial(itemId, Math.max(1, count), targetRarity, minRarity, maxRarity);
    }

    private static void saveConfig(Path configFile) {
        try {
            Files.createDirectories(configFile.getParent());

            JsonArray arr = new JsonArray();
            for (StationMaterial material : MATERIALS) {
                arr.add(materialToJson(material));
            }

            String json = new GsonBuilder().setPrettyPrinting().create().toJson(arr);
            Files.writeString(configFile, json);
        } catch (IOException e) {
            LOGGER.error("Failed to save station_materials.json", e);
        }
    }

    private static JsonObject materialToJson(StationMaterial material) {
        JsonObject obj = new JsonObject();
        obj.addProperty("item", material.getItemId());
        obj.addProperty("count", material.getCount());
        if (material.getTargetRarity() >= 0) {
            obj.addProperty("targetRarity", material.getTargetRarity());
        }
        if (material.getMinRarity() >= 0) {
            obj.addProperty("minRarity", material.getMinRarity());
        }
        if (material.getMaxRarity() >= 0) {
            obj.addProperty("maxRarity", material.getMaxRarity());
        }
        return obj;
    }

    private static void initDefaults() {
        MATERIALS.clear();
        // Default: diamond x1, no rarity constraint (matches old default behavior)
        MATERIALS.add(new StationMaterial("minecraft:diamond", 1, -1, -1, -1));
    }
}
