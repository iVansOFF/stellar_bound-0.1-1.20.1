package net.cardboard.stellarbound.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class InfuseForgeryRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> ingredients;
    private final int processingTime;

    public InfuseForgeryRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> ingredients, int processingTime) {
        this.id = id;
        this.output = output;
        this.ingredients = ingredients;
        this.processingTime = processingTime;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        // Verificar que los 3 slots de input contengan los ingredientes correctos
        // Nota: Esta implementación asume orden específico. Puedes modificarla para ser más flexible
        if (level.isClientSide()) return false;

        // Asegurarse de que haya exactamente 3 ingredientes
        if (ingredients.size() != 3) return false;

        return ingredients.get(0).test(container.getItem(0)) &&
                ingredients.get(1).test(container.getItem(1)) &&
                ingredients.get(2).test(container.getItem(2));
    }

    @Override
    public @NotNull ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    public int getProcessingTime() {
        return processingTime;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.INFUSE_FORGERY_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.INFUSE_FORGERY_TYPE.get();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    // Serializador para JSON y Network
    public static class Serializer implements RecipeSerializer<InfuseForgeryRecipe> {
        @Override
        public @NotNull InfuseForgeryRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            ItemStack output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "output"), true);

            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(3, Ingredient.EMPTY);

            for (int i = 0; i < Math.min(ingredients.size(), 3); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            int processingTime = GsonHelper.getAsInt(json, "processingTime", 100);

            return new InfuseForgeryRecipe(recipeId, output, inputs, processingTime);
        }

        @Nullable
        @Override
        public InfuseForgeryRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(3, Ingredient.EMPTY);

            for (int i = 0; i < 3; i++) {
                inputs.set(i, Ingredient.fromNetwork(buffer));
            }

            ItemStack output = buffer.readItem();
            int processingTime = buffer.readInt();

            return new InfuseForgeryRecipe(recipeId, output, inputs, processingTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, InfuseForgeryRecipe recipe) {
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buffer);
            }
            buffer.writeItem(recipe.output);
            buffer.writeInt(recipe.processingTime);
        }
    }
}