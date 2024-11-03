package dev.galiev.worldoflabyrinth.block.entity

import dev.galiev.worldoflabyrinth.block.TrapBlock
import dev.galiev.worldoflabyrinth.registry.BlockEntityRegistry
import dev.galiev.worldoflabyrinth.registry.DimensionRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class TrapBlockEntity(pos: BlockPos?, state: BlockState?)
    : BlockEntity(BlockEntityRegistry.TRAP_BLOCK_ENTITY, pos, state) {

    companion object {
        fun tick(world: World, blockPos: BlockPos, state: BlockState, blockEntity: TrapBlockEntity) {
            val targetPoses: MutableList<BlockPos> = mutableListOf()
            val block = state.block
            if (world.isClient) return
            if ((block as TrapBlock).trap.isBlank()) return

            world.players.forEach { player ->
                if (!player.world.registryKey.equals(DimensionRegistry.WOL_LEVEL_KEY)) return
                when (block.trap) {
                    "mobs" -> {
                        for (targetPos: BlockPos in BlockPos.iterate(blockPos.add(-7, 0, -7), blockPos.add(7, 2, 7))) {
                            targetPoses.add(targetPos)
                            if (!player.isCreative && !player.isSpectator && player.blockPos in targetPoses) {
                                mobs(player, world, blockPos)
                                world.breakBlock(targetPos, false)
                                world.setBlockState(blockPos, Blocks.MOSS_BLOCK.defaultState)
                                break
                            }
                        }
                    }

                    "lava" -> {
                        for (targetPos: BlockPos in BlockPos.iterate(blockPos.add(-2, -1, -2), blockPos.add(2, 2, 2))) {
                            targetPoses.add(targetPos)
                            if (!player.isCreative && !player.isSpectator && player.blockPos in targetPoses) {
                                for (lavaPos: BlockPos in BlockPos.iterate(blockPos.add(-1, 0, -1), blockPos.add(1, 0, 1))) {
                                    world.setBlockState(lavaPos, Blocks.LAVA.defaultState)
                                }
                                for (barrierPos: BlockPos in BlockPos.iterate(blockPos.add(-1, -1, -1), blockPos.add(1, -1, 1))) {
                                    world.setBlockState(barrierPos, Blocks.BARRIER.defaultState)
                                }
                                world.setBlockState(blockPos, Blocks.LAVA.defaultState)
                            }
                        }
                    }

                    "void" -> {
                        for (targetPos: BlockPos in BlockPos.iterate(blockPos.add(-2, -1, -2), blockPos.add(2, 2, 2))) {
                            targetPoses.add(targetPos)
                            if (!player.isCreative && !player.isSpectator && player.blockPos in targetPoses) {
                                for (lavaPos: BlockPos in BlockPos.iterate(blockPos.add(-1, 0, -1), blockPos.add(1, 0, 1))) {
                                    world.breakBlock(lavaPos, false)
                                }
                                world.breakBlock(blockPos, false)
                            }
                        }
                    }

                    else -> return
                }
            }
        }

        private fun mobs(player: PlayerEntity, world: World, pos: BlockPos) {
            player.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS, 300, 3))
            player.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS, 300, 3))
            player.addStatusEffect(StatusEffectInstance(StatusEffects.WEAKNESS, 300, 3))

            for (i in -1..1) {
                val zombie = EntityType.ZOMBIE.create(world).apply {
                    this?.equipStack(EquipmentSlot.HEAD, Items.IRON_HELMET.defaultStack)
                    this?.equipStack(EquipmentSlot.CHEST, Items.DIAMOND_CHESTPLATE.defaultStack)
                    this?.equipStack(EquipmentSlot.LEGS, Items.IRON_LEGGINGS.defaultStack)
                    this?.equipStack(EquipmentSlot.FEET, Items.DIAMOND_BOOTS.defaultStack)
                    this?.equipStack(EquipmentSlot.MAINHAND, Items.DIAMOND_SWORD.defaultStack)
                    this?.setPosition(
                        pos.x.toDouble() - i + 3,
                        pos.y + 1.0,
                        pos.z.toDouble() - i + 3
                    )
                }

                val skeleton = EntityType.SKELETON.create(world).apply {
                    this?.equipStack(EquipmentSlot.HEAD, Items.CHAINMAIL_HELMET.defaultStack)
                    this?.equipStack(EquipmentSlot.CHEST, Items.IRON_CHESTPLATE.defaultStack)
                    this?.equipStack(EquipmentSlot.LEGS, Items.CHAINMAIL_LEGGINGS.defaultStack)
                    this?.equipStack(EquipmentSlot.FEET, Items.LEATHER_BOOTS.defaultStack)
                    this?.equipStack(EquipmentSlot.MAINHAND, Items.BOW.defaultStack)
                    this?.setPosition(
                        pos.x.toDouble() - i + 3,
                        pos.y + 1.0,
                        pos.z.toDouble() - i + 3
                    )
                }

                world.spawnEntity(skeleton)
                world.spawnEntity(zombie)
            }
        }
    }
}