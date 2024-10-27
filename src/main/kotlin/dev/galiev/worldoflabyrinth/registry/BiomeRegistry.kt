package dev.galiev.worldoflabyrinth.registry

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.MOD_ID
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome

object BiomeRegistry {
    val LABYRINTH_BIOME: RegistryKey<Biome> = RegistryKey.of(RegistryKeys.BIOME,
        Identifier.of(MOD_ID, "labyrinth_biome"))
}