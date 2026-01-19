package net.cardboard.stellarbound;

import net.cardboard.stellarbound.entity.SkraeveEntity;
import net.cardboard.stellarbound.entity.WimpEntity;
import net.cardboard.stellarbound.entity.WispBellEntity;
import net.cardboard.stellarbound.registry.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Stellarbound.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        // Debug log
        System.out.println("=== REGISTRANDO ATRIBUTOS DE ENTIDADES DE STELLARBOUND ===");

        try {
            event.put(ModEntities.WIMP.get(), WimpEntity.createAttributes().build());
            System.out.println("✓ Wimp registrado");
        } catch (Exception e) {
            System.err.println("✗ Error registrando Wimp: " + e.getMessage());
        }

        try {
            event.put(ModEntities.WISP_BELL.get(), WispBellEntity.createAttributes().build());
            System.out.println("✓ Wisp Bell registrado");
        } catch (Exception e) {
            System.err.println("✗ Error registrando Wisp Bell: " + e.getMessage());
        }

        try {
            event.put(ModEntities.SKRAEVE.get(), SkraeveEntity.createAttributes().build());
            System.out.println("✓ Skraeve registrado");
        } catch (Exception e) {
            System.err.println("✗ Error registrando Skraeve: " + e.getMessage());
        }

        System.out.println("=== FIN REGISTRO ENTIDADES ===");
    }
}