package net.cardboard.stellarbound.entity;

import net.cardboard.stellarbound.base.BaseProcessingBlockEntity;
import net.cardboard.stellarbound.registry.ModBlockEntities;
import net.cardboard.stellarbound.registry.ModMenuTypes;
import net.cardboard.stellarbound.screen.InfuseForgeryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class InfuseForgeryBlockEntity extends BaseProcessingBlockEntity {

    public InfuseForgeryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INFUSE_FORGERY.get(), pos, state, 4, 100);
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
    protected AbstractContainerMenu createMenuInternal(int containerId, Inventory playerInventory, Player player) {
        return new InfuseForgeryMenu(containerId, playerInventory, this, this.data);
    }

    @Override
    protected boolean hasRecipe() {
        // Verifica si hay items en los 3 slots de input
        boolean hasInput = !itemHandler.getStackInSlot(0).isEmpty() &&
                !itemHandler.getStackInSlot(1).isEmpty() &&
                !itemHandler.getStackInSlot(2).isEmpty();

        // Verifica que el output tenga espacio
        ItemStack result = itemHandler.getStackInSlot(3);
        boolean hasSpace = result.isEmpty() || result.getCount() < result.getMaxStackSize();

        return hasInput && hasSpace;
    }

    @Override
    protected void craftItem() {
        if (hasRecipe()) {
            // Extrae 1 item de cada input slot
            itemHandler.extractItem(0, 1, false);
            itemHandler.extractItem(1, 1, false);
            itemHandler.extractItem(2, 1, false);

            // Agrega el resultado al output (slot 3)
            // Nota: Aquí deberías implementar tu sistema de recetas
            itemHandler.setStackInSlot(3, new ItemStack(
                    itemHandler.getStackInSlot(3).getItem(),
                    itemHandler.getStackInSlot(3).getCount() + 1
            ));
        }
    }

    @Override
    protected PlayState predicate(AnimationState<?> event) {
        // Anima cuando está procesando
        if (this.progress > 0) {
            // Aquí puedes configurar la animación de "trabajando"
            // Ejemplo: event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.infuse_forgery.working"));
        }
        return PlayState.CONTINUE;
    }
}