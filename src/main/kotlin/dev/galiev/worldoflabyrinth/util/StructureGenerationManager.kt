package dev.galiev.worldoflabyrinth.util

import dev.galiev.worldoflabyrinth.WorldOfLabyrinth.logger
import net.minecraft.block.JigsawBlock
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.structure.*
import net.minecraft.structure.StructureTemplate.StructureBlockInfo
import net.minecraft.structure.pool.EmptyPoolElement
import net.minecraft.structure.pool.StructurePool
import net.minecraft.structure.pool.StructurePoolElement
import net.minecraft.structure.pool.StructurePools
import net.minecraft.structure.pool.alias.StructurePoolAliasLookup
import net.minecraft.util.BlockRotation
import net.minecraft.util.Identifier
import net.minecraft.util.collection.PriorityIterator
import net.minecraft.util.function.BooleanBiFunction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.random.ChunkRandom
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.HeightLimitView
import net.minecraft.world.Heightmap
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.noise.NoiseConfig
import net.minecraft.world.gen.structure.DimensionPadding
import net.minecraft.world.gen.structure.Structure
import net.minecraft.world.gen.structure.Structure.StructurePosition
import org.apache.commons.lang3.mutable.MutableObject
import java.util.*
import java.util.function.Consumer
import kotlin.math.max
import kotlin.math.min


object StructureGenerationManager {

    fun generateLabyrinth(
        context: Structure.Context,
        startPool: RegistryEntry<StructurePool>,
        jigsawName: Optional<Identifier>,
        maxSize: Int,
        startPos: BlockPos,
        heightmapType: Optional<Heightmap.Type>,
        maxDistance: Int,
        aliasLookup: StructurePoolAliasLookup,
        dimensionPadding: DimensionPadding,
        liquidSettings: StructureLiquidSettings
    ): Optional<StructurePosition> {
        val registryManager = context.dynamicRegistryManager()
        val chunkGenerator = context.chunkGenerator()
        val templateManager = context.structureTemplateManager()
        val heightLimit = context.world()
        val random = context.random()
        val poolRegistry = registryManager.get(RegistryKeys.TEMPLATE_POOL)
        val rotation = BlockRotation.random(random)
        logger.info("Rotation of structure: $rotation")
        val pool = startPool.key
            .flatMap { key: RegistryKey<StructurePool>? ->
                poolRegistry.getOrEmpty(
                    aliasLookup.lookup(key)
                )
            }
            .orElse(startPool.value())
        val element = pool.getRandomElement(random)
        if (element === EmptyPoolElement.INSTANCE) {
            return Optional.empty()
        } else {
            val jigsawPos: BlockPos
            if (jigsawName.isPresent) {
                val identifier = jigsawName.get()
                val optional = findStartingJigsawPos(element, identifier, startPos, rotation, templateManager, random)
                if (optional.isEmpty) {
                    logger.error(
                        "No starting jigsaw {} found in start pool {}",
                        identifier,
                        startPool.key.map { key: RegistryKey<StructurePool> -> key.value.toString() }
                            .orElse("<unregistered>")
                    )
                    return Optional.empty()
                }
                jigsawPos = optional.get()
            } else {
                jigsawPos = startPos
            }

            val offset = jigsawPos.subtract(startPos)
            val adjustedStartPos = startPos.subtract(offset)
            val startPiece = PoolStructurePiece(
                templateManager,
                element,
                adjustedStartPos,
                element.groundLevelDelta,
                rotation,
                element.getBoundingBox(templateManager, adjustedStartPos, rotation),
                liquidSettings
            )
            logger.info("Adjusted starting pos: $adjustedStartPos")
            val boundingBox = startPiece.boundingBox
            logger.info("Bounding box: $boundingBox")
            val centerX = (boundingBox.maxX + boundingBox.minX) / 2
            val centerZ = (boundingBox.maxZ + boundingBox.minZ) / 2
            val startY = if (heightmapType.isPresent) {
                startPos.y + chunkGenerator.getHeightOnGround(
                    centerX,
                    centerZ,
                    heightmapType.get(),
                    heightLimit,
                    context.noiseConfig()
                )
            } else {
                startPos.y
            }
            val adjustedY = boundingBox.minY + startPiece.groundLevelDelta
            startPiece.translate(0, startY - adjustedY, 0)
            val finalY = startY + offset.y
            return Optional.of(
                StructurePosition(
                    BlockPos(centerX, finalY, centerZ)
                ) { collector: StructurePiecesCollector ->
                    val pieces: MutableList<PoolStructurePiece> = arrayListOf()
                    pieces.add(startPiece)
                    if (maxSize > 0) {
                        val searchBox = Box(
                            (centerX - maxDistance).toDouble(),
                            max(
                                (finalY - maxDistance).toDouble(),
                                (heightLimit.bottomY + dimensionPadding.bottom()).toDouble()
                            ),
                            (centerZ - maxDistance).toDouble(),
                            (centerX + maxDistance + 1).toDouble(),
                            min(
                                (finalY + maxDistance + 1).toDouble(),
                                (heightLimit.topY - dimensionPadding.top()).toDouble()
                            ),
                            (centerZ + maxDistance + 1).toDouble()
                        )
                        logger.info("Found box: $searchBox")
                        val voxelShape = VoxelShapes.combineAndSimplify(
                            VoxelShapes.cuboid(searchBox),
                            VoxelShapes.cuboid(Box.from(boundingBox)),
                            BooleanBiFunction.ONLY_FIRST
                        )
                        logger.info("Voxel Shape: $voxelShape")
                        generate(
                            context.noiseConfig(),
                            maxSize,
                            chunkGenerator,
                            templateManager,
                            heightLimit,
                            random,
                            poolRegistry,
                            startPiece,
                            pieces,
                            voxelShape,
                            aliasLookup,
                            liquidSettings
                        )
                        pieces.forEach(Consumer { piece: PoolStructurePiece? ->
                            collector.addPiece(
                                piece
                            )
                        })
                    }
                }
            )
        }
    }

