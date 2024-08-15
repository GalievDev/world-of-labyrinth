package dev.galiev.worldoflabyrinth.event

import dev.galiev.worldoflabyrinth.world.dimension.DimensionRegistry
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.block.Blocks
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.world.World

object DoorOpenEvent: UseBlockCallback {
    override fun interact(player: PlayerEntity?, world: World?, hand: Hand?, hitResult: BlockHitResult?): ActionResult {

        if (player?.world?.registryKey == DimensionRegistry.WOL_LEVEL_KEY) {
            if (player.isCreative) {
                return ActionResult.PASS
            }
            val block = world?.getBlockState(hitResult?.blockPos)?.block
            if (block == Blocks.SPRUCE_DOOR) {
                player.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS, 200, 3))
                player.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS, 200, 3))
                player.sendMessage(Text.literal("HAHAHA did you seriously think it was that easy?").formatted(Formatting.RED), true)
                val zombie = ZombieEntity(world)
                world?.spawnEntity(zombie)
                zombie.equipStack(EquipmentSlot.HEAD, Items.DIAMOND_HELMET.defaultStack)
                zombie.equipStack(EquipmentSlot.BODY, Items.DIAMOND_CHESTPLATE.defaultStack)
                zombie.equipStack(EquipmentSlot.LEGS, Items.DIAMOND_LEGGINGS.defaultStack)
                zombie.equipStack(EquipmentSlot.FEET, Items.DIAMOND_BOOTS.defaultStack)
                zombie.equipStack(EquipmentSlot.MAINHAND, Items.DIAMOND_SWORD.defaultStack)

                return ActionResult.FAIL
            }
            return ActionResult.FAIL
        }
        return ActionResult.PASS
    }
}