package dev.galiev.worldoflabyrinth.event

import dev.galiev.worldoflabyrinth.registry.DimensionRegistry
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.block.ChestBlock
import net.minecraft.block.DoorBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.world.World


object BlockPlacingEvent: UseBlockCallback {
    override fun interact(player: PlayerEntity?, world: World?, hand: Hand?, hitResult: BlockHitResult?): ActionResult {
        if(player?.world?.registryKey == DimensionRegistry.WOL_LEVEL_KEY) {
            if (player.isCreative) { return ActionResult.PASS }

            val block = world?.getBlockState(hitResult?.blockPos)?.block
            if (block is DoorBlock || block is ChestBlock) { return ActionResult.PASS }

            return ActionResult.FAIL
        }
        return ActionResult.PASS
    }
}