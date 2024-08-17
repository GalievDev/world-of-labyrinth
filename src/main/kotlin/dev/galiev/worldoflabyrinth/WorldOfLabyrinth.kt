package dev.galiev.worldoflabyrinth

import dev.galiev.worldoflabyrinth.event.BlockBreakingEvent
import dev.galiev.worldoflabyrinth.event.DoorOpenEvent
import dev.galiev.worldoflabyrinth.registry.BlockEntityRegistry
import dev.galiev.worldoflabyrinth.registry.BlockRegistry
import dev.galiev.worldoflabyrinth.registry.ItemRegistry
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import org.apache.logging.log4j.LogManager

object WorldOfLabyrinth : ModInitializer {
    const val MOD_ID = "world-of-labyrinth"
    val logger = LogManager.getLogger(WorldOfLabyrinth::class.java)

    override fun onInitialize() {
        AttackBlockCallback.EVENT.register(BlockBreakingEvent)
        UseBlockCallback.EVENT.register(DoorOpenEvent)
        ItemRegistry
        BlockRegistry
        BlockEntityRegistry
    }
}
