package net.cardboard.stellarbound;

import net.cardboard.stellarbound.entity.WimpEntity;
import net.cardboard.stellarbound.registry.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Stellarbound.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.WIMP.get(), WimpEntity.createAttributes().build());
    }
}