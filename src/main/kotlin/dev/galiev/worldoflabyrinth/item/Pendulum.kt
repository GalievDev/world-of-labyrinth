package dev.galiev.worldoflabyrinth.item

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.tag.StructureTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class Pendulum : Item(Settings().maxCount(1)) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (world is ServerWorld) {
            val structurePos = world.locateStructure(StructureTags.EYE_OF_ENDER_LOCATED, user.blockPos, 100, false)!!
            val direction = Vec3d(
                (structurePos.x + 0.5) - user.x,
                (structurePos.y + 0.5) - user.y,
                (structurePos.z + 0.5) - user.z
            ).normalize()

            for (i in 1..160) {
                val offset = direction.multiply(i * 0.5)
                val particlePos = user.pos.add(offset)

                world.spawnParticles(
                    ParticleTypes.END_ROD,
                    particlePos.x,
                    particlePos.y + 1,
                    particlePos.z,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0
                )
            }
        }
        return super.use(world, user, hand)
    }
}