package dev.galiev.worldoflabyrinth.registry

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.MOD_ID
import dev.galiev.worldoflabyrinth.block.LabyrinthSensor
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object BlockRegistry {
    val BLOCKS: MutableMap<Block, Identifier> = LinkedHashMap()

    val LABYRINTH_SCULK = LabyrinthSensor(
        Settings.copy(Blocks.SCULK_SENSOR)
    ).create("labyrinth_sculk_sensor")

    init {
        BLOCKS.keys.forEach { block ->
            Registry.register(Registries.BLOCK, BLOCKS[block], block)
            Registry.register(Registries.ITEM, BLOCKS[block], BlockItem(block, Item.Settings()))
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register {
                it.add(block)
            }
        }
    }

    private fun Block.create(id: String): Block = this.apply {
        BLOCKS[this] = Identifier.of(MOD_ID, id)
    }
}