package dev.galiev.worldoflabyrinth.event

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.RANDOM
import dev.galiev.worldoflabyrinth.registry.DimensionRegistry
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.world.World

object DoorOpenEvent: UseBlockCallback {
    override fun interact(player: PlayerEntity?, world: World?, hand: Hand?, hitResult: BlockHitResult?): ActionResult {
        if (world?.isClient!!) { return ActionResult.PASS }
        if (player?.world?.registryKey == DimensionRegistry.WOL_LEVEL_KEY && world is ServerWorld) {
            if (player.isCreative) {
                return ActionResult.PASS
            }
            val block = world.getBlockState(hitResult?.blockPos)?.block
            if (block == Blocks.SPRUCE_DOOR) {
                player.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS, 300, 3))
                player.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS, 300, 3))
                player.addStatusEffect(StatusEffectInstance(StatusEffects.WEAKNESS, 300, 3))
                for (i in 0..RANDOM.nextInt(2)) {
                    val zombie = EntityType.ZOMBIE.create(world).apply {
                        this?.equipStack(EquipmentSlot.HEAD, Items.DIAMOND_HELMET.defaultStack)
                        this?.equipStack(EquipmentSlot.CHEST, Items.DIAMOND_CHESTPLATE.defaultStack)
                        this?.equipStack(EquipmentSlot.LEGS, Items.DIAMOND_LEGGINGS.defaultStack)
                        this?.equipStack(EquipmentSlot.FEET, Items.DIAMOND_BOOTS.defaultStack)
                        this?.equipStack(EquipmentSlot.MAINHAND, Items.DIAMOND_SWORD.defaultStack)
                        this?.setPosition(player.x - i + 3, player.y, player.z - i + 3)
                    }

                    world.spawnEntity(zombie)
                }

                return ActionResult.FAIL
            }
        }
        return ActionResult.PASS
    }
}