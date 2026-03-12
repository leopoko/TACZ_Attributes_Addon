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
 * Registry of all attribute entries available for random generation.
 * Each entry maps to a TACZ Attributes attribute (registered under "tacz_attributes:" namespace).
 *
 * Entries can be loaded from a JSON config file (config/tacz_attributes_addon/attribute_pool.json).
 * If the file doesn't exist, defaults are written out.
 */
public class AttributeRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<AttributeEntry> ENTRIES = new ArrayList<>();
    private static final List<AttributeGroup> GROUPS = new ArrayList<>();
    private static boolean initialized = false;

    public static List<AttributeEntry> getEntries() {
        if (!initialized) {
            initDefaults();
            initialized = true;
        }
        return Collections.unmodifiableList(ENTRIES);
    }

    public static List<AttributeGroup> getGroups() {
        return Collections.unmodifiableList(GROUPS);
    }

    public static void clear() {
        ENTRIES.clear();
        GROUPS.clear();
        initialized = false;
    }

    public static void addEntry(AttributeEntry entry) {
        ENTRIES.add(entry);
    }

    /**
     * Load attribute pool from config file, or create the default file if it doesn't exist.
     * Called during commonSetup.
     */
    public static void loadConfig(Path configDir) {
        Path configFile = configDir.resolve("tacz_attributes_addon").resolve("attribute_pool.json");

        if (!Files.exists(configFile)) {
            // Initialize defaults first, then write them out
            initDefaults();
            initialized = true;
            saveConfig(configFile);
            LOGGER.info("Created default attribute_pool.json with {} entries", ENTRIES.size());
            return;
        }

        // Load from file
        try (Reader reader = Files.newBufferedReader(configFile)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            if (!root.has("attributes") || !root.get("attributes").isJsonArray()) {
                LOGGER.warn("attribute_pool.json missing 'attributes' array, using defaults");
                initDefaults();
                initialized = true;
                return;
            }

            ENTRIES.clear();
            JsonArray attrs = root.getAsJsonArray("attributes");

            for (JsonElement elem : attrs) {
                try {
                    AttributeEntry entry = parseEntry(elem.getAsJsonObject());
                    if (entry != null) {
                        ENTRIES.add(entry);
                    }
                } catch (Exception e) {
                    LOGGER.warn("Skipping invalid attribute entry: {}", e.getMessage());
                }
            }

            // Parse attribute groups
            GROUPS.clear();
            if (root.has("attributeGroups") && root.get("attributeGroups").isJsonArray()) {
                JsonArray groupsArr = root.getAsJsonArray("attributeGroups");
                for (JsonElement groupElem : groupsArr) {
                    try {
                        AttributeGroup group = parseGroup(groupElem.getAsJsonObject());
                        if (group != null) {
                            GROUPS.add(group);
                        }
                    } catch (Exception e) {
                        LOGGER.warn("Skipping invalid attribute group: {}", e.getMessage());
                    }
                }
            }

            initialized = true;
            LOGGER.info("Loaded {} attribute entries and {} attribute groups from attribute_pool.json",
                    ENTRIES.size(), GROUPS.size());
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.error("Failed to load attribute_pool.json, using defaults", e);
            initDefaults();
            initialized = true;
        }
    }

    /**
     * Reload from config file (for /taczaddon reload command).
     */
    public static void reloadConfig(Path configDir) {
        ENTRIES.clear();
        initialized = false;
        loadConfig(configDir);
    }

    private static AttributeEntry parseEntry(JsonObject obj) {
        String id = obj.get("attributeId").getAsString();
        double min = obj.get("minValue").getAsDouble();
        double max = obj.get("maxValue").getAsDouble();

        String opStr = obj.has("operation") ? obj.get("operation").getAsString() : "MULTIPLY_BASE";
        AttributeModifier.Operation op = parseOperation(opStr);

        int weight = obj.has("weight") ? obj.get("weight").getAsInt() : 10;
        int rarityTier = obj.has("rarityTier") ? obj.get("rarityTier").getAsInt() : 1;
        double buffThreshold = obj.has("buffThreshold") ? obj.get("buffThreshold").getAsDouble() : 0.0;
        double scoreWeight = obj.has("scoreWeight") ? obj.get("scoreWeight").getAsDouble() : 100;

        Set<String> gunTypes = new HashSet<>();
        if (obj.has("applicableGunTypes")) {
            JsonArray typesArr = obj.getAsJsonArray("applicableGunTypes");
            for (JsonElement t : typesArr) {
                gunTypes.add(t.getAsString().toLowerCase());
            }
        }
        // Empty set means all gun types

        String linkedAttribute = obj.has("linkedAttribute") ? obj.get("linkedAttribute").getAsString() : null;

        Set<String> weaponWhitelist = new HashSet<>();
        if (obj.has("weaponWhitelist")) {
            JsonArray wlArr = obj.getAsJsonArray("weaponWhitelist");
            for (JsonElement w : wlArr) {
                weaponWhitelist.add(w.getAsString());
            }
        }

        Set<String> weaponBlacklist = new HashSet<>();
        if (obj.has("weaponBlacklist")) {
            JsonArray blArr = obj.getAsJsonArray("weaponBlacklist");
            for (JsonElement b : blArr) {
                weaponBlacklist.add(b.getAsString());
            }
        }

        return new AttributeEntry(id, min, max, op, weight, rarityTier, gunTypes, buffThreshold, scoreWeight,
                linkedAttribute, weaponWhitelist, weaponBlacklist);
    }

    private static AttributeGroup parseGroup(JsonObject obj) {
        String name = obj.get("name").getAsString();
        int maxFromGroup = obj.get("maxFromGroup").getAsInt();

        Set<String> attributes = new HashSet<>();
        if (obj.has("attributes") && obj.get("attributes").isJsonArray()) {
            JsonArray attrsArr = obj.getAsJsonArray("attributes");
            for (JsonElement a : attrsArr) {
                attributes.add(a.getAsString());
            }
        }

        return new AttributeGroup(name, maxFromGroup, attributes);
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

    private static void saveConfig(Path configFile) {
        try {
            Files.createDirectories(configFile.getParent());

            JsonObject root = new JsonObject();
            root.addProperty("_comment", "Attribute pool configuration for TACZ Attributes Addon. "
                    + "Add, remove, or modify entries to customize which attributes can appear on guns.");
            root.addProperty("_format", "attributeId: TACZ Attributes attribute ID | "
                    + "minValue/maxValue: value range for random generation | "
                    + "operation: MULTIPLY_BASE, ADDITION, or MULTIPLY_TOTAL | "
                    + "weight: selection frequency (higher = more common) | "
                    + "rarityTier: 1-4 (1=common, 4=epic, affects RARITY_ADAPTIVE mode) | "
                    + "applicableGunTypes: [] for all types, or list like [\"rifle\",\"smg\"] | "
                    + "buffThreshold: values above this are considered buffs (usually 0.0) | "
                    + "scoreWeight: rarity score contribution per unit value (negative for inverted attributes like recoil) | "
                    + "linkedAttribute: (optional) attributeId of a partner that must be selected together "
                    + "(e.g. ammo_recovery_chance needs ammo_recovery_amount to function) | "
                    + "weaponBlacklist: (optional) specific weapon IDs to always exclude (e.g. [\"tacz:rpg7\"]) | "
                    + "weaponWhitelist: (optional) specific weapon IDs to additionally allow regardless of gun type (e.g. [\"tacz:ak47\"])");

            JsonArray attrs = new JsonArray();
            for (AttributeEntry entry : ENTRIES) {
                attrs.add(entryToJson(entry));
            }
            root.add("attributes", attrs);

            root.addProperty("_attributeGroupsFormat",
                    "attributeGroups: (optional) define groups of similar attributes with a limit on how many "
                    + "from the group can appear on a single gun. "
                    + "name: group identifier | maxFromGroup: max attributes from this group per gun | "
                    + "attributes: list of attribute IDs in this group");
            JsonArray groupsArr = new JsonArray();
            for (AttributeGroup group : GROUPS) {
                groupsArr.add(groupToJson(group));
            }
            root.add("attributeGroups", groupsArr);

            String json = new GsonBuilder().setPrettyPrinting().create().toJson(root);
            Files.writeString(configFile, json);
        } catch (IOException e) {
            LOGGER.error("Failed to save attribute_pool.json", e);
        }
    }

    private static JsonObject entryToJson(AttributeEntry entry) {
        JsonObject obj = new JsonObject();
        obj.addProperty("attributeId", entry.getAttributeId());
        obj.addProperty("minValue", entry.getMinValue());
        obj.addProperty("maxValue", entry.getMaxValue());
        obj.addProperty("operation", operationToString(entry.getOperation()));
        obj.addProperty("weight", entry.getWeight());
        obj.addProperty("rarityTier", entry.getRarityTier());

        JsonArray types = new JsonArray();
        for (String type : entry.getApplicableGunTypes()) {
            types.add(type);
        }
        obj.add("applicableGunTypes", types);

        obj.addProperty("buffThreshold", entry.getBuffThreshold());
        obj.addProperty("scoreWeight", entry.getScoreWeight());
        if (entry.hasLinkedAttribute()) {
            obj.addProperty("linkedAttribute", entry.getLinkedAttribute());
        }
        if (!entry.getWeaponWhitelist().isEmpty()) {
            JsonArray wl = new JsonArray();
            for (String w : entry.getWeaponWhitelist()) {
                wl.add(w);
            }
            obj.add("weaponWhitelist", wl);
        }
        if (!entry.getWeaponBlacklist().isEmpty()) {
            JsonArray bl = new JsonArray();
            for (String b : entry.getWeaponBlacklist()) {
                bl.add(b);
            }
            obj.add("weaponBlacklist", bl);
        }
        return obj;
    }

    private static JsonObject groupToJson(AttributeGroup group) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", group.getName());
        obj.addProperty("maxFromGroup", group.getMaxFromGroup());
        JsonArray attrs = new JsonArray();
        for (String attrId : group.getAttributes()) {
            attrs.add(attrId);
        }
        obj.add("attributes", attrs);
        return obj;
    }

    private static String operationToString(AttributeModifier.Operation op) {
        switch (op) {
            case ADD_VALUE: return "ADDITION";
            case ADD_MULTIPLIED_TOTAL: return "MULTIPLY_TOTAL";
            case ADD_MULTIPLIED_BASE:
            default: return "MULTIPLY_BASE";
        }
    }

    private static void initDefaults() {
        ENTRIES.clear();
        Set<String> all = Collections.emptySet();
        Set<String> noRpg = Set.of("pistol", "sniper", "rifle", "shotgun", "smg", "mg");
        Set<String> autoCapable = Set.of("rifle", "smg", "mg", "pistol");
        Set<String> burstCapable = Set.of("rifle", "smg", "pistol");
        Set<String> sniperRifle = Set.of("sniper", "rifle");
        Set<String> closeCombat = Set.of("pistol", "shotgun", "smg");

        AttributeModifier.Operation MULT = AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
        AttributeModifier.Operation ADD = AttributeModifier.Operation.ADD_VALUE;

        // === Core damage attributes ===
        add("tacz_attributes:gun_damage", -0.20, 0.30, MULT, 20, 1, all, 0.0, 500);
        add("tacz_attributes:headshot_multiplier", -0.15, 0.50, MULT, 10, 2, sniperRifle, 0.0, 500);

        // === Accuracy attributes ===
        add("tacz_attributes:hip_fire_accuracy", -0.20, 0.40, MULT, 15, 1, all, 0.0, 250);
        add("tacz_attributes:ads_accuracy", -0.10, 0.30, MULT, 15, 1, all, 0.0, 100);

        // === Recoil attributes (lower is better, so inverted scoring) ===
        add("tacz_attributes:recoil", -0.50, 0.50, MULT, 18, 1, all, 0.0, -200);
        add("tacz_attributes:vertical_recoil", -0.40, 0.40, MULT, 12, 2, all, 0.0, -200);
        add("tacz_attributes:horizontal_recoil", -0.20, 0.20, MULT, 12, 2, all, 0.0, -200);

        // === Speed attributes ===
        add("tacz_attributes:reload_speed", -0.15, 0.50, MULT, 18, 1, all, 0.0, 200);
        add("tacz_attributes:bolt_action_speed", -0.10, 0.25, MULT, 10, 2, Set.of("sniper", "shotgun"), 0.0, 400);
        add("tacz_attributes:draw_speed", -0.15, 0.30, MULT, 12, 1, all, 0.0, 100);
        add("tacz_attributes:ads_speed", -0.30, 0.40, MULT, 12, 1, all, 0.0, 100);
        add("tacz_attributes:rpm_multiplier", -0.10, 0.20, MULT, 8, 3, noRpg, 0.0, 750);
        add("tacz_attributes:gun_movement_speed", -0.10, 0.15, MULT, 10, 2, all, 0.0, 300);

        // === Magazine & ammo attributes ===
        add("tacz_attributes:magazine_capacity", -0.30, 0.30, MULT, 15, 2, noRpg, 0.0, 300);
        add("tacz_attributes:ammo_save_chance", 0.0, 0.15, ADD, 8, 3, noRpg, 0.0, 200);
        add("tacz_attributes:reload_ammo_save_chance", 0.0, 0.10, ADD, 6, 3, noRpg, 0.0, 1000);

        // === Kill recovery attributes (chance and amount/percent are linked pairs) ===
        addLinked("tacz_attributes:ammo_recovery_chance", 0.0, 0.20, ADD, 6, 3, noRpg, 0.0, 400, "tacz_attributes:ammo_recovery_amount");
        addLinked("tacz_attributes:ammo_recovery_amount", 0.0, 5.0, ADD, 5, 3, noRpg, 0.0, 10, "tacz_attributes:ammo_recovery_chance");
        addLinked("tacz_attributes:ammo_recovery_percent", 0.0, 0.50, ADD, 4, 4, noRpg, 0.0, 200, "tacz_attributes:ammo_recovery_chance");

        // === Bonus ammo (chance and amount are linked pairs) ===
        addLinked("tacz_attributes:bonus_ammo_chance", 0.0, 0.15, ADD, 5, 3, noRpg, 0.0, 600, "tacz_attributes:bonus_ammo_amount");
        addLinked("tacz_attributes:bonus_ammo_amount", 0.0, 5.0, ADD, 4, 3, noRpg, 0.0, 10, "tacz_attributes:bonus_ammo_chance");
        addLinked("tacz_attributes:bonus_ammo_percent", 0.0, 0.50, ADD, 4, 3, noRpg, 0.0, 200, "tacz_attributes:bonus_ammo_chance");

        // === Combat modifiers ===
        add("tacz_attributes:knockback_multiplier", 0.0, 1.40, MULT, 8, 2, all, 0.0, 40);
        add("tacz_attributes:knockback_base", 0.0, 1.2, ADD, 6, 2, closeCombat, 0.0, 50);
        add("tacz_attributes:pierce_multiplier", -0.10, 1.30, MULT, 6, 3, noRpg, 0.0, 100);

        // === Fire mode specific (auto) ===
        add("tacz_attributes:auto_damage", -0.10, 0.20, MULT, 8, 2, autoCapable, 0.0, 500);
        add("tacz_attributes:auto_accuracy", -0.15, 0.25, MULT, 8, 2, autoCapable, 0.0, 400);

        // === Fire mode specific (semi) ===
        add("tacz_attributes:semi_damage", -0.10, 0.20, MULT, 8, 2, all, 0.0, 500);
        add("tacz_attributes:semi_accuracy", -0.15, 0.25, MULT, 8, 2, all, 0.0, 400);

        // === Fire mode specific (burst) ===
        add("tacz_attributes:burst_damage", -0.10, 0.20, MULT, 6, 2, burstCapable, 0.0, 500);
        add("tacz_attributes:burst_accuracy", -0.15, 0.25, MULT, 6, 2, burstCapable, 0.0, 400);
        add("tacz_attributes:burst_speed", -0.10, 0.20, MULT, 5, 3, burstCapable, 0.0, 250);

        // === Stance specific ===
        add("tacz_attributes:hip_fire_damage", -0.10, 0.20, MULT, 8, 2, closeCombat, 0.0, 400);
        add("tacz_attributes:ads_damage", -0.10, 0.20, MULT, 8, 2, sniperRifle, 0.0, 500);

        // === Bullet amount ===
        add("tacz_attributes:semi_bullet_amount", 0.0, 0.50, MULT, 3, 4, Set.of("shotgun"), 0.0, 200);
        add("tacz_attributes:auto_bullet_amount", 0.0, 0.30, MULT, 3, 4, Set.of("shotgun"), 0.0, 200);
    }

    private static void add(String id, double min, double max, AttributeModifier.Operation op,
                            int weight, int rarityTier, Set<String> gunTypes, double buffThreshold, double scoreWeight) {
        ENTRIES.add(new AttributeEntry(id, min, max, op, weight, rarityTier, gunTypes, buffThreshold, scoreWeight));
    }

    private static void addLinked(String id, double min, double max, AttributeModifier.Operation op,
                                  int weight, int rarityTier, Set<String> gunTypes, double buffThreshold,
                                  double scoreWeight, String linkedAttribute) {
        ENTRIES.add(new AttributeEntry(id, min, max, op, weight, rarityTier, gunTypes, buffThreshold, scoreWeight, linkedAttribute));
    }
}