    private fun findStartingJigsawPos(
        pool: StructurePoolElement,
        jigsawId: Identifier,
        pos: BlockPos,
        rotation: BlockRotation,
        templateManager: StructureTemplateManager,
        random: ChunkRandom
    ): Optional<BlockPos> {
        val blockInfos = pool.getStructureBlockInfos(templateManager, pos, rotation, random)
        return blockInfos.find { blockInfo ->
            val identifier = Identifier.tryParse(
                (blockInfo.nbt() as NbtCompound).getString("name")
            )
            jigsawId == identifier
        }?.pos?.let { Optional.of(it) } ?: Optional.empty()
    }

    private fun generate(
        noiseConfig: NoiseConfig,
        maxSize: Int,
        chunkGenerator: ChunkGenerator,
        templateManager: StructureTemplateManager,
        heightLimit: HeightLimitView,
        random: Random,
        poolRegistry: Registry<StructurePool>,
        firstPiece: PoolStructurePiece,
        pieces: MutableList<PoolStructurePiece>,
        pieceShape: VoxelShape,
        aliasLookup: StructurePoolAliasLookup,
        liquidSettings: StructureLiquidSettings
    ) {
        val generator = PieceGenerator(
            poolRegistry,
            maxSize,
            chunkGenerator,
            templateManager,
            pieces,
            random
        )
        generator.generatePiece(firstPiece, MutableObject(pieceShape), 0, heightLimit, noiseConfig, aliasLookup, liquidSettings)

        while (generator.structurePieces.hasNext()) {
            val shapedPiece = generator.structurePieces.next()
            generator.generatePiece(
                shapedPiece.piece,
                shapedPiece.pieceShape,
                shapedPiece.currentSize,
                heightLimit,
                noiseConfig,
                aliasLookup,
                liquidSettings
            )
        }
    }

    data class ShapedPoolStructurePiece(
        val piece: PoolStructurePiece,
        val pieceShape: MutableObject<VoxelShape>,
        val currentSize: Int
    )

