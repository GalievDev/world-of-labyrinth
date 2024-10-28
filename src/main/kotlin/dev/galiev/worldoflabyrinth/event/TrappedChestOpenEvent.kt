package dev.galiev.worldoflabyrinth.event

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.RANDOM
import dev.galiev.worldoflabyrinth.registry.DimensionRegistry
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.world.World

object TrappedChestOpenEvent: UseBlockCallback {
    override fun interact(player: PlayerEntity?, world: World?, hand: Hand?, hitResult: BlockHitResult?): ActionResult {
        if (player?.world?.registryKey == DimensionRegistry.WOL_LEVEL_KEY && world is ServerWorld) {
            if (player.isCreative) {
                return ActionResult.PASS
            }
            if (world.getBlockState(hitResult?.blockPos).block == Blocks.TRAPPED_CHEST) {
                val rand = RANDOM.nextDouble(0.0, 1000.0)
                player.teleport(world, rand, 17.0, rand, setOf(), player.bodyYaw, player.prevPitch)
                return ActionResult.FAIL
            }
        }
        return ActionResult.PASS
    }
}