package dev.galiev.worldoflabyrinth.event

import dev.galiev.worldoflabyrinth.world.dimension.DimensionRegistry
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
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
                player.sendMessage(Text.literal("HAHAHA did you seriously think it was that easy?").formatted(Formatting.RED), true)
                val zombie = EntityType.ZOMBIE.create(world)
                zombie?.equipStack(EquipmentSlot.HEAD, Items.DIAMOND_HELMET.defaultStack)
                zombie?.equipStack(EquipmentSlot.CHEST, Items.DIAMOND_CHESTPLATE.defaultStack)
                zombie?.equipStack(EquipmentSlot.LEGS, Items.DIAMOND_LEGGINGS.defaultStack)
                zombie?.equipStack(EquipmentSlot.FEET, Items.DIAMOND_BOOTS.defaultStack)
                zombie?.equipStack(EquipmentSlot.MAINHAND, Items.DIAMOND_SWORD.defaultStack)
                zombie?.setPosition(player.x - 1, player.y, player.z - 1)

                world.spawnEntity(zombie)

                return ActionResult.FAIL
            }
        }
        return ActionResult.PASS
    }
}