    class PieceGenerator(
        private val registry: Registry<StructurePool>,
        private val maxSize: Int,
        private val chunkGenerator: ChunkGenerator,
        private val structureTemplateManager: StructureTemplateManager,
        private val children: MutableList<PoolStructurePiece>,
        private val random: Random,
        val structurePieces: PriorityIterator<ShapedPoolStructurePiece> = PriorityIterator()
    ) {

        fun generatePiece(
            piece: PoolStructurePiece,
            pieceShape: MutableObject<VoxelShape>,
            minY: Int,
            world: HeightLimitView,
            noiseConfig: NoiseConfig,
            aliasLookup: StructurePoolAliasLookup,
            liquidSettings: StructureLiquidSettings
        ) {
            val structureElement = piece.poolElement
            val piecePosition = piece.pos
            val pieceRotation = piece.rotation
            val projectionType = structureElement.projection
            val isRigidProjection = projectionType == StructurePool.Projection.RIGID
            val tempShape = MutableObject<VoxelShape>()
            val boundingBox = piece.boundingBox
            val baseY = boundingBox.minY

            for (structureBlock in structureElement.getStructureBlockInfos(
                structureTemplateManager, piecePosition, pieceRotation, random
            )) {
                val facingDirection = JigsawBlock.getFacing(structureBlock.state)
                val blockPosition = structureBlock.pos
                val offsetPosition = blockPosition.offset(facingDirection)
                val localY = blockPosition.y - baseY
                var groundHeight = -1
                val poolKey = lookupPool(structureBlock, aliasLookup)
                val poolEntry = registry.getEntry(poolKey)

                if (poolEntry.isEmpty) {
                    logger.warn("Empty or non-existent pool: {}", poolKey.value)
                    continue
                }

                val poolValue = poolEntry.get().value()
                if (poolValue.elementCount == 0 && !poolEntry.get().matchesKey(StructurePools.EMPTY)) {
                    logger.warn("Empty or non-existent pool: {}", poolKey.value)
                    continue
                }

                val fallbackPool = poolValue.fallback
                if (fallbackPool.value().elementCount == 0 && !fallbackPool.matchesKey(StructurePools.EMPTY)) {
                    logger.warn("Empty or non-existent fallback pool: {}", fallbackPool.key.map { it.value.toString() }.orElse("<unregistered>"))
                    continue
                }

                val isInsideBoundingBox = boundingBox.contains(offsetPosition)
                val targetShape = if (isInsideBoundingBox) {
                    tempShape.also { if (it.value == null) it.value = VoxelShapes.cuboid(Box.from(boundingBox)) }
                } else {
                    pieceShape
                }

                val elementList = mutableListOf<StructurePoolElement>().apply {
                    if (minY != maxSize) addAll(poolValue.getElementIndicesInRandomOrder(random))
                    addAll(fallbackPool.value().getElementIndicesInRandomOrder(random))
                }

                val placementPriority = structureBlock.nbt?.getInt("placement_priority") ?: 0

                for (element in elementList) {
                    if (element == EmptyPoolElement.INSTANCE) break

                    for (rotation in BlockRotation.randomRotationOrder(random)) {
                        val blockInfoList = element.getStructureBlockInfos(structureTemplateManager, BlockPos.ORIGIN, rotation, random)
                        val elementBoundingBox = element.getBoundingBox(structureTemplateManager, BlockPos.ORIGIN, rotation)

                        val maxElementHeight = if (elementBoundingBox.blockCountY <= 16) {
                            blockInfoList.maxOfOrNull {
                                val adjustedPosition = it.pos.offset(JigsawBlock.getFacing(it.state))
                                if (!elementBoundingBox.contains(adjustedPosition)) return@maxOfOrNull 0
                                val adjustedPoolKey = lookupPool(it, aliasLookup)
                                val adjustedPoolEntry = registry.getEntry(adjustedPoolKey)
                                val highestY = adjustedPoolEntry.map { entry -> entry.value().getHighestY(structureTemplateManager) }.orElse(0)
                                val fallbackHighestY = adjustedPoolEntry.map { entry -> entry.value().fallback.value().getHighestY(structureTemplateManager) }.orElse(0)
                                maxOf(highestY, fallbackHighestY)
                            } ?: 0
                        } else 0

                        for (matchBlock in blockInfoList) {
                            if (JigsawBlock.attachmentMatches(structureBlock, matchBlock)) {
                                val offsetPos = matchBlock.pos
                                val relativePos = offsetPosition.subtract(offsetPos)
                                val newBoundingBox = element.getBoundingBox(structureTemplateManager, relativePos, rotation)
                                val newMinY = newBoundingBox.minY
                                val newProjection = element.projection
                                val isNewRigid = newProjection == StructurePool.Projection.RIGID
                                val attachmentOffset = matchBlock.pos.y
                                val heightOffset = localY - attachmentOffset + JigsawBlock.getFacing(structureBlock.state).offsetY

                                val finalY = if (isRigidProjection && isNewRigid) {
                                    baseY + heightOffset
                                } else {
                                    if (groundHeight == -1) {
                                        groundHeight = chunkGenerator.getHeightOnGround(
                                            blockPosition.x, blockPosition.z, Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig
                                        )
                                    }
                                    groundHeight - attachmentOffset
                                }

                                val yAdjustment = finalY - newMinY
                                val adjustedBoundingBox = newBoundingBox.offset(0, yAdjustment, 0)
                                val adjustedPos = relativePos.add(0, yAdjustment, 0)

                                if (maxElementHeight > 0) {
                                    val maxAdjustedHeight = maxOf(maxElementHeight + 1, adjustedBoundingBox.maxY - adjustedBoundingBox.minY)
                                    adjustedBoundingBox.encompass(BlockPos(adjustedBoundingBox.minX, adjustedBoundingBox.minY + maxAdjustedHeight, adjustedBoundingBox.minZ))
                                }

                                if (!VoxelShapes.matchesAnywhere(targetShape.value, VoxelShapes.cuboid(Box.from(adjustedBoundingBox).contract(0.25)), BooleanBiFunction.ONLY_SECOND)) {
                                    targetShape.value = VoxelShapes.combine(targetShape.value, VoxelShapes.cuboid(Box.from(adjustedBoundingBox)), BooleanBiFunction.ONLY_FIRST)

                                    val groundLevelDelta = piece.groundLevelDelta
                                    val newGroundLevel = if (isNewRigid) {
                                        groundLevelDelta - heightOffset
                                    } else {
                                        element.groundLevelDelta
                                    }

                                    val newPiece = PoolStructurePiece(
                                        structureTemplateManager, element, adjustedPos, newGroundLevel, rotation, adjustedBoundingBox, liquidSettings
                                    )

                                    val adjustedGroundY = if (isRigidProjection) baseY + localY else if (isNewRigid) finalY + attachmentOffset else {
                                        if (groundHeight == -1) {
                                            groundHeight = chunkGenerator.getHeightOnGround(blockPosition.x, blockPosition.z, Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig)
                                        }
                                        groundHeight + heightOffset / 2
                                    }

                                    piece.addJunction(JigsawJunction(offsetPosition.x, adjustedGroundY - localY + groundLevelDelta, offsetPosition.z, heightOffset, newProjection))
                                    newPiece.addJunction(JigsawJunction(blockPosition.x, adjustedGroundY - attachmentOffset + newGroundLevel, blockPosition.z, -heightOffset, projectionType))
                                    children.add(newPiece)

                                    if (minY + 1 <= maxSize) {
                                        structurePieces.enqueue(ShapedPoolStructurePiece(newPiece, targetShape, minY + 1), placementPriority)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        private fun lookupPool(
            structureBlockInfo: StructureBlockInfo, aliasLookup: StructurePoolAliasLookup
        ): RegistryKey<StructurePool> {
            val nbt = Objects.requireNonNull(structureBlockInfo.nbt()) { "$structureBlockInfo nbt was null" }!!
            return aliasLookup.lookup(StructurePools.of(nbt.getString("pool")))
        }

    }
}