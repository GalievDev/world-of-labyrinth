package dev.galiev.worldoflabyrinth.world.structure

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.galiev.worldoflabyrinth.registry.StructureRegistry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.structure.StructureLiquidSettings
import net.minecraft.structure.pool.StructurePool
import net.minecraft.structure.pool.StructurePoolBasedGenerator
import net.minecraft.structure.pool.alias.StructurePoolAliasLookup
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.minecraft.world.gen.HeightContext
import net.minecraft.world.gen.heightprovider.HeightProvider
import net.minecraft.world.gen.structure.DimensionPadding
import net.minecraft.world.gen.structure.JigsawStructure
import net.minecraft.world.gen.structure.Structure
import net.minecraft.world.gen.structure.StructureType
import java.util.*


class SkyStructure(
    config: Structure.Config,
    private val startPool: RegistryEntry<StructurePool>,
    private val startJigsawName: Optional<Identifier>,
    private val size: Int,
    private val startHeight: HeightProvider,
    private val projectStartToHeightmap: Optional<Heightmap.Type>,
    private val maxDistanceFromCenter: Int,
    private val dimensionPadding: DimensionPadding,
    private val liquidSettings: StructureLiquidSettings
) : Structure(config) {

    companion object {
        val CODEC: MapCodec<SkyStructure> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                configCodecBuilder(instance),
                StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter { it.startPool },
                Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter { it.startJigsawName },
                Codec.intRange(0, 30).fieldOf("size").forGetter { it.size },
                HeightProvider.CODEC.fieldOf("start_height").forGetter { it.startHeight },
                Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter { it.projectStartToHeightmap },
                Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter { it.maxDistanceFromCenter },
                DimensionPadding.CODEC.optionalFieldOf("dimension_padding", JigsawStructure.DEFAULT_DIMENSION_PADDING)
                    .forGetter { it.dimensionPadding },
                StructureLiquidSettings.codec.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS)
                    .forGetter { it.liquidSettings }
            ).apply(instance, ::SkyStructure)
        }
    }

    private fun extraSpawningChecks(context: Structure.Context): Boolean {
        val chunkPos = context.chunkPos()

        return context.chunkGenerator().getHeightInGround(
            chunkPos.startX, chunkPos.startZ,
            Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
            context.world(),
            context.noiseConfig()
        ) < 150
    }

    override fun getStructurePosition(context: Structure.Context): Optional<Structure.StructurePosition> {
        if (!extraSpawningChecks(context)) {
            return Optional.empty()
        }

        val startY = startHeight.get(context.random(), HeightContext(context.chunkGenerator(), context.world()))
        val chunkPos = context.chunkPos()
        val blockPos = BlockPos(chunkPos.startX, startY, chunkPos.startZ)

        val structurePiecesGenerator = StructurePoolBasedGenerator.generate(
            context,
            startPool,
            startJigsawName,
            size,
            blockPos,
            false,
            projectStartToHeightmap,
            maxDistanceFromCenter,
            StructurePoolAliasLookup.EMPTY,
            dimensionPadding,
            liquidSettings
        )

        return structurePiecesGenerator
    }

    override fun getType(): StructureType<*> {
        return StructureRegistry.SKY_STRUCTURE
    }
}

