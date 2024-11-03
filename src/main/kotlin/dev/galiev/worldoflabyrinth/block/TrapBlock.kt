package dev.galiev.worldoflabyrinth.block

import com.mojang.serialization.MapCodec
import dev.galiev.worldoflabyrinth.block.entity.TrapBlockEntity
import dev.galiev.worldoflabyrinth.registry.BlockEntityRegistry
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TrapBlock(settings: Settings?, val trap: String) : BlockWithEntity(settings) {
    val CODEC: MapCodec<TrapBlock> = createCodec { settings ->
        TrapBlock(settings, trap)
    }

    override fun getCodec(): MapCodec<out BlockWithEntity> = CODEC

    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL

    override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity = TrapBlockEntity(pos, state)

    override fun <T : BlockEntity?> getTicker(
        world: World?,
        state: BlockState?,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? = validateTicker(type, BlockEntityRegistry.TRAP_BLOCK_ENTITY, TrapBlockEntity::tick)
}