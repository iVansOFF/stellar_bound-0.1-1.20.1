package net.cardboard.stellarbound.entity;

import net.cardboard.stellarbound.registry.ModBlockEntities;
import net.cardboard.stellarbound.registry.ModMenuTypes;
import net.cardboard.stellarbound.screen.InfuseForgeryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class InfuseForgeryBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Inventario: 3 inputs + 1 output = 4 slots
    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot != 3; // Slot 3 es el output, no se puede poner items ahí
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    // Data para sincronizar con el cliente
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100; // Tiempo de crafteo en ticks

    public InfuseForgeryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INFUSE_FORGERY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> InfuseForgeryBlockEntity.this.progress;
                    case 1 -> InfuseForgeryBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> InfuseForgeryBlockEntity.this.progress = value;
                    case 1 -> InfuseForgeryBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.stellarbound.infuse_forgery");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new InfuseForgeryMenu(containerId, playerInventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("infuse_forgery.progress", progress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        progress = tag.getInt("infuse_forgery.progress");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, InfuseForgeryBlockEntity blockEntity) {
        if (level.isClientSide()) {
            return;
        }

        if (hasRecipe(blockEntity)) {
            blockEntity.progress++;
            setChanged(level, pos, state);

            if (blockEntity.progress >= blockEntity.maxProgress) {
                craftItem(blockEntity);
            }
        } else {
            blockEntity.resetProgress();
            setChanged(level, pos, state);
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(InfuseForgeryBlockEntity blockEntity) {
        // Aquí defines tu receta
        // Ejemplo simple: si hay items en los 3 slots de input, crea un output
        if (hasRecipe(blockEntity)) {
            // Extrae 1 item de cada input slot
            blockEntity.itemHandler.extractItem(0, 1, false);
            blockEntity.itemHandler.extractItem(1, 1, false);
            blockEntity.itemHandler.extractItem(2, 1, false);

            // Agrega el resultado al output (slot 3)
            // Por ahora un placeholder, luego agregarás tu sistema de recetas
            blockEntity.itemHandler.setStackInSlot(3, new ItemStack(
                    blockEntity.itemHandler.getStackInSlot(3).getItem(),
                    blockEntity.itemHandler.getStackInSlot(3).getCount() + 1
            ));

            blockEntity.resetProgress();
        }
    }

    private static boolean hasRecipe(InfuseForgeryBlockEntity blockEntity) {
        // Verifica si hay items en los 3 slots de input
        boolean hasInput = !blockEntity.itemHandler.getStackInSlot(0).isEmpty() &&
                !blockEntity.itemHandler.getStackInSlot(1).isEmpty() &&
                !blockEntity.itemHandler.getStackInSlot(2).isEmpty();

        // Verifica que el output tenga espacio
        ItemStack result = blockEntity.itemHandler.getStackInSlot(3);
        boolean hasSpace = result.isEmpty() || result.getCount() < result.getMaxStackSize();

        return hasInput && hasSpace;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<InfuseForgeryBlockEntity> event) {
        // Anima cuando está procesando
        if (this.progress > 0) {
            // Aquí puedes poner una animación de "trabajando"
            // event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.infuse_forgery.working"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}