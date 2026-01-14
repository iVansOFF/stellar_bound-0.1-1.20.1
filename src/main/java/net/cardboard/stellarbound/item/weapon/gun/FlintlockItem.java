package net.cardboard.stellarbound.item.weapon.gun;

import net.cardboard.stellarbound.item.weapon.gun.client.FlintlockRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FlintlockItem extends BaseGunItem {

    public FlintlockItem() {
        super(
                new Properties()
                        .stacksTo(1)
                        .rarity(Rarity.COMMON)
                        .durability(500),
                1,      // maxAmmo
                6.0f,  // damage
                20.0f,  // fireRate
                60.0f,  // reloadTime
                0.70f   // accuracy
        );
    }

    @Override
    protected String getIdleAnimation() {

        return "flintlock.idle";
    }

    @Override
    protected String getIdleUnloadedAnimation() {

        return "flintlock.idle_unloaded";
    }

    @Override
    protected String getShootAnimation() {

        return "flintlock.shoot";
    }

    @Override
    protected String getReloadAnimation() {

        return "flintlock.reload";
    }

    @Override
    protected BaseGunRenderer<?> createRenderer() {
        return new FlintlockRenderer();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal("§7Class: §eMarksman"));
        tooltip.add(Component.literal(""));

        int ammo = getAmmo(stack);
        tooltip.add(Component.literal("§6Ammo: §f" + ammo + "/" + maxAmmo));

        tooltip.add(Component.literal("§cDamage: §f" + damage));
        tooltip.add(Component.literal("§bFire Rate: §f" + (fireRate / 20.0f) + "s"));
        tooltip.add(Component.literal("§eReload Time: §f" + (reloadTime / 20.0f) + "s"));
        tooltip.add(Component.literal("§aAccuracy: §f" + (int)(accuracy * 100) + "%"));

        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Right-click to shoot"));
        tooltip.add(Component.literal("§7Press R to reload"));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return getAmmo(stack) == maxAmmo;
    }
}