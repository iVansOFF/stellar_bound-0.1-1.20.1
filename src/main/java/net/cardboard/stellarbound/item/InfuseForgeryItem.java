package net.cardboard.stellarbound.item;

import net.cardboard.stellarbound.base.BaseGeckoBlockItem;
import net.cardboard.stellarbound.client.renderer.InfuseForgeryItemRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Supplier;

public class InfuseForgeryItem extends BaseGeckoBlockItem {

    public InfuseForgeryItem(Block block, Properties properties) {
        super(block, properties, InfuseForgeryItemRenderer::new);
    }
}