package com.github.leopoko.tacz_attributes_addon.handler;

import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.github.leopoko.tacz_attributes_addon.data.GunModifier;
import com.github.leopoko.tacz_attributes_addon.random.GunTypeFilter;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Feature 2: Applies per-gun-model fixed attributes.
 * Reads from config/tacz_attributes_addon/weapon_attributes.json.
 *
 * JSON format:
 * {
 *   "tacz:ak47": [
 *     {"attribute": "tacz_attributes:gun_damage", "value": 0.05, "operation": "MULTIPLY_BASE"}
 *   ],
 *   "tacz:m16a2": [
 *     {"attribute": "tacz_attributes:reload_speed", "value": 0.10, "operation": "MULTIPLY_BASE"},
 *     {"attribute": "tacz_attributes:recoil", "value": -0.05, "operation": "MULTIPLY_BASE"}
 *   ]
 * }
 */
public class WeaponTypeHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, List<GunModifier>> WEAPON_ATTRIBUTES = new HashMap<>();
    private static boolean loaded = false;

    /**
     * Load weapon attributes from config file.
     */
    public static void loadConfig(Path configDir) {
        WEAPON_ATTRIBUTES.clear();
        Path configFile = configDir.resolve("tacz_attributes_addon").resolve("weapon_attributes.json");

        if (!Files.exists(configFile)) {
            // Create default config
            createDefaultConfig(configFile);
            loaded = true;
            return;
        }

        try (Reader reader = Files.newBufferedReader(configFile)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                String gunId = entry.getKey();
                JsonArray attrs = entry.getValue().getAsJsonArray();
                List<GunModifier> modifiers = new ArrayList<>();

                for (JsonElement attrElem : attrs) {
                    JsonObject attrObj = attrElem.getAsJsonObject();
                    String attribute = attrObj.get("attribute").getAsString();
                    double value = attrObj.get("value").getAsDouble();
                    String opStr = attrObj.has("operation") ? attrObj.get("operation").getAsString() : "MULTIPLY_BASE";
                    AttributeModifier.Operation op = parseOperation(opStr);
                    modifiers.add(new GunModifier(attribute, value, op));
                }

                WEAPON_ATTRIBUTES.put(gunId, modifiers);
            }

            LOGGER.info("Loaded weapon attributes for {} gun models", WEAPON_ATTRIBUTES.size());
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.error("Failed to load weapon_attributes.json", e);
        }

        loaded = true;
    }

    /**
     * Apply fixed attributes to a gun based on its gun ID.
     */
    public static void applyFixedAttributes(ItemStack stack) {
        if (!loaded || !CommonConfig.ENABLE_WEAPON_TYPE_ATTRIBUTES.get()) return;

        ResourceLocation gunId = GunTypeFilter.resolveGunId(stack);
        if (gunId == null) return;

        List<GunModifier> fixedMods = WEAPON_ATTRIBUTES.get(gunId.toString());
        if (fixedMods == null || fixedMods.isEmpty()) return;

        GunAttributeData.setFixedModifiers(stack, fixedMods);
    }

    /**
     * Get fixed attributes for a gun ID (for FIXED_INFLUENCES_RANDOM mode).
     */
    public static List<GunModifier> getFixedAttributes(String gunId) {
        return WEAPON_ATTRIBUTES.getOrDefault(gunId, Collections.emptyList());
    }

    private static AttributeModifier.Operation parseOperation(String op) {
        switch (op.toUpperCase()) {
            case "ADDITION":
            case "ADD_VALUE": return AttributeModifier.Operation.ADD_VALUE;
            case "MULTIPLY_TOTAL":
            case "ADD_MULTIPLIED_TOTAL": return AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
            case "MULTIPLY_BASE":
            case "ADD_MULTIPLIED_BASE":
            default: return AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
        }
    }

    private static void createDefaultConfig(Path configFile) {
        try {
            Files.createDirectories(configFile.getParent());
            JsonObject root = new JsonObject();

            // Example entries (commented out by using empty arrays to not affect gameplay by default)
            JsonObject example = new JsonObject();
            example.addProperty("_comment", "Example: add per-gun-model fixed attributes here");

            Files.writeString(configFile, new GsonBuilder().setPrettyPrinting().create().toJson(root));
            LOGGER.info("Created default weapon_attributes.json");
        } catch (IOException e) {
            LOGGER.error("Failed to create default weapon_attributes.json", e);
        }
    }
}
