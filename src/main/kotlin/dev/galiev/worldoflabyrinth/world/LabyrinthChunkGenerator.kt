package dev.galiev.worldoflabyrinth.world

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.SharedConstants
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.registry.RegistryCodecs
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntryList
import net.minecraft.structure.StructureSet
import net.minecraft.util.crash.CrashException
import net.minecraft.util.crash.CrashReport
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.ChunkRegion
import net.minecraft.world.HeightLimitView
import net.minecraft.world.Heightmap
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.biome.source.BiomeAccess
import net.minecraft.world.biome.source.BiomeSource
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.StructureAccessor
import net.minecraft.world.gen.chunk.Blender
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig
import net.minecraft.world.gen.chunk.VerticalBlockSample
import net.minecraft.world.gen.noise.NoiseConfig
import java.util.*
import java.util.concurrent.CompletableFuture


class LabyrinthChunkGenerator(
    private val biomeSource: BiomeSource,
    private val structureOverrides: Optional<RegistryEntryList<StructureSet>>
): ChunkGenerator(biomeSource) {
    private val placedChunks = mutableSetOf<Pair<Int, Int>>()
    companion object {
        val CODEC: MapCodec<LabyrinthChunkGenerator> =
            RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source")
                        .forGetter { generator -> generator.biomeSource },
                    RegistryCodecs.entryList(RegistryKeys.STRUCTURE_SET)
                        .lenientOptionalFieldOf("structure_overrides")
                        .forGetter { generator -> generator.structureOverrides }
                ).apply(instance, ::LabyrinthChunkGenerator)
            }
    }

    override fun getCodec(): MapCodec<out ChunkGenerator> = CODEC

    override fun generateFeatures(world: StructureWorldAccess, chunk: Chunk, structureAccessor: StructureAccessor) {
        val chunkPos = chunk.pos
        val chunkX = chunkPos.x
        val chunkZ = chunkPos.z
        if (!SharedConstants.isOutsideGenerationArea(chunkPos)) {
            val registry = world.registryManager.get(RegistryKeys.STRUCTURE)
            val map = registry.groupBy { it.featureGenerationStep.ordinal }
            val chunkSection = ChunkSectionPos.from(chunk.pos, world.bottomSectionCoord)
            // Check if this chunk or adjacent chunks already have a structure
            val neighbors = listOf(
                Pair(chunkX, chunkZ)
            )
            if (neighbors.any { placedChunks.contains(it) }) return // Avoid placing if overlapping
            try {
                val j = GenerationStep.Feature.entries.size
                for (k in 0 until j) {
                    if (structureAccessor.shouldGenerateStructures()) {
                        val structures = map.getOrDefault(k, emptyList())
                        for (structure in structures) {
                            val supplier: () -> String =
                                { registry.getKey(structure)?.toString() ?: structure.toString() }

                            try {
                                world.setCurrentlyGeneratingStructureName(supplier)
                                structureAccessor.getStructureStarts(chunkSection, structure)
                                    .forEach { start ->
                                        start.children.forEach {
                                            it.generate(
                                                world,
                                                structureAccessor,
                                                this,
                                                Random.create(),
                                                getBlockBoxForChunk(chunk),
                                                chunkPos,
                                                getBlockBoxForChunk(chunk).center)
                                        }
                                        placedChunks.add(Pair(chunkX, chunkZ))
                                    }
                            } catch (var29: Exception) {
                                val crashReport = CrashReport.create(var29, "Feature placement")
                                crashReport.addElement("Feature").add("Description", supplier)
                                throw CrashException(crashReport)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                val crashReport3 = CrashReport.create(e, "Biome decoration")
                crashReport3.addElement("Generation").add("CenterX", chunkPos.x).add("CenterZ", chunkPos.z)
                throw CrashException(crashReport3)
            }
        }
    }

    private fun getBlockBoxForChunk(chunk: Chunk): BlockBox {
        val chunkPos = chunk.pos
        val i = chunkPos.startX
        val j = chunkPos.startZ
        val heightLimitView = chunk.heightLimitView
        val k = heightLimitView.bottomY + 1
        val l = heightLimitView.topY - 1
        return BlockBox(i, k, j, i + 15, l, j + 15)
    }

    override fun carve(
        chunkRegion: ChunkRegion?,
        seed: Long,
        noiseConfig: NoiseConfig?,
        biomeAccess: BiomeAccess?,
        structureAccessor: StructureAccessor?,
        chunk: Chunk?,
        carverStep: GenerationStep.Carver?
    ) {
    }

    override fun buildSurface(
        region: ChunkRegion?,
        structures: StructureAccessor?,
        noiseConfig: NoiseConfig?,
        chunk: Chunk?
    ) {

    }

    override fun populateEntities(region: ChunkRegion?){}

    override fun getWorldHeight(): Int = 384

    override fun populateNoise(
        blender: Blender,
        noiseConfig: NoiseConfig,
        structureAccessor: StructureAccessor,
        chunk: Chunk
    ): CompletableFuture<Chunk> {
        val mutable = BlockPos.Mutable()
        val heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG)
        val heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG)

            val blockState = Blocks.AIR.defaultState
            if (blockState != null) {
                val i = chunk.bottomY

                for (j in 0..15) {
                    for (k in 0..15) {
                        chunk.setBlockState(mutable.set(j, i, k), blockState, false)
                        heightmap.trackUpdate(j, i, k, blockState)
                        heightmap2.trackUpdate(j, i, k, blockState)
                    }
                }
            }

        return CompletableFuture.completedFuture(chunk)
    }

    override fun getSeaLevel(): Int = -63

    override fun getMinimumY(): Int = 0

    override fun getHeight(
        x: Int,
        z: Int,
        heightmap: Heightmap.Type,
        world: HeightLimitView,
        noiseConfig: NoiseConfig
    ): Int {
        for (i in 0 until world.topY - 1) {
            return world.bottomY + i + 1
        }
        return world.bottomY
    }

    override fun getColumnSample(
        x: Int,
        z: Int,
        world: HeightLimitView,
        noiseConfig: NoiseConfig
    ): VerticalBlockSample = VerticalBlockSample(
        world.bottomY,
        arrayOf<BlockState>(Blocks.AIR.defaultState)
    )

    override fun getDebugHudText(text: MutableList<String>?, noiseConfig: NoiseConfig?, pos: BlockPos?) {
    }
}