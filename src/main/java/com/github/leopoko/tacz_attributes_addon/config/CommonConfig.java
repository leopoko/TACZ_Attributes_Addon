package com.github.leopoko.tacz_attributes_addon.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {
    public static final ModConfigSpec SPEC;

    // Feature toggles
    public static final ModConfigSpec.BooleanValue ENABLE_RANDOM_ON_OBTAIN;
    public static final ModConfigSpec.BooleanValue ENABLE_WEAPON_TYPE_ATTRIBUTES;
    public static final ModConfigSpec.BooleanValue ENABLE_ATTRIBUTE_STATION;
    public static final ModConfigSpec.BooleanValue ENABLE_APOTHEOSIS;
    public static final ModConfigSpec.BooleanValue ENABLE_RARITY_SCORING;
    public static final ModConfigSpec.BooleanValue SHOW_EMPTY_SLOTS;

    // Random mode
    public static final ModConfigSpec.EnumValue<RandomMode> RANDOM_MODE;

    // Fixed attribute mode
    public static final ModConfigSpec.EnumValue<FixedAttributeMode> FIXED_ATTRIBUTE_MODE;

    // Random attribute count
    public static final ModConfigSpec.IntValue MIN_ATTRIBUTES;
    public static final ModConfigSpec.IntValue MAX_ATTRIBUTES;

    // Value distribution
    public static final ModConfigSpec.EnumValue<DistributionType> VALUE_DISTRIBUTION;
    public static final ModConfigSpec.DoubleValue DISTRIBUTION_EXPONENT;

    // Rarity settings
    public static final ModConfigSpec.DoubleValue RARITY_SPREAD_FACTOR;
    public static final ModConfigSpec.DoubleValue BUFF_DEBUFF_RATIO;

    // Rarity score thresholds
    public static final ModConfigSpec.IntValue UNCOMMON_THRESHOLD;
    public static final ModConfigSpec.IntValue RARE_THRESHOLD;
    public static final ModConfigSpec.IntValue EPIC_THRESHOLD;

    // Attribute Station settings
    public static final ModConfigSpec.IntValue STATION_PROCESSING_TIME;
    public static final ModConfigSpec.BooleanValue STATION_CONSUME_ITEM;
    public static final ModConfigSpec.ConfigValue<String> STATION_CONSUME_ITEM_ID;
    public static final ModConfigSpec.IntValue STATION_CONSUME_COUNT;
    public static final ModConfigSpec.BooleanValue STATION_ALLOW_REROLL;
    public static final ModConfigSpec.IntValue STATION_MAX_REROLLS;

    // Apotheosis socket settings
    public static final ModConfigSpec.IntValue GUN_BASE_SOCKETS;
    public static final ModConfigSpec.BooleanValue SOCKETS_SCALE_WITH_RARITY;
    public static final ModConfigSpec.IntValue COMMON_SOCKETS;
    public static final ModConfigSpec.IntValue UNCOMMON_SOCKETS;
    public static final ModConfigSpec.IntValue RARE_SOCKETS;
    public static final ModConfigSpec.IntValue EPIC_SOCKETS;

    // Enhancement Station settings
    public static final ModConfigSpec.BooleanValue ENABLE_ENHANCEMENT_STATION;
    public static final ModConfigSpec.ConfigValue<String> ENHANCEMENT_MATERIAL_ID;
    public static final ModConfigSpec.IntValue ENHANCEMENT_APPLY_COST;
    public static final ModConfigSpec.IntValue ENHANCEMENT_REROLL_COST;
    public static final ModConfigSpec.IntValue ENHANCEMENT_CHOICE_COUNT;
    public static final ModConfigSpec.IntValue ENHANCEMENT_MAX_COUNT;
    public static final ModConfigSpec.BooleanValue ENHANCEMENT_COST_SCALING;
    public static final ModConfigSpec.IntValue ENHANCEMENT_COST_SCALING_AMOUNT;
    public static final ModConfigSpec.DoubleValue ENHANCEMENT_MIN_VALUE;
    public static final ModConfigSpec.DoubleValue ENHANCEMENT_MAX_VALUE;
    public static final ModConfigSpec.BooleanValue ENHANCEMENT_ONLY_POSITIVE;
    public static final ModConfigSpec.IntValue ENHANCEMENT_MAX_TYPES;
    public static final ModConfigSpec.BooleanValue ENHANCEMENT_EXISTING_ONLY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("TACZ Attributes Addon Configuration").push("general");

        ENABLE_RANDOM_ON_OBTAIN = builder
                .comment("Apply random attributes when guns are obtained (crafted/picked up)")
                .define("enableRandomOnObtain", true);

        ENABLE_WEAPON_TYPE_ATTRIBUTES = builder
                .comment("Apply per-weapon-model fixed attributes (configured in weapon_attributes.json)")
                .define("enableWeaponTypeAttributes", true);

        ENABLE_ATTRIBUTE_STATION = builder
                .comment("Enable the Attribute Station block for applying random attributes")
                .define("enableAttributeStation", true);

        ENABLE_APOTHEOSIS = builder
                .comment("Enable Apotheosis integration (gem/socket system for guns)")
                .define("enableApotheosis", true);

        ENABLE_RARITY_SCORING = builder
                .comment("Enable attribute scoring and rarity display")
                .define("enableRarityScoring", true);

        SHOW_EMPTY_SLOTS = builder
                .comment("Show empty attribute slots in tooltip when current attributes < maxAttributes")
                .define("showEmptySlots", false);

        builder.pop();

        builder.comment("Random Attribute Generation").push("random");

        RANDOM_MODE = builder
                .comment("Random mode: FULL_RANDOM, ADAPTIVE, RARITY_ADAPTIVE, BALANCED")
                .defineEnum("randomMode", RandomMode.RARITY_ADAPTIVE);

        FIXED_ATTRIBUTE_MODE = builder
                .comment("How fixed (per-weapon) and random attributes interact",
                         "FIXED_ONLY: Only fixed attributes, no random",
                         "RANDOM_ONLY: Only random, fixed config ignored",
                         "BOTH_STACKING: Both applied, stacking multiplicatively",
                         "FIXED_INFLUENCES_RANDOM: Fixed attributes modify random pool weights")
                .defineEnum("fixedAttributeMode", FixedAttributeMode.BOTH_STACKING);

        MIN_ATTRIBUTES = builder
                .comment("Minimum number of random attributes per gun")
                .defineInRange("minAttributes", 1, 0, 20);

        MAX_ATTRIBUTES = builder
                .comment("Maximum number of random attributes per gun")
                .defineInRange("maxAttributes", 5, 0, 20);

        VALUE_DISTRIBUTION = builder
                .comment("Value distribution curve: LINEAR, EXPONENTIAL, QUADRATIC")
                .defineEnum("valueDistribution", DistributionType.EXPONENTIAL);

        DISTRIBUTION_EXPONENT = builder
                .comment("Exponent for EXPONENTIAL distribution (higher = more skewed toward low values)")
                .defineInRange("distributionExponent", 2.0, 1.0, 10.0);

        RARITY_SPREAD_FACTOR = builder
                .comment("How spread out rarity weights are (higher = rarer attributes are much rarer)")
                .defineInRange("raritySpreadFactor", 2.0, 1.0, 10.0);

        BUFF_DEBUFF_RATIO = builder
                .comment("Target buff/debuff ratio for BALANCED mode (1.0 = equal, 2.0 = twice as many buffs)")
                .defineInRange("buffDebuffRatio", 1.0, 0.1, 5.0);

        builder.pop();

        builder.comment("Rarity Score Thresholds").push("rarity");

        UNCOMMON_THRESHOLD = builder
                .comment("Minimum score for UNCOMMON rarity")
                .defineInRange("uncommonThreshold", 50, 0, Integer.MAX_VALUE);

        RARE_THRESHOLD = builder
                .comment("Minimum score for RARE rarity")
                .defineInRange("rareThreshold", 70, 0, Integer.MAX_VALUE);

        EPIC_THRESHOLD = builder
                .comment("Minimum score for EPIC rarity")
                .defineInRange("epicThreshold", 100, 0, Integer.MAX_VALUE);

        builder.pop();

        builder.comment("Attribute Station Block").push("station");

        STATION_PROCESSING_TIME = builder
                .comment("Processing time in ticks (20 ticks = 1 second)")
                .defineInRange("processingTime", 200, 1, 72000);

        STATION_CONSUME_ITEM = builder
                .comment("Whether the station consumes an item during processing")
                .define("consumeItem", false);

        STATION_CONSUME_ITEM_ID = builder
                .comment("Item ID to consume (e.g., 'minecraft:diamond'), only used if consumeItem is true")
                .define("consumeItemId", "minecraft:diamond");

        STATION_CONSUME_COUNT = builder
                .comment("Number of items to consume per processing")
                .defineInRange("consumeCount", 1, 1, 64);

        STATION_ALLOW_REROLL = builder
                .comment("Allow re-rolling attributes on guns that already have them")
                .define("allowReroll", true);

        STATION_MAX_REROLLS = builder
                .comment("Maximum number of rerolls allowed per gun (0 = unlimited)",
                         "Once a gun reaches this limit, it cannot be rerolled at the station")
                .defineInRange("maxRerolls", 0, 0, 1000);

        builder.pop();

        builder.comment("Apotheosis Integration (requires Apotheosis mod)").push("apotheosis");

        GUN_BASE_SOCKETS = builder
                .comment("Base number of sockets for guns (used when socketsScaleWithRarity is false)")
                .defineInRange("gunBaseSockets", 2, 0, 6);

        SOCKETS_SCALE_WITH_RARITY = builder
                .comment("Whether socket count scales with the gun's rarity")
                .define("socketsScaleWithRarity", true);

        COMMON_SOCKETS = builder
                .comment("Socket count for COMMON rarity guns")
                .defineInRange("commonSockets", 1, 0, 6);

        UNCOMMON_SOCKETS = builder
                .comment("Socket count for UNCOMMON rarity guns")
                .defineInRange("uncommonSockets", 2, 0, 6);

        RARE_SOCKETS = builder
                .comment("Socket count for RARE rarity guns")
                .defineInRange("rareSockets", 3, 0, 6);

        EPIC_SOCKETS = builder
                .comment("Socket count for EPIC rarity guns")
                .defineInRange("epicSockets", 4, 0, 6);

        builder.pop();

        builder.comment("Enhancement Station Block").push("enhancement");

        ENABLE_ENHANCEMENT_STATION = builder
                .comment("Enable the Enhancement Station block for selectively enhancing gun attributes")
                .define("enableEnhancementStation", true);

        ENHANCEMENT_MATERIAL_ID = builder
                .comment("Material item ID required for enhancements (e.g., 'minecraft:diamond')")
                .define("materialId", "minecraft:diamond");

        ENHANCEMENT_APPLY_COST = builder
                .comment("Number of materials consumed when applying an enhancement")
                .defineInRange("applyCost", 1, 1, 64);

        ENHANCEMENT_REROLL_COST = builder
                .comment("Number of materials consumed when rerolling choices")
                .defineInRange("rerollCost", 1, 1, 64);

        ENHANCEMENT_CHOICE_COUNT = builder
                .comment("Number of enhancement choices shown to the player")
                .defineInRange("choiceCount", 3, 1, 10);

        ENHANCEMENT_MAX_COUNT = builder
                .comment("Maximum number of enhancements per gun (0 = unlimited)")
                .defineInRange("maxEnhancements", 10, 0, 100);

        ENHANCEMENT_COST_SCALING = builder
                .comment("Whether material cost increases with each enhancement level")
                .define("costScaling", false);

        ENHANCEMENT_COST_SCALING_AMOUNT = builder
                .comment("Additional materials per enhancement level when cost scaling is enabled")
                .defineInRange("costScalingAmount", 1, 1, 10);

        ENHANCEMENT_MIN_VALUE = builder
                .comment("Minimum enhancement value as a fraction of the attribute's range")
                .defineInRange("minValueMultiplier", 0.01, 0.001, 1.0);

        ENHANCEMENT_MAX_VALUE = builder
                .comment("Maximum enhancement value as a fraction of the attribute's range")
                .defineInRange("maxValueMultiplier", 0.10, 0.001, 1.0);

        ENHANCEMENT_ONLY_POSITIVE = builder
                .comment("If true, enhancements are always positive (buffs only)")
                .define("onlyPositive", true);

        ENHANCEMENT_MAX_TYPES = builder
                .comment("Maximum number of distinct enhancement attribute types per gun (0 = unlimited)",
                         "When reached, only already-enhanced attributes appear as choices")
                .defineInRange("maxTypes", 0, 0, 100);

        ENHANCEMENT_EXISTING_ONLY = builder
                .comment("If true, enhancement choices are restricted to attributes already on the gun",
                         "(random + fixed + enhanced modifiers)")
                .define("existingOnly", false);

        builder.pop();

        SPEC = builder.build();
    }

    public enum RandomMode {
        FULL_RANDOM,
        ADAPTIVE,
        RARITY_ADAPTIVE,
        BALANCED
    }

    public enum FixedAttributeMode {
        FIXED_ONLY,
        RANDOM_ONLY,
        BOTH_STACKING,
        FIXED_INFLUENCES_RANDOM
    }

    public enum DistributionType {
        LINEAR,
        EXPONENTIAL,
        QUADRATIC
    }
}
