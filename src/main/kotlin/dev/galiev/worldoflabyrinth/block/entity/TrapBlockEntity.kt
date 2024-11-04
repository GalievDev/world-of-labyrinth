package dev.galiev.worldoflabyrinth.block.entity

import dev.galiev.worldoflabyrinth.block.TrapBlock
import dev.galiev.worldoflabyrinth.registry.BlockEntityRegistry
import dev.galiev.worldoflabyrinth.registry.DimensionRegistry
import net.minecraft.block.BarrelBlock
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ChestBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.tag.BlockTags
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class TrapBlockEntity(pos: BlockPos?, state: BlockState?)
    : BlockEntity(BlockEntityRegistry.TRAP_BLOCK_ENTITY, pos, state) {

    companion object {
        fun tick(world: World, blockPos: BlockPos, state: BlockState, blockEntity: TrapBlockEntity) {
            if (world.isClient) return
            val block = state.block as? TrapBlock ?: return
            if (block.trap.isBlank()) return

            world.players.filter {
                it.world.registryKey == DimensionRegistry.WOL_LEVEL_KEY &&
                        !it.isCreative && !it.isSpectator
            }.forEach { player ->
                when (block.trap) {
                    "mobs" -> handleMobsTrap(world, blockPos, player)
                    "lava" -> handleLavaTrap(world, blockPos, player)
                    "void" -> handleVoidTrap(world, blockPos, player)
                }
            }
        }

        private fun handleMobsTrap(world: World, blockPos: BlockPos, player: PlayerEntity) {
            val targetArea = BlockPos.iterate(blockPos.add(-7, 0, -7), blockPos.add(7, 3, 7))
            if (player.blockPos in targetArea) {
                mobs(player, world, blockPos)
                targetArea.forEach {
                    if(world.getBlockState(it).isIn(BlockTags.BEACON_BASE_BLOCKS)
                        || world.getBlockState(it).block is ChestBlock || world.getBlockState(it).block is BarrelBlock) {
                        world.breakBlock(it, false)
                    }
                }

                world.setBlockState(blockPos, Blocks.MOSS_BLOCK.defaultState)
            }
        }

        private fun handleLavaTrap(world: World, blockPos: BlockPos, player: PlayerEntity) {
            val targetArea = BlockPos.iterate(blockPos.add(-2, -1, -2), blockPos.add(2, 2, 2))
            if (player.blockPos in targetArea) {
                BlockPos.iterate(blockPos.add(-1, 0, -1), blockPos.add(1, 0, 1)).forEach {
                    world.setBlockState(it, Blocks.LAVA.defaultState)
                }
                BlockPos.iterate(blockPos.add(-1, -1, -1), blockPos.add(1, -1, 1)).forEach {
                    world.setBlockState(it, Blocks.BARRIER.defaultState)
                }
                world.setBlockState(blockPos, Blocks.LAVA.defaultState)
            }
        }

        private fun handleVoidTrap(world: World, blockPos: BlockPos, player: PlayerEntity) {
            val targetArea = BlockPos.iterate(blockPos.add(-3, -1, -3), blockPos.add(3, 2, 3))
            if (player.blockPos in targetArea) {
                BlockPos.iterate(blockPos.add(-1, 0, -1), blockPos.add(1, 0, 1)).forEach {
                    world.breakBlock(it, false)
                }
                world.breakBlock(blockPos, false)
            }
        }

        private fun mobs(player: PlayerEntity, world: World, pos: BlockPos) {
            val effects = listOf(
                StatusEffectInstance(StatusEffects.BLINDNESS, 300, 3),
                StatusEffectInstance(StatusEffects.SLOWNESS, 300, 3),
                StatusEffectInstance(StatusEffects.WEAKNESS, 300, 3)
            )
            effects.forEach { player.addStatusEffect(it) }

            (-1..1).forEach { i ->
                listOf(
                    EntityType.ZOMBIE.create(world)?.apply {
                        equipArmor(
                            Items.IRON_HELMET, Items.DIAMOND_CHESTPLATE,
                            Items.IRON_LEGGINGS, Items.DIAMOND_BOOTS,
                            Items.DIAMOND_SWORD
                        )
                        setPosition(pos.x.toDouble() - i + 3, pos.y + 1.0, pos.z.toDouble() - i + 3)
                    },
                    EntityType.SKELETON.create(world)?.apply {
                        equipArmor(
                            Items.CHAINMAIL_HELMET, Items.IRON_CHESTPLATE,
                            Items.CHAINMAIL_LEGGINGS, Items.LEATHER_BOOTS,
                            Items.BOW
                        )
                        setPosition(pos.x.toDouble() - i + 3, pos.y + 1.0, pos.z.toDouble() - i + 3)
                    }
                ).forEach { entity -> entity?.let { world.spawnEntity(it) } }
            }
        }

        private fun LivingEntity.equipArmor(
            head: Item, chest: Item, legs: Item, feet: Item, mainHand: Item
        ) {
            equipStack(EquipmentSlot.HEAD, head.defaultStack)
            equipStack(EquipmentSlot.CHEST, chest.defaultStack)
            equipStack(EquipmentSlot.LEGS, legs.defaultStack)
            equipStack(EquipmentSlot.FEET, feet.defaultStack)
            equipStack(EquipmentSlot.MAINHAND, mainHand.defaultStack)
        }

    }
}