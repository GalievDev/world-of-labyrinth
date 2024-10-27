package dev.galiev.worldoflabyrinth

import dev.galiev.worldoflabyrinth.event.BlockBreakingEvent
import dev.galiev.worldoflabyrinth.event.BlockPlacingEvent
import dev.galiev.worldoflabyrinth.event.DoorOpenEvent
import dev.galiev.worldoflabyrinth.event.TrappedChestOpenEvent
import dev.galiev.worldoflabyrinth.registry.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import org.apache.logging.log4j.LogManager
import kotlin.random.Random

object WorldOfLabyrinth : ModInitializer {
    const val MOD_ID = "world-of-labyrinth"
    val RANDOM = Random(System.currentTimeMillis())
    val logger = LogManager.getLogger(WorldOfLabyrinth::class.java)

    override fun onInitialize() {
        AttackBlockCallback.EVENT.register(BlockBreakingEvent)
        UseBlockCallback.EVENT.register(BlockPlacingEvent)
        UseBlockCallback.EVENT.register(DoorOpenEvent)
        UseBlockCallback.EVENT.register(TrappedChestOpenEvent)
        ItemRegistry
        BlockRegistry
        BlockEntityRegistry
        BiomeRegistry
        StructureRegistry
    }
}
