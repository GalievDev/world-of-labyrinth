package dev.galiev.worldoflabyrinth.util

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.MOD_ID
import dev.galiev.worldoflabyrinth.component.DataComponentType
import dev.galiev.worldoflabyrinth.registry.ItemRegistry
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

object ModelPredicate {
    init {
        ModelPredicateProviderRegistry.register(ItemRegistry.GUIDE_STONE, Identifier.of(MOD_ID, "active")
        ) { stack: ItemStack, _: ClientWorld?, _: LivingEntity?, _: Int ->
            if (stack.get(DataComponentType.ACTIVATED) == true) 1f else 0f
        }
    }
}