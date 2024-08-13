package dev.galiev.worldoflabyrinth.world.dimension

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionOptions
import net.minecraft.world.dimension.DimensionType

object DimensionRegistry {
    val WOL_KEY: RegistryKey<DimensionOptions> = RegistryKey.of(RegistryKeys.DIMENSION,
        Identifier.of(WorldOfLabyrinth.MOD_ID,"worldoflab"))
    val WOL_LEVEL_KEY: RegistryKey<World> = RegistryKey.of(RegistryKeys.WORLD,
        Identifier.of(WorldOfLabyrinth.MOD_ID,"worldoflab"))
    val WOL_TYPE: RegistryKey<DimensionType> = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
        Identifier.of(WorldOfLabyrinth.MOD_ID,"worldoflab_type"))
}