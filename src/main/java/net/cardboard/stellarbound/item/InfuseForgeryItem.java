package net.cardboard.stellarbound.item;

import net.cardboard.stellarbound.base.BaseGeckoBlockItem;
import net.cardboard.stellarbound.client.renderer.InfuseForgeryItemRenderer;
import net.cardboard.stellarbound.util.AnimatedItemName;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;

public class InfuseForgeryItem extends BaseGeckoBlockItem {

    public InfuseForgeryItem(Block block, Properties properties) {
        super(block, properties, InfuseForgeryItemRenderer::new);
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        long time = System.currentTimeMillis() / 50;
        return AnimatedItemName.magicGradient("Infuse Forgery", time);
    }
}