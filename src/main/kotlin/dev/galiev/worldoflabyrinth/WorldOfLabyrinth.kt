package dev.galiev.worldoflabyrinth

import dev.galiev.worldoflabyrinth.component.DataComponentType
import dev.galiev.worldoflabyrinth.event.*
import dev.galiev.worldoflabyrinth.registry.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.`object`.builder.v1.trade.TradeOfferHelper
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.village.TradeOffer
import net.minecraft.village.TradeOffers
import net.minecraft.village.TradedItem
import net.minecraft.village.VillagerProfession
import org.apache.logging.log4j.LogManager
import java.util.*


object WorldOfLabyrinth : ModInitializer {
    const val MOD_ID = "world-of-labyrinth"
    val RANDOM = Random(System.currentTimeMillis())
    val logger = LogManager.getLogger(WorldOfLabyrinth::class.java)

    override fun onInitialize() {
        AttackBlockCallback.EVENT.register(BlockBreakingEvent)
        UseBlockCallback.EVENT.register(BlockPlacingEvent)
        UseBlockCallback.EVENT.register(DoorOpenEvent)
        UseBlockCallback.EVENT.register(TrappedChestOpenEvent)
        EntityElytraEvents.ALLOW.register(ElytraEvent)
        ItemRegistry
        BlockRegistry
        BlockEntityRegistry
        BiomeRegistry
        StructureRegistry
        //StructureTags
        DataComponentType
        TradeOfferHelper.registerVillagerOffers(
            VillagerProfession.CARTOGRAPHER, 3
        ) { factories ->
            factories.add(TradeOffers.Factory { entity, random ->
                TradeOffer(
                    TradedItem(Items.CLOCK, 1),
                    Optional.of(TradedItem(Items.COMPASS, 1)),
                    ItemStack(ItemRegistry.GUIDE_STONE, 1),
                    1, 5, 0.05f
                )
            })
        }
    }
}
