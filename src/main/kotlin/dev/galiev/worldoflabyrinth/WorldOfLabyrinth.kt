package dev.galiev.worldoflabyrinth

import dev.galiev.worldoflabyrinth.item.ItemRegistry
import net.fabricmc.api.ModInitializer

object WorldOfLabyrinth : ModInitializer {
    const val MOD_ID = "world-of-labyrinth"

    override fun onInitialize() {
        ItemRegistry
    }
}
