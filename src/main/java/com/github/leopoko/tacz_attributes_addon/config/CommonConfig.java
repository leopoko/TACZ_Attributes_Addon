package com.github.leopoko.tacz_attributes_addon.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public static final ForgeConfigSpec SPEC;

    // Feature toggles
    public static final ForgeConfigSpec.BooleanValue ENABLE_RANDOM_ON_OBTAIN;
    public static final ForgeConfigSpec.BooleanValue ENABLE_WEAPON_TYPE_ATTRIBUTES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_ATTRIBUTE_STATION;
    public static final ForgeConfigSpec.BooleanValue ENABLE_APOTHEOSIS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_RARITY_SCORING;

    // Random mode
    public static final ForgeConfigSpec.EnumValue<RandomMode> RANDOM_MODE;

    // Fixed attribute mode
    public static final ForgeConfigSpec.EnumValue<FixedAttributeMode> FIXED_ATTRIBUTE_MODE;

    // Random attribute count
    public static final ForgeConfigSpec.IntValue MIN_ATTRIBUTES;
    public static final ForgeConfigSpec.IntValue MAX_ATTRIBUTES;

    // Value distribution
    public static final ForgeConfigSpec.EnumValue<DistributionType> VALUE_DISTRIBUTION;
    public static final ForgeConfigSpec.DoubleValue DISTRIBUTION_EXPONENT;

    // Rarity settings
    public static final ForgeConfigSpec.DoubleValue RARITY_SPREAD_FACTOR;
    public static final ForgeConfigSpec.DoubleValue BUFF_DEBUFF_RATIO;

    // Rarity score thresholds
    public static final ForgeConfigSpec.IntValue UNCOMMON_THRESHOLD;
    public static final ForgeConfigSpec.IntValue RARE_THRESHOLD;
    public static final ForgeConfigSpec.IntValue EPIC_THRESHOLD;

    // Attribute Station settings
    public static final ForgeConfigSpec.IntValue STATION_PROCESSING_TIME;
    public static final ForgeConfigSpec.BooleanValue STATION_CONSUME_ITEM;
    public static final ForgeConfigSpec.ConfigValue<String> STATION_CONSUME_ITEM_ID;
    public static final ForgeConfigSpec.IntValue STATION_CONSUME_COUNT;
    public static final ForgeConfigSpec.BooleanValue STATION_ALLOW_REROLL;
    public static final ForgeConfigSpec.IntValue STATION_MAX_REROLLS;

    // Apotheosis socket settings
    public static final ForgeConfigSpec.IntValue GUN_BASE_SOCKETS;
    public static final ForgeConfigSpec.BooleanValue SOCKETS_SCALE_WITH_RARITY;
    public static final ForgeConfigSpec.IntValue COMMON_SOCKETS;
    public static final ForgeConfigSpec.IntValue UNCOMMON_SOCKETS;
    public static final ForgeConfigSpec.IntValue RARE_SOCKETS;
    public static final ForgeConfigSpec.IntValue EPIC_SOCKETS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

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
                .defineInRange("maxAttributes", 4, 0, 20);

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
                .defineInRange("uncommonThreshold", 100, 0, Integer.MAX_VALUE);

        RARE_THRESHOLD = builder
                .comment("Minimum score for RARE rarity")
                .defineInRange("rareThreshold", 300, 0, Integer.MAX_VALUE);

        EPIC_THRESHOLD = builder
                .comment("Minimum score for EPIC rarity")
                .defineInRange("epicThreshold", 600, 0, Integer.MAX_VALUE);

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
