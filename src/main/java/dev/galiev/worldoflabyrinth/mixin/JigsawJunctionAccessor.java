package dev.galiev.worldoflabyrinth.mixin;

import net.minecraft.structure.JigsawJunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(JigsawJunction.class)
public interface JigsawJunctionAccessor {
    @Mutable
    @Accessor("sourceX")
    void setSourceX(int var1);

    @Mutable
    @Accessor("sourceGroundY")
    void setSourceGroundY(int var1);

    @Mutable
    @Accessor("sourceZ")
    void setSourceZ(int var1);
}
