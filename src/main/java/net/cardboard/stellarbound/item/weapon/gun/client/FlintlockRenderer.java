package net.cardboard.stellarbound.item.weapon.gun.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.item.weapon.gun.BaseGunRenderer;
import net.cardboard.stellarbound.item.weapon.gun.FlintlockItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class FlintlockRenderer extends BaseGunRenderer<FlintlockItem> {

    private static final ResourceLocation RIGHT_ARM_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Stellarbound.MOD_ID, "textures/entity/arm_right.png");
    private static final ResourceLocation LEFT_ARM_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Stellarbound.MOD_ID, "textures/entity/arm_left.png");

    public FlintlockRenderer() {
        super(new FlintlockModel());
    }

    @Override
    public void renderByItem(ItemStack stack, net.minecraft.world.item.ItemDisplayContext transformType,
                             PoseStack poseStack, MultiBufferSource bufferSource,
                             int packedLight, int packedOverlay) {

        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, FlintlockItem animatable, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource,
                                  VertexConsumer buffer, boolean isReRender, float partialTick,
                                  int packedLight, int packedOverlay, float red, float green,
                                  float blue, float alpha) {

        // Determinar qué textura usar
        String boneName = bone.getName();
        ResourceLocation textureToUse;

        if (boneName != null) {
            if (boneName.equals("right")) {
                textureToUse = RIGHT_ARM_TEXTURE;
            } else if (boneName.equals("left")) {
                textureToUse = LEFT_ARM_TEXTURE;
            } else {
                textureToUse = getTextureLocation(animatable);
            }
        } else {
            textureToUse = getTextureLocation(animatable);
        }

        // Crear nuevo buffer con la textura adecuada
        VertexConsumer actualBuffer = bufferSource.getBuffer(RenderType.entityTranslucent(textureToUse));

        // Renderizar normalmente con el buffer seleccionado
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource,
                actualBuffer, isReRender, partialTick, packedLight,
                packedOverlay, red, green, blue, alpha);
    }
}