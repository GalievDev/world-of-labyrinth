package dev.galiev.worldoflabyrinth.world

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.logger
import dev.galiev.worldoflabyrinth.registry.StructureTypeRegistry
import dev.galiev.worldoflabyrinth.util.StructureGenerationManager
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.structure.StructureLiquidSettings
import net.minecraft.structure.pool.StructurePool
import net.minecraft.structure.pool.alias.StructurePoolAliasLookup
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.minecraft.world.gen.HeightContext
import net.minecraft.world.gen.heightprovider.HeightProvider
import net.minecraft.world.gen.structure.DimensionPadding
import net.minecraft.world.gen.structure.Structure
import net.minecraft.world.gen.structure.StructureType
import java.util.*

class LabyrinthJigsawStructure(
    config: Config,
    val startPool: RegistryEntry<StructurePool>,
    val startJigsawName: Optional<Identifier>,
    val size: Int,
    val maxDistance: Int,
    val startHeight: HeightProvider,
) : Structure(config) {

    companion object {
        val CODEC: MapCodec<LabyrinthJigsawStructure> =
            RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    configCodecBuilder(instance),
                    StructurePool.REGISTRY_CODEC.fieldOf("start_pool")
                        .forGetter { it.startPool },
                    Identifier.CODEC.optionalFieldOf("start_jigsaw_name")
                        .forGetter { it.startJigsawName },
                    Codec.intRange(0, 30).fieldOf("size")
                        .forGetter { it.size },
                    Codec.intRange(0, 348).fieldOf("max_distance")
                        .forGetter { it.maxDistance },
                    HeightProvider.CODEC.fieldOf("start_height")
                        .forGetter { it.startHeight },
                ).apply(instance, ::LabyrinthJigsawStructure)
            }
    }

    override fun getStructurePosition(context: Context): Optional<StructurePosition> {
        val offsetY = startHeight.get(context.random, HeightContext(context.chunkGenerator, context.world))
        val blockPos = BlockPos(context.chunkPos.endX, offsetY, context.chunkPos.endZ)
        logger.info("Starting pos $blockPos")
        return StructureGenerationManager.generateLabyrinth(
            context,
            startPool,
            startJigsawName,
            size,
            blockPos,
            Optional.of(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES),
            maxDistance,
            StructurePoolAliasLookup.EMPTY,
            DimensionPadding.NONE,
            StructureLiquidSettings.IGNORE_WATERLOGGING
        )
    }

    override fun getType(): StructureType<*> = StructureTypeRegistry.LABYRINTH
}
