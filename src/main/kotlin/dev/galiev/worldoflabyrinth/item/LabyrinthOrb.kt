package dev.galiev.worldoflabyrinth.item

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.RANDOM
import dev.galiev.worldoflabyrinth.registry.DimensionRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class LabyrinthOrb : Item(Settings().maxCount(1)) {

    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        if (world?.isClient!!) return TypedActionResult.fail(user?.getStackInHand(hand))

        val dim = world.server?.getWorld(DimensionRegistry.WOL_LEVEL_KEY)
        if (user?.world?.registryKey == dim?.registryKey) {
            user?.teleport(dim, RANDOM.nextDouble(1000.0), user.y,
                RANDOM.nextDouble(1000.0), setOf(), user.yaw, user.pitch)
        } else {
            user?.teleport(dim, user.x, user.y, user.z, setOf(), user.yaw, user.pitch)
        }

        return super.use(world, user, hand)
    }

    override fun appendTooltip(
        stack: ItemStack?,
        context: TooltipContext?,
        tooltip: MutableList<Text>?,
        type: TooltipType?
    ) {
        tooltip?.add(Text.translatable("itemTooltip.world-of-labyrinth.labyrinth_orb").formatted(Formatting.LIGHT_PURPLE))
        super.appendTooltip(stack, context, tooltip, type)
    }
}