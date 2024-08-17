package dev.galiev.worldoflabyrinth.registry

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.MOD_ID
import dev.galiev.worldoflabyrinth.block.entity.LabyrinthSensorEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object BlockEntityRegistry {
    val LABYRINTH_SCULK_SENSOR_ENTITY: BlockEntityType<LabyrinthSensorEntity> = Registry.register(
        Registries.BLOCK_ENTITY_TYPE, Identifier.of(MOD_ID, "labyrinth_sculk_sensor_entity"), BlockEntityType.Builder.create(
            ::LabyrinthSensorEntity, BlockRegistry.LABYRINTH_SCULK
        ).build()
    )
}