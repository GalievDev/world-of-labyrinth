package dev.galiev.worldoflabyrinth.event

import dev.galiev.worldoflabyrinth.registry.DimensionRegistry
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

object BlockBreakingEvent: AttackBlockCallback {
    override fun interact(
        player: PlayerEntity?,
        world: World?,
        hand: Hand?,
        pos: BlockPos?,
        direction: Direction?
    ): ActionResult {
        if(!player?.isCreative!! && world?.registryKey == DimensionRegistry.WOL_LEVEL_KEY) {
            return ActionResult.FAIL
        }
        return ActionResult.PASS
    }
}