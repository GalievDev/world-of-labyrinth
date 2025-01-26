package dev.galiev.worldoflabyrinth.component

import com.mojang.serialization.Codec
import dev.galiev.worldoflabyrinth.WorldOfLabyrinth
import net.minecraft.component.ComponentType
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import java.util.function.UnaryOperator


object DataComponentType {
    val ACTIVATED: ComponentType<Boolean> = register("activated") { builder ->
            builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL)
    }

    private fun <T> register(name: String, builderOperator: UnaryOperator<ComponentType.Builder<T>>): ComponentType<T> {
        return Registry.register<ComponentType<*>, ComponentType<T>>(
            Registries.DATA_COMPONENT_TYPE, Identifier.of(WorldOfLabyrinth.MOD_ID, name),
            builderOperator.apply(ComponentType.builder<T>()).build()
        )
    }
}