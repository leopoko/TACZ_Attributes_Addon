package com.github.leopoko.tacz_attributes_addon.command;

import com.github.leopoko.tacz_attributes_addon.bridge.AttributeBridge;
import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.AttributeRegistry;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.github.leopoko.tacz_attributes_addon.data.GunModifier;
import com.github.leopoko.tacz_attributes_addon.handler.RarityHandler;
import com.github.leopoko.tacz_attributes_addon.handler.WeaponTypeHandler;
import com.github.leopoko.tacz_attributes_addon.random.AttributeGenerator;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.tacz.guns.api.item.IGun;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;

import java.util.List;

/**
 * Server commands for TACZ Attributes Addon.
 *
 * Commands:
 *   /taczaddon clear              - Remove all addon attributes from held gun
 *   /taczaddon clear random       - Remove only random attributes from held gun
 *   /taczaddon clear fixed        - Remove only fixed attributes from held gun
 *   /taczaddon reroll             - Reroll random attributes on held gun
 *   /taczaddon reload             - Reload attribute_pool.json and weapon_attributes.json
 *   /taczaddon info               - Show addon attribute info for held gun
 *   /taczaddon config get <key>   - Get a config value
 *   /taczaddon config set <key> <value> - Set a config value (temporary, until restart)
 */
public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("taczaddon")
                .requires(source -> source.hasPermission(2)) // OP level 2
                .then(Commands.literal("clear")
                        .executes(ctx -> clearAll(ctx.getSource()))
                        .then(Commands.literal("random")
                                .executes(ctx -> clearRandom(ctx.getSource())))
                        .then(Commands.literal("fixed")
                                .executes(ctx -> clearFixed(ctx.getSource()))))
                .then(Commands.literal("reroll")
                        .executes(ctx -> reroll(ctx.getSource())))
                .then(Commands.literal("reload")
                        .executes(ctx -> reload(ctx.getSource())))
                .then(Commands.literal("info")
                        .executes(ctx -> info(ctx.getSource())))
                .then(Commands.literal("config")
                        .then(Commands.literal("get")
                                .then(Commands.argument("key", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            for (String key : CONFIG_KEYS) {
                                                builder.suggest(key);
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> configGet(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "key")))))
                        .then(Commands.literal("set")
                                .then(Commands.argument("key", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            for (String key : CONFIG_KEYS) {
                                                builder.suggest(key);
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("value", StringArgumentType.greedyString())
                                                .executes(ctx -> configSet(ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "key"),
                                                        StringArgumentType.getString(ctx, "value")))))))
        );
    }

    // ========== Config keys ==========

    private static final String[] CONFIG_KEYS = {
            "enableRandomOnObtain", "enableWeaponTypeAttributes", "enableAttributeStation",
            "enableApotheosis", "enableRarityScoring",
            "randomMode", "fixedAttributeMode", "minAttributes", "maxAttributes",
            "valueDistribution", "distributionExponent", "raritySpreadFactor", "buffDebuffRatio",
            "uncommonThreshold", "rareThreshold", "epicThreshold",
            "processingTime", "consumeItem", "consumeItemId", "consumeCount",
            "allowReroll", "maxRerolls",
            "gunBaseSockets", "socketsScaleWithRarity",
            "commonSockets", "uncommonSockets", "rareSockets", "epicSockets"
    };

    // ========== Clear commands ==========

    private static int clearAll(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command must be run by a player"));
            return 0;
        }

        ItemStack held = player.getMainHandItem();
        if (held.isEmpty() || IGun.getIGunOrNull(held) == null) {
            source.sendFailure(Component.literal("You must be holding a TACZ gun"));
            return 0;
        }

        if (!GunAttributeData.hasAddonData(held)) {
            source.sendFailure(Component.literal("This gun has no addon attributes"));
            return 0;
        }

        GunAttributeData.removeAllAddonData(held);
        AttributeBridge.refreshPlayer(player);
        source.sendSuccess(() -> Component.literal("Removed all addon attributes from held gun")
                .withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static int clearRandom(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command must be run by a player"));
            return 0;
        }

        ItemStack held = player.getMainHandItem();
        if (held.isEmpty() || IGun.getIGunOrNull(held) == null) {
            source.sendFailure(Component.literal("You must be holding a TACZ gun"));
            return 0;
        }

        GunAttributeData.setModifiers(held, List.of());

        // Recalculate rarity based on remaining fixed modifiers
        if (CommonConfig.ENABLE_RARITY_SCORING.get()) {
            RarityHandler.calculateAndApplyRarity(held);
        }

        AttributeBridge.refreshPlayer(player);
        source.sendSuccess(() -> Component.literal("Removed random attributes from held gun")
                .withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static int clearFixed(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command must be run by a player"));
            return 0;
        }

        ItemStack held = player.getMainHandItem();
        if (held.isEmpty() || IGun.getIGunOrNull(held) == null) {
            source.sendFailure(Component.literal("You must be holding a TACZ gun"));
            return 0;
        }

        GunAttributeData.setFixedModifiers(held, List.of());

        // Recalculate rarity based on remaining random modifiers
        if (CommonConfig.ENABLE_RARITY_SCORING.get()) {
            RarityHandler.calculateAndApplyRarity(held);
        }

        AttributeBridge.refreshPlayer(player);
        source.sendSuccess(() -> Component.literal("Removed fixed attributes from held gun")
                .withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    // ========== Reroll command ==========

    private static int reroll(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command must be run by a player"));
            return 0;
        }

        ItemStack held = player.getMainHandItem();
        if (held.isEmpty() || IGun.getIGunOrNull(held) == null) {
            source.sendFailure(Component.literal("You must be holding a TACZ gun"));
            return 0;
        }

        RandomSource random = player.getRandom();
        CommonConfig.FixedAttributeMode fixedMode = CommonConfig.FIXED_ATTRIBUTE_MODE.get();
        boolean applyRandom = fixedMode != CommonConfig.FixedAttributeMode.FIXED_ONLY;

        if (applyRandom) {
            List<GunModifier> modifiers = AttributeGenerator.generate(held, random);
            GunAttributeData.setModifiers(held, modifiers);
        }

        // Always re-apply fixed attributes from config (coexist with random)
        if (CommonConfig.ENABLE_WEAPON_TYPE_ATTRIBUTES.get()) {
            WeaponTypeHandler.applyFixedAttributes(held);
        }

        if (CommonConfig.ENABLE_RARITY_SCORING.get()) {
            RarityHandler.calculateAndApplyRarity(held);
        }

        GunAttributeData.setSealed(held, true);

        AttributeBridge.refreshPlayer(player);
        source.sendSuccess(() -> Component.literal("Rerolled attributes on held gun")
                .withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    // ========== Reload command ==========

    private static int reload(CommandSourceStack source) {
        AttributeRegistry.reloadConfig(FMLPaths.CONFIGDIR.get());
        WeaponTypeHandler.loadConfig(FMLPaths.CONFIGDIR.get());
        source.sendSuccess(() -> Component.literal("Reloaded attribute_pool.json and weapon_attributes.json")
                .withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    // ========== Info command ==========

    private static int info(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command must be run by a player"));
            return 0;
        }

        ItemStack held = player.getMainHandItem();
        if (held.isEmpty() || IGun.getIGunOrNull(held) == null) {
            source.sendFailure(Component.literal("You must be holding a TACZ gun"));
            return 0;
        }

        if (!GunAttributeData.hasAddonData(held)) {
            source.sendSuccess(() -> Component.literal("This gun has no addon attributes")
                    .withStyle(ChatFormatting.YELLOW), false);
            return 1;
        }

        int score = GunAttributeData.getScore(held);
        int rarityOrd = GunAttributeData.getRarityOrdinal(held);
        int rerolls = GunAttributeData.getRerollCount(held);
        boolean sealed = GunAttributeData.isSealed(held);

        source.sendSuccess(() -> Component.literal("=== TACZ Addon Gun Info ===")
                .withStyle(ChatFormatting.GOLD), false);
        source.sendSuccess(() -> Component.literal("Score: " + score + " | Rarity: " + rarityOrd
                + " | Rerolls: " + rerolls + " | Sealed: " + sealed)
                .withStyle(ChatFormatting.GRAY), false);

        List<GunModifier> randomMods = GunAttributeData.getModifiers(held);
        if (!randomMods.isEmpty()) {
            source.sendSuccess(() -> Component.literal("Random Modifiers:")
                    .withStyle(ChatFormatting.GOLD), false);
            for (GunModifier mod : randomMods) {
                String val = String.format("%+.2f", mod.getValue());
                source.sendSuccess(() -> Component.literal("  " + mod.getAttributeId() + ": " + val
                        + " (" + mod.getOperation().name() + ")")
                        .withStyle(ChatFormatting.GRAY), false);
            }
        }

        List<GunModifier> fixedMods = GunAttributeData.getFixedModifiers(held);
        if (!fixedMods.isEmpty()) {
            source.sendSuccess(() -> Component.literal("Fixed Modifiers:")
                    .withStyle(ChatFormatting.BLUE), false);
            for (GunModifier mod : fixedMods) {
                String val = String.format("%+.2f", mod.getValue());
                source.sendSuccess(() -> Component.literal("  " + mod.getAttributeId() + ": " + val
                        + " (" + mod.getOperation().name() + ")")
                        .withStyle(ChatFormatting.GRAY), false);
            }
        }

        // Show gem/socket info if Apotheosis is present
        showGemInfo(source, held);

        return 1;
    }

    private static void showGemInfo(CommandSourceStack source, ItemStack held) {
        try {
            if (!com.github.leopoko.tacz_attributes_addon.compat.apotheosis.ApotheosisCompat.isApotheosisPresent()) return;
            com.github.leopoko.tacz_attributes_addon.compat.apotheosis.GemInfoHelper.showInfo(source, held);
        } catch (NoClassDefFoundError ignored) {
            // Apotheosis classes not available
        }
    }

    // ========== Config commands ==========

    private static int configGet(CommandSourceStack source, String key) {
        String value = getConfigValue(key);
        if (value == null) {
            source.sendFailure(Component.literal("Unknown config key: " + key));
            return 0;
        }
        source.sendSuccess(() -> Component.literal(key + " = " + value)
                .withStyle(ChatFormatting.AQUA), false);
        return 1;
    }

    private static int configSet(CommandSourceStack source, String key, String value) {
        boolean success = setConfigValue(key, value);
        if (!success) {
            source.sendFailure(Component.literal("Failed to set " + key + " = " + value
                    + " (unknown key or invalid value)"));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Set " + key + " = " + value
                + " (temporary, resets on restart)")
                .withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static String getConfigValue(String key) {
        switch (key) {
            case "enableRandomOnObtain": return String.valueOf(CommonConfig.ENABLE_RANDOM_ON_OBTAIN.get());
            case "enableWeaponTypeAttributes": return String.valueOf(CommonConfig.ENABLE_WEAPON_TYPE_ATTRIBUTES.get());
            case "enableAttributeStation": return String.valueOf(CommonConfig.ENABLE_ATTRIBUTE_STATION.get());
            case "enableApotheosis": return String.valueOf(CommonConfig.ENABLE_APOTHEOSIS.get());
            case "enableRarityScoring": return String.valueOf(CommonConfig.ENABLE_RARITY_SCORING.get());
            case "randomMode": return CommonConfig.RANDOM_MODE.get().name();
            case "fixedAttributeMode": return CommonConfig.FIXED_ATTRIBUTE_MODE.get().name();
            case "minAttributes": return String.valueOf(CommonConfig.MIN_ATTRIBUTES.get());
            case "maxAttributes": return String.valueOf(CommonConfig.MAX_ATTRIBUTES.get());
            case "valueDistribution": return CommonConfig.VALUE_DISTRIBUTION.get().name();
            case "distributionExponent": return String.valueOf(CommonConfig.DISTRIBUTION_EXPONENT.get());
            case "raritySpreadFactor": return String.valueOf(CommonConfig.RARITY_SPREAD_FACTOR.get());
            case "buffDebuffRatio": return String.valueOf(CommonConfig.BUFF_DEBUFF_RATIO.get());
            case "uncommonThreshold": return String.valueOf(CommonConfig.UNCOMMON_THRESHOLD.get());
            case "rareThreshold": return String.valueOf(CommonConfig.RARE_THRESHOLD.get());
            case "epicThreshold": return String.valueOf(CommonConfig.EPIC_THRESHOLD.get());
            case "processingTime": return String.valueOf(CommonConfig.STATION_PROCESSING_TIME.get());
            case "consumeItem": return String.valueOf(CommonConfig.STATION_CONSUME_ITEM.get());
            case "consumeItemId": return CommonConfig.STATION_CONSUME_ITEM_ID.get();
            case "consumeCount": return String.valueOf(CommonConfig.STATION_CONSUME_COUNT.get());
            case "allowReroll": return String.valueOf(CommonConfig.STATION_ALLOW_REROLL.get());
            case "maxRerolls": return String.valueOf(CommonConfig.STATION_MAX_REROLLS.get());
            case "gunBaseSockets": return String.valueOf(CommonConfig.GUN_BASE_SOCKETS.get());
            case "socketsScaleWithRarity": return String.valueOf(CommonConfig.SOCKETS_SCALE_WITH_RARITY.get());
            case "commonSockets": return String.valueOf(CommonConfig.COMMON_SOCKETS.get());
            case "uncommonSockets": return String.valueOf(CommonConfig.UNCOMMON_SOCKETS.get());
            case "rareSockets": return String.valueOf(CommonConfig.RARE_SOCKETS.get());
            case "epicSockets": return String.valueOf(CommonConfig.EPIC_SOCKETS.get());
            default: return null;
        }
    }

    private static boolean setConfigValue(String key, String value) {
        try {
            switch (key) {
                case "enableRandomOnObtain": CommonConfig.ENABLE_RANDOM_ON_OBTAIN.set(Boolean.parseBoolean(value)); return true;
                case "enableWeaponTypeAttributes": CommonConfig.ENABLE_WEAPON_TYPE_ATTRIBUTES.set(Boolean.parseBoolean(value)); return true;
                case "enableAttributeStation": CommonConfig.ENABLE_ATTRIBUTE_STATION.set(Boolean.parseBoolean(value)); return true;
                case "enableApotheosis": CommonConfig.ENABLE_APOTHEOSIS.set(Boolean.parseBoolean(value)); return true;
                case "enableRarityScoring": CommonConfig.ENABLE_RARITY_SCORING.set(Boolean.parseBoolean(value)); return true;
                case "randomMode": CommonConfig.RANDOM_MODE.set(CommonConfig.RandomMode.valueOf(value.toUpperCase())); return true;
                case "fixedAttributeMode": CommonConfig.FIXED_ATTRIBUTE_MODE.set(CommonConfig.FixedAttributeMode.valueOf(value.toUpperCase())); return true;
                case "minAttributes": CommonConfig.MIN_ATTRIBUTES.set(Integer.parseInt(value)); return true;
                case "maxAttributes": CommonConfig.MAX_ATTRIBUTES.set(Integer.parseInt(value)); return true;
                case "valueDistribution": CommonConfig.VALUE_DISTRIBUTION.set(CommonConfig.DistributionType.valueOf(value.toUpperCase())); return true;
                case "distributionExponent": CommonConfig.DISTRIBUTION_EXPONENT.set(Double.parseDouble(value)); return true;
                case "raritySpreadFactor": CommonConfig.RARITY_SPREAD_FACTOR.set(Double.parseDouble(value)); return true;
                case "buffDebuffRatio": CommonConfig.BUFF_DEBUFF_RATIO.set(Double.parseDouble(value)); return true;
                case "uncommonThreshold": CommonConfig.UNCOMMON_THRESHOLD.set(Integer.parseInt(value)); return true;
                case "rareThreshold": CommonConfig.RARE_THRESHOLD.set(Integer.parseInt(value)); return true;
                case "epicThreshold": CommonConfig.EPIC_THRESHOLD.set(Integer.parseInt(value)); return true;
                case "processingTime": CommonConfig.STATION_PROCESSING_TIME.set(Integer.parseInt(value)); return true;
                case "consumeItem": CommonConfig.STATION_CONSUME_ITEM.set(Boolean.parseBoolean(value)); return true;
                case "consumeItemId": CommonConfig.STATION_CONSUME_ITEM_ID.set(value); return true;
                case "consumeCount": CommonConfig.STATION_CONSUME_COUNT.set(Integer.parseInt(value)); return true;
                case "allowReroll": CommonConfig.STATION_ALLOW_REROLL.set(Boolean.parseBoolean(value)); return true;
                case "maxRerolls": CommonConfig.STATION_MAX_REROLLS.set(Integer.parseInt(value)); return true;
                case "gunBaseSockets": CommonConfig.GUN_BASE_SOCKETS.set(Integer.parseInt(value)); return true;
                case "socketsScaleWithRarity": CommonConfig.SOCKETS_SCALE_WITH_RARITY.set(Boolean.parseBoolean(value)); return true;
                case "commonSockets": CommonConfig.COMMON_SOCKETS.set(Integer.parseInt(value)); return true;
                case "uncommonSockets": CommonConfig.UNCOMMON_SOCKETS.set(Integer.parseInt(value)); return true;
                case "rareSockets": CommonConfig.RARE_SOCKETS.set(Integer.parseInt(value)); return true;
                case "epicSockets": CommonConfig.EPIC_SOCKETS.set(Integer.parseInt(value)); return true;
                default: return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
