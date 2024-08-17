package dev.galiev.worldoflabyrinth.block.entity

import dev.galiev.worldoflabyrinth.block.LabyrinthSensor
import dev.galiev.worldoflabyrinth.registry.BlockEntityRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.entity.SculkSensorBlockEntity
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.event.GameEvent
import net.minecraft.world.event.Vibrations


open class LabyrinthSensorEntity(pos: BlockPos, state: BlockState): SculkSensorBlockEntity(BlockEntityRegistry.LABYRINTH_SCULK_SENSOR_ENTITY, pos, state) {

    override fun createCallback(): Vibrations.Callback {
        return Callback(pos)
    }

    protected inner class Callback(pos: BlockPos?) : SculkSensorBlockEntity.VibrationCallback(pos) {
        override fun accepts(
            world: ServerWorld?,
            pos: BlockPos?,
            event: RegistryEntry<GameEvent>?,
            emitter: GameEvent.Emitter?
        ): Boolean {
            val startPos: BlockPos = pos?.offset(this@LabyrinthSensorEntity.cachedState[LabyrinthSensor.FACING].opposite)!!
            val endPos = startPos.add(5, 2, 5)

            val withinArea =
                pos.x >= startPos.x && pos.x <= endPos.x && pos.y >= startPos.y && pos.y <= endPos.y && pos.z >= startPos.z && pos.z <= endPos.z
            return if (withinArea && !event!!.matches(GameEvent.BLOCK_DESTROY) && !event.matches(GameEvent.BLOCK_PLACE))
                LabyrinthSensor.isInactive(this@LabyrinthSensorEntity.cachedState)
            else
                false
        }
    }
}