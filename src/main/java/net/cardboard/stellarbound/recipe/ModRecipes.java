package net.cardboard.stellarbound.recipe;

import net.cardboard.stellarbound.Stellarbound;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Stellarbound.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Stellarbound.MOD_ID);

    public static final RegistryObject<RecipeSerializer<InfuseForgeryRecipe>> INFUSE_FORGERY_SERIALIZER =
            RECIPE_SERIALIZERS.register("infuse_forgery", InfuseForgeryRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<InfuseForgeryRecipe>> INFUSE_FORGERY_TYPE =
            RECIPE_TYPES.register("infuse_forgery", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return Stellarbound.id("infuse_forgery").toString();
                }
            });

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}