package dev.galiev.worldoflabyrinth.item

import dev.galiev.worldoflabyrinth.component.DataComponentType
import dev.galiev.worldoflabyrinth.registry.StructureTags
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class GuideStone : Item(Settings().maxCount(1)) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)

        world.playSound(user, user.blockPos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS)

        user.itemCooldownManager.set(this, 240)

        if (world is ServerWorld) {
            val structurePos = world.locateStructure(StructureTags.EXIT_LOCATED, user.blockPos, 100, false)!!
            val direction = Vec3d(
                (structurePos.x + 0.5) - user.x,
                (structurePos.y + 0.5) - user.y,
                (structurePos.z + 0.5) - user.z
            ).normalize()

            for (i in 1..20) {
                val offset = direction.multiply(i * 0.5)
                val particlePos = user.pos.add(offset)

                world.spawnParticles(
                    ParticleTypes.FLAME,
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

        return TypedActionResult.success(stack, world.isClient)
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if (entity is PlayerEntity) {
            if (entity.itemCooldownManager.isCoolingDown(this)) {
                stack.set(DataComponentType.ACTIVATED, true)
            } else {
                stack.set(DataComponentType.ACTIVATED, false)
            }
        }
    }
}