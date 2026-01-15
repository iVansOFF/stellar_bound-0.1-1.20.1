package net.cardboard.stellarbound.item.weapon.gun;

import net.cardboard.stellarbound.entity.BulletEntity;
import net.cardboard.stellarbound.item.weapon.gun.client.FlintlockRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.core.animation.RawAnimation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class FlintlockItem extends BaseGunItem {

    public FlintlockItem() {
        super(
                new Properties()
                        .stacksTo(1)
                        .rarity(Rarity.COMMON),
                1,      // maxAmmo
                6.0f,   // damage
                20,     // fireRate (ticks)
                60,     // reloadTime (ticks)
                0.70f   // accuracy
        );
    }

    @Override
    protected RawAnimation getIdleAnimation() {
        return RawAnimation.begin().thenLoop("flintlock.idle");
    }

    @Override
    protected RawAnimation getIdleUnloadedAnimation() {
        return RawAnimation.begin().thenLoop("flintlock.idle_unloaded");
    }

    @Override
    protected RawAnimation getShootAnimation() {
        return RawAnimation.begin().thenPlay("flintlock.shoot");
    }

    @Override
    protected RawAnimation getReloadAnimation() {
        return RawAnimation.begin().thenPlay("flintlock.reload");
    }

    @Override
    protected void shoot(Player player, Level level, ItemStack stack) {
        // Crear bala
        BulletEntity bullet = new BulletEntity(level, player);

        // Posición inicial (boca del arma)
        Vec3 look = player.getLookAngle();
        Vec3 handPos = player.getEyePosition()
                .add(look.scale(0.5))
                .add(player.getUpVector(1.0F).scale(-0.2F));

        bullet.setPos(handPos.x, handPos.y, handPos.z);

        // Dirección con precisión
        float spread = (1.0f - getAccuracy()) * 0.1f;
        Vec3 direction = look.add(
                (level.random.nextDouble() - 0.5) * spread,
                (level.random.nextDouble() - 0.5) * spread,
                (level.random.nextDouble() - 0.5) * spread
        ).normalize();

        // Velocidad
        bullet.shoot(direction.x, direction.y, direction.z, 3.0F, spread);
        bullet.setDamage(getDamage());

        level.addFreshEntity(bullet);

        // Sonido de disparo
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS,
                0.8F, 1.2F + level.random.nextFloat() * 0.2F);

        // Retroceso
        player.push(-direction.x * 0.1, -direction.y * 0.05, -direction.z * 0.1);
    }

    @Override
    protected void playShootEffects(Level level, Player player) {
        // Efectos visuales en cliente
        Vec3 look = player.getLookAngle();
        Vec3 handPos = player.getEyePosition()
                .add(look.scale(0.5))
                .add(player.getUpVector(1.0F).scale(-0.2F));

        for (int i = 0; i < 5; i++) {
            level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,
                    handPos.x + look.x * 0.5,
                    handPos.y + look.y * 0.5,
                    handPos.z + look.z * 0.5,
                    (level.random.nextDouble() - 0.5) * 0.02,
                    (level.random.nextDouble() - 0.5) * 0.02,
                    (level.random.nextDouble() - 0.5) * 0.02);
        }
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level,
                                @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        int ammo = getAmmo(stack);
        boolean reloading = isReloading(stack);

        tooltip.add(Component.literal("§7Class: §eMarksman"));
        tooltip.add(Component.literal(""));

        tooltip.add(Component.literal("§6Ammo: §f" + ammo + "/" + getMaxAmmo()));

        if (reloading) {
            tooltip.add(Component.literal("§eReloading..."));
        }

        tooltip.add(Component.literal("§cDamage: §f" + getDamage()));
        tooltip.add(Component.literal("§bFire Rate: §f" + (getFireRate() / 20.0f) + "s"));
        tooltip.add(Component.literal("§eReload Time: §f" + (getReloadTime() / 20.0f) + "s"));
        tooltip.add(Component.literal("§aAccuracy: §f" + (int)(getAccuracy() * 100) + "%"));

        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Left-click to shoot"));
        tooltip.add(Component.literal("§7Press R to reload"));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return getAmmo(stack) == getMaxAmmo();
    }

    // ========== CLIENT-SIDE RENDERER ==========

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private FlintlockRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new FlintlockRenderer();
                }
                return this.renderer;
            }
        });
    }
}