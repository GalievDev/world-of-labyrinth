package dev.galiev.worldoflabyrinth.registry

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.MOD_ID
import dev.galiev.worldoflabyrinth.item.GuideStone
import dev.galiev.worldoflabyrinth.item.LabyrinthOrb
import dev.galiev.worldoflabyrinth.item.NetherLabyrinthOrb
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ItemRegistry {
    private val ITEMS: MutableMap<Item, Identifier> = LinkedHashMap()

    val LABYRINTH_ORB = LabyrinthOrb().create("labyrinth_orb")
    val NETHER_LABYRINTH_ORB = NetherLabyrinthOrb().create("nether_labyrinth_orb")
    val GUIDE_STONE = GuideStone().create("guide_stone")

    init {
        ITEMS.keys.forEach { item ->
            Registry.register(Registries.ITEM, ITEMS[item], item)
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register {
                it.add(item)
            }
        }
    }

    private fun Item.create(id: String): Item = this.apply {
        ITEMS[this] = Identifier.of(MOD_ID, id)
    }
}