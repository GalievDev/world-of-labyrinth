package dev.galiev.worldoflabyrinth.event

import dev.galiev.worldoflabyrinth.registry.DimensionRegistry
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents
import net.minecraft.entity.LivingEntity

object ElytraEvent: EntityElytraEvents.Allow {
    override fun allowElytraFlight(entity: LivingEntity?): Boolean {
        return entity?.world?.registryKey != DimensionRegistry.WOL_LEVEL_KEY
    }
}