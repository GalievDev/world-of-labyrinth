package dev.galiev.worldoflabyrinth.item

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.MOD_ID
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ItemRegistry {
    private val ITEMS: MutableMap<Item, Identifier> = LinkedHashMap()

    val TELEPORTER = Teleporter().create("teleporter")

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