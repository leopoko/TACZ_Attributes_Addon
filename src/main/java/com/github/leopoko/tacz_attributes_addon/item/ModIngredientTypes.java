package com.github.leopoko.tacz_attributes_addon.item;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModIngredientTypes {
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, TaczAttributesAddon.MODID);

    public static final DeferredHolder<IngredientType<?>, IngredientType<GunIngredient>> GUN_INGREDIENT_TYPE =
            INGREDIENT_TYPES.register("gun", () -> new IngredientType<>(GunIngredient.CODEC));
}
