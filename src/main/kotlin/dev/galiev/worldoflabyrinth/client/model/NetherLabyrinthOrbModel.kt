package dev.galiev.worldoflabyrinth.client.model

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth
import dev.galiev.worldoflabyrinth.item.LabyrinthOrb
import dev.galiev.worldoflabyrinth.item.NetherLabyrinthOrb
import net.minecraft.util.Identifier
import software.bernie.geckolib.model.GeoModel

class NetherLabyrinthOrbModel: GeoModel<NetherLabyrinthOrb>() {
    override fun getModelResource(p0: NetherLabyrinthOrb?): Identifier =
        Identifier.of(WorldOfLabyrinth.MOD_ID, "geo/labyrinth_orb.geo.json")

    override fun getTextureResource(p0: NetherLabyrinthOrb?): Identifier =
        Identifier.of(WorldOfLabyrinth.MOD_ID, "textures/item/nether_labyrinth_orb.png")

    override fun getAnimationResource(p0: NetherLabyrinthOrb?): Identifier =
        Identifier.of(WorldOfLabyrinth.MOD_ID, "animations/labyrinth_orb.animation.json")
}