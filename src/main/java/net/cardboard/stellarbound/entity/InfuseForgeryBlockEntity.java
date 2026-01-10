package net.cardboard.stellarbound.entity;

import net.cardboard.stellarbound.base.BaseProcessingBlockEntity;
import net.cardboard.stellarbound.recipe.InfuseForgeryRecipe;
import net.cardboard.stellarbound.recipe.ModRecipes;
import net.cardboard.stellarbound.registry.ModBlockEntities;
import net.cardboard.stellarbound.screen.InfuseForgeryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Optional;

public class InfuseForgeryBlockEntity extends BaseProcessingBlockEntity {

    public InfuseForgeryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INFUSE_FORGERY.get(), pos, state, 4, 100);
    }

    // Método static para el ticker
    public static void tick(Level level, BlockPos pos, BlockState state, InfuseForgeryBlockEntity blockEntity) {
        BaseProcessingBlockEntity.tick(level, pos, state, blockEntity);
    }

    @Override
    protected boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot != 3; // Slot 3 es el output
    }

    @Override
    protected Component getDefaultDisplayName() {
        return Component.translatable("block.stellarbound.infuse_forgery");
    }

    @Nullable
    @Override
    protected AbstractContainerMenu createMenuInternal(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new InfuseForgeryMenu(containerId, playerInventory, this, this.data);
    }

    @Override
    protected boolean hasRecipe() {
        Level level = this.level;
        if (level == null || level.isClientSide()) return false;

        SimpleContainer inventory = new SimpleContainer(3); // Solo 3 slots para la receta
        for (int i = 0; i < 3; i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }

        Optional<InfuseForgeryRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.INFUSE_FORGERY_TYPE.get(), inventory, level);

        if (recipe.isEmpty()) {
            // Debug: imprimir lo que hay en los slots
            System.out.println("No se encontró receta para:");
            for (int i = 0; i < 3; i++) {
                System.out.println("  Slot " + i + ": " + itemHandler.getStackInSlot(i));
            }
            return false;
        }

        RegistryAccess registryAccess = level.registryAccess();
        ItemStack result = recipe.get().getResultItem(registryAccess);

        // Verificar que haya espacio en el output
        ItemStack currentOutput = this.itemHandler.getStackInSlot(3);
        boolean canInsert = currentOutput.isEmpty() ||
                (ItemStack.isSameItemSameTags(currentOutput, result) &&
                        currentOutput.getCount() + result.getCount() <= currentOutput.getMaxStackSize());

        if (!canInsert) {
            System.out.println("No hay espacio en el output");
            return false;
        }

        return hasIngredients(recipe.get());
    }

    private boolean hasIngredients(InfuseForgeryRecipe recipe) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        // Verificar que cada slot tenga suficiente cantidad del ingrediente requerido
        for (int i = 0; i < 3; i++) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(i);
            Ingredient requiredIngredient = ingredients.get(i);

            if (!requiredIngredient.test(stackInSlot) || stackInSlot.getCount() < 1) {
                System.out.println("Falta ingrediente en slot " + i + ": " + requiredIngredient);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void craftItem() {
        Level level = this.level;
        if (level == null || level.isClientSide()) return;

        SimpleContainer inventory = new SimpleContainer(3);
        for (int i = 0; i < 3; i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }

        Optional<InfuseForgeryRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.INFUSE_FORGERY_TYPE.get(), inventory, level);

        if (recipe.isPresent()) {
            System.out.println("Crafting receta: " + recipe.get().getId());

            InfuseForgeryRecipe selectedRecipe = recipe.get();
            RegistryAccess registryAccess = level.registryAccess();
            ItemStack result = selectedRecipe.getResultItem(registryAccess);

            // Extraer un item de cada slot de input
            for (int i = 0; i < 3; i++) {
                this.itemHandler.extractItem(i, 1, false);
            }

            // Actualizar el tiempo de procesamiento si está definido en la receta
            this.maxProgress = selectedRecipe.getProcessingTime();

            // Agregar el resultado al output
            ItemStack currentOutput = this.itemHandler.getStackInSlot(3);
            if (currentOutput.isEmpty()) {
                this.itemHandler.setStackInSlot(3, result.copy());
            } else if (ItemStack.isSameItemSameTags(currentOutput, result)) {
                currentOutput.grow(result.getCount());
            }

            System.out.println("Crafteo completado. Output: " + itemHandler.getStackInSlot(3));
        }
    }

    @Override
    protected PlayState predicate(AnimationState<?> event) {
        // Anima cuando está procesando
        if (this.progress > 0) {
            // Configura aquí tu animación de "trabajando"
            // event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.infuse_forgery.working"));
        }
        return PlayState.CONTINUE;
    }
}