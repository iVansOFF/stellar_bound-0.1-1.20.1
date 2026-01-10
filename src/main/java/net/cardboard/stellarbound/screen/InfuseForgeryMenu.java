package net.cardboard.stellarbound.screen;

import net.cardboard.stellarbound.base.BaseProcessingMenu;
import net.cardboard.stellarbound.registry.ModBlocks;
import net.cardboard.stellarbound.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class InfuseForgeryMenu extends BaseProcessingMenu {

    public InfuseForgeryMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public InfuseForgeryMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.INFUSE_FORGERY_MENU.get(), containerId, inv, entity, data);
    }

    @Override
    protected Block getBlock() {
        return ModBlocks.INFUSE_FORGERY.get();
    }

    @Override
    protected int getSlotCount() {
        return 4;
    }

    @Override
    protected void addSlots() {
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // Input slots (0, 1, 2)
            this.addSlot(new SlotItemHandler(handler, 0, 30, 35));
            this.addSlot(new SlotItemHandler(handler, 1, 48, 35));
            this.addSlot(new SlotItemHandler(handler, 2, 66, 35));

            // Output slot (3)
            this.addSlot(new SlotItemHandler(handler, 3, 124, 35) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false; // No se puede poner items en el output
                }
            });
        });
    }
}