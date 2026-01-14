package net.cardboard.stellarbound.item.weapon.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BaseGunRenderer<T extends BaseGunItem> extends GeoItemRenderer<T> {

    private ResourceLocation playerSkinTexture = null;

    public BaseGunRenderer(GeoModel<T> model) {
        super(model);
        System.out.println("BaseGunRenderer creado para: " + model.getClass().getSimpleName());
    }

    @Override
    public ResourceLocation getTextureLocation(T animatable) {
        ResourceLocation texture = super.getTextureLocation(animatable);
        System.out.println("Intentando cargar textura: " + texture);
        return texture;
    }

    @Override
    public void renderByItem(ItemStack stack, net.minecraft.world.item.ItemDisplayContext transformType,
                             PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        System.out.println("Renderizando item: " + stack.getItem());

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            playerSkinTexture = Minecraft.getInstance().getEntityRenderDispatcher()
                    .getRenderer(player)
                    .getTextureLocation(player);
            System.out.println("Textura del jugador: " + playerSkinTexture);
        }

        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource,
                                  com.mojang.blaze3d.vertex.VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight,
                                  int packedOverlay, float red, float green, float blue, float alpha) {

        String boneName = bone.getName();
        System.out.println("Renderizando bone: " + boneName);

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource,
                buffer, isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);
    }
}