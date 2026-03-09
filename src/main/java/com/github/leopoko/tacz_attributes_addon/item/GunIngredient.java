package com.github.leopoko.tacz_attributes_addon.item;

import com.google.gson.JsonObject;
import com.tacz.guns.api.item.IGun;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.stream.Stream;

/**
 * Custom Forge ingredient that matches a TACZ gun by its GunId NBT.
 * Used in recipes like: "center slot must be a Vector (tacz:vector)".
 */
public class GunIngredient extends AbstractIngredient {

    public static final ResourceLocation TYPE_ID =
            new ResourceLocation("tacz_attributes_addon", "gun");

    private final ResourceLocation gunId;

    public GunIngredient(ResourceLocation gunId) {
        super(Stream.of(new Ingredient.ItemValue(new ItemStack(
                ForgeRegistries.ITEMS.getValue(new ResourceLocation("tacz", "modern_kinetic_gun"))
        ))));
        this.gunId = gunId;
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
    public boolean isSimple() {
        return false; // NBT-sensitive
    }

    @Override
    public IIngredientSerializer<? extends GunIngredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", TYPE_ID.toString());
        json.addProperty("gun_id", gunId.toString());
        return json;
    }

    /**
     * Register this ingredient type with Forge's CraftingHelper.
     * Call during mod construction or common setup.
     */
    public static void register() {
        CraftingHelper.register(TYPE_ID, Serializer.INSTANCE);
    }

    public static class Serializer implements IIngredientSerializer<GunIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public GunIngredient parse(FriendlyByteBuf buffer) {
            ResourceLocation gunId = buffer.readResourceLocation();
            return new GunIngredient(gunId);
        }

        @Override
        public GunIngredient parse(JsonObject json) {
            ResourceLocation gunId = new ResourceLocation(json.get("gun_id").getAsString());
            return new GunIngredient(gunId);
        }

        @Override
        public void write(FriendlyByteBuf buffer, GunIngredient ingredient) {
            buffer.writeResourceLocation(ingredient.gunId);
        }
    }
}
