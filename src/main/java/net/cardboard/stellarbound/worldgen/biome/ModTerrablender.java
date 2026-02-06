package net.cardboard.stellarbound.worldgen.biome;

import net.cardboard.stellarbound.Stellarbound;
import net.cardboard.stellarbound.worldgen.biome.surface.ModSurfaceRules;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

public class ModTerrablender {
    public static void registerBiomes() {
        // Registrar la región del bioma
        Regions.register(new StarfieldsRegion(
                ResourceLocation.fromNamespaceAndPath(Stellarbound.MOD_ID, "overworld"),
                5 // peso - más alto = más común
        ));

        // Registrar las reglas de superficie (¡IMPORTANTE!)
        SurfaceRuleManager.addSurfaceRules(
                SurfaceRuleManager.RuleCategory.OVERWORLD,
                Stellarbound.MOD_ID,
                ModSurfaceRules.makeRules()
        );
    }
}