package dev.galiev.worldoflabyrinth.mixin;

import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PoolStructurePiece.class)
public interface PoolStructurePieceAccessor {
    @Mutable
    @Accessor("pos")
    void setPos(BlockPos newPos);
}
