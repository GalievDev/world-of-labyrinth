package dev.galiev.worldoflabyrinth.client

import dev.galiev.worldoflabyrinth.util.ModelPredicate
import net.fabricmc.api.ClientModInitializer

class WorldOfLabyrinthClient : ClientModInitializer {

    override fun onInitializeClient() {
        ModelPredicate
        //BuiltinItemRendererRegistry.INSTANCE.register(ItemRegistry.LABYRINTH_ORB, LabyrinthOrbRenderer())
    }
}
