package dev.galiev.worldoflabyrinth.registry

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.MOD_ID
import dev.galiev.worldoflabyrinth.world.LabyrinthJigsawStructure
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.world.gen.structure.StructureType

object StructureTypeRegistry {
    val LABYRINTH: StructureType<LabyrinthJigsawStructure> = Registry.register(Registries.STRUCTURE_TYPE,
        Identifier.of(MOD_ID, "labyrinth"), StructureType { LabyrinthJigsawStructure.CODEC })
}