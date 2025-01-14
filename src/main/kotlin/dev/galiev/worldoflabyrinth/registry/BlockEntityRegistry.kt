package dev.galiev.worldoflabyrinth.registry

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.MOD_ID
import dev.galiev.worldoflabyrinth.block.entity.TrapBlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object BlockEntityRegistry {
    val TRAP_BLOCK_ENTITY: BlockEntityType<TrapBlockEntity> = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        Identifier.of(MOD_ID, "trap_block_entity"),
        BlockEntityType.Builder.create(::TrapBlockEntity, *BlockRegistry.BLOCKS.keys.toTypedArray()).build(null)
    )
}