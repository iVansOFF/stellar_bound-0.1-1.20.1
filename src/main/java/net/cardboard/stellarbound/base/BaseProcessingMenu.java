package net.cardboard.stellarbound.base;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class BaseProcessingMenu extends AbstractContainerMenu {

    public final BlockEntity blockEntity;
    protected final Level level;
    protected final ContainerData data;

    protected BaseProcessingMenu(MenuType<?> menuType, int containerId, Inventory inv,
                                 BlockEntity entity, ContainerData data) {
        super(menuType, containerId);
        this.blockEntity = entity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addSlots(); // Método para que las subclases agreguen sus slots
        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress(int progressBarSize) {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        return maxProgress != 0 && progress != 0 ? progress * progressBarSize / maxProgress : 0;
    }

    protected abstract Block getBlock();
    protected abstract int getSlotCount();
    protected abstract void addSlots(); // Nuevo método para que las subclases agreguen sus slots

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        int vanillaSlotCount = 36;
        int teFirstSlot = vanillaSlotCount;
        int teSlotCount = getSlotCount();

        if (index < vanillaSlotCount) {
            if (!moveItemStackTo(sourceStack, teFirstSlot, teFirstSlot + teSlotCount - 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < teFirstSlot + teSlotCount) {
            if (!moveItemStackTo(sourceStack, 0, vanillaSlotCount, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, getBlock());
    }

    protected void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    protected void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}