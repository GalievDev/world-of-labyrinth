package dev.galiev.worldoflabyrinth.item

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.RANDOM
import dev.galiev.worldoflabyrinth.client.render.NetherLabyrinthOrbRenderer
import dev.galiev.worldoflabyrinth.registry.DimensionRegistry
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import software.bernie.geckolib.animatable.GeoAnimatable
import software.bernie.geckolib.animatable.GeoItem
import software.bernie.geckolib.animatable.SingletonGeoAnimatable
import software.bernie.geckolib.animatable.client.GeoRenderProvider
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.animation.*
import software.bernie.geckolib.renderer.GeoItemRenderer
import software.bernie.geckolib.util.GeckoLibUtil
import java.util.function.Consumer

class NetherLabyrinthOrb : Item(Settings().maxCount(1)), GeoItem {
    private val cache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)

    init {
        SingletonGeoAnimatable.registerSyncedAnimatable(this)
    }

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
        if (Screen.hasShiftDown()) {
            tooltip?.add(
                Text.translatable("itemTooltip.world-of-labyrinth.nether_labyrinth_orb")
                    .formatted(Formatting.RED)
            )
        } else {
            tooltip?.add(
                Text.translatable("itemTooltip.world-of-labyrinth.shiftDown")
                    .formatted(Formatting.YELLOW)
            )
        }
        super.appendTooltip(stack, context, tooltip, type)
    }

    override fun createGeoRenderer(consumer: Consumer<GeoRenderProvider>) {
        consumer.accept(object : GeoRenderProvider {
            private var renderer: NetherLabyrinthOrbRenderer? = null

                override fun getGeoItemRenderer(): GeoItemRenderer<NetherLabyrinthOrb>? {
                if (this.renderer == null) this.renderer = NetherLabyrinthOrbRenderer()

                return this.renderer
            }
        })
    }

    override fun registerControllers(p0: AnimatableManager.ControllerRegistrar) {
        p0.add(AnimationController(this, "controller", 0, ::predict))
    }

    private fun <T: GeoAnimatable> predict(animationState: AnimationState<T>): PlayState {
        animationState.controller.setAnimation(RawAnimation.begin().thenLoop("animtel"))
        return PlayState.CONTINUE
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache = cache
}