package dev.galiev.worldoflabyrinth.registry

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.MOD_ID
import dev.galiev.worldoflabyrinth.world.structure.SkyStructure
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.world.gen.structure.StructureType

object StructureRegistry {
    val SKY_STRUCTURE: StructureType<SkyStructure>

    init {
        SKY_STRUCTURE = Registry.register(
            Registries.STRUCTURE_TYPE,
            Identifier.of(MOD_ID, "sky"), StructureType { SkyStructure.CODEC }
        )
    }
}