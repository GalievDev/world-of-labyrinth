package dev.galiev.worldoflabyrinth.block

import com.mojang.serialization.MapCodec
import dev.galiev.worldoflabyrinth.block.entity.LabyrinthSensorEntity
import dev.galiev.worldoflabyrinth.registry.BlockEntityRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.SculkSensorBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.enums.SculkSensorPhase
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.event.Vibrations

open class LabyrinthSensor(settings: Settings?): SculkSensorBlock(settings) {
    val CODEC: MapCodec<LabyrinthSensor> = createCodec { settings: Settings? ->
        LabyrinthSensor(
            settings
        )
    }

    companion object {
        val FACING: DirectionProperty = Properties.HORIZONTAL_FACING

        fun isInactive(state: BlockState?): Boolean {
            return getPhase(state) == SculkSensorPhase.INACTIVE
        }
    }

    init {
        defaultState = ((stateManager.defaultState as BlockState).with(FACING, Direction.NORTH))
    }

    override fun getCodec(): MapCodec<out SculkSensorBlock> {
        return CODEC
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return super.getPlacementState(ctx)!!.with(FACING, ctx.horizontalPlayerFacing)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        super.appendProperties(builder)
        builder.add(FACING)
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        return state.with(
            FACING,
            rotation.rotate(state.get(FACING))
        )
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        return state.rotate(mirror.getRotation(state.get(FACING)))
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return LabyrinthSensorEntity(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        world: World?,
        state: BlockState?,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? {
        return if (!world!!.isClient)
            validateTicker(
                type,
                BlockEntityRegistry.LABYRINTH_SCULK_SENSOR_ENTITY
            ) { worldx: World?, pos: BlockPos?, statex: BlockState?, blockEntity: LabyrinthSensorEntity ->
                Vibrations.Ticker.tick(
                    worldx,
                    blockEntity.vibrationListenerData,
                    blockEntity.vibrationCallback
                )
            }
        else
            null
    }
}