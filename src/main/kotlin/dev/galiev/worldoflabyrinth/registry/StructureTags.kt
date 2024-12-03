package dev.galiev.worldoflabyrinth.registry

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.MOD_ID
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.world.gen.structure.Structure

object StructureTags {
    val PENDULUM_LOCATED: TagKey<Structure> = TagKey.of(RegistryKeys.STRUCTURE, Identifier.of(MOD_ID, "pendulum_located"))
}