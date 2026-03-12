package com.github.leopoko.tacz_attributes_addon.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.item.IGun;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import javax.annotation.Nullable;
import java.util.stream.Stream;

/**
 * Custom NeoForge ingredient that matches a TACZ gun by its GunId.
 * Used in recipes like: "center slot must be a Vector (tacz:vector)".
 */
public class GunIngredient implements ICustomIngredient {

    public static final MapCodec<GunIngredient> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("gun_id").forGetter(GunIngredient::getGunId)
    ).apply(builder, GunIngredient::new));

    private final ResourceLocation gunId;

    public GunIngredient(ResourceLocation gunId) {
        this.gunId = gunId;
    }

    public ResourceLocation getGunId() {
        return gunId;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        IGun iGun = IGun.getIGunOrNull(stack);
        if (iGun == null) return false;
        ResourceLocation id = iGun.getGunId(stack);
        return gunId.equals(id);
    }

    @Override
    public Stream<ItemStack> getItems() {
        // Provide the modern_kinetic_gun item as display hint
        return Stream.of(new ItemStack(
                BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("tacz", "modern_kinetic_gun"))
        ));
    }

    @Override
    public boolean isSimple() {
        return false; // NBT-sensitive
    }

    @Override
    public IngredientType<?> getType() {
        return ModIngredientTypes.GUN_INGREDIENT_TYPE.get();
    }
}
