package dev.galiev.worldoflabyrinth.client.model

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth
import dev.galiev.worldoflabyrinth.item.LabyrinthOrb
import net.minecraft.util.Identifier
import software.bernie.geckolib.model.GeoModel

class LabyrinthOrbModel: GeoModel<LabyrinthOrb>() {
    override fun getModelResource(p0: LabyrinthOrb?): Identifier =
        Identifier.of(WorldOfLabyrinth.MOD_ID, "geo/labyrinth_orb.geo.json")

    override fun getTextureResource(p0: LabyrinthOrb?): Identifier =
        Identifier.of(WorldOfLabyrinth.MOD_ID, "textures/item/labyrinth_orb.png")

    override fun getAnimationResource(p0: LabyrinthOrb?): Identifier =
        Identifier.of(WorldOfLabyrinth.MOD_ID, "animations/labyrinth_orb.animation.json")
}