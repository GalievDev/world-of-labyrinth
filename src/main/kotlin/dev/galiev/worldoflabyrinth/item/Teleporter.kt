package dev.galiev.worldoflabyrinth.item

import dev.galiev.worldoflabyrinth.world.dimension.DimensionRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class Teleporter : Item(Settings().maxCount(1)) {

    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        if (!world?.isClient()!!) {
            val dim = world.server?.getWorld(DimensionRegistry.WOL_LEVEL_KEY)
            user?.teleport(dim, user.x, user.y, user.z, setOf(), user.yaw, user.pitch)
        }
        return super.use(world, user, hand)
    }
}