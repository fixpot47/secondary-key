package dev.fixpot47.secondarykey.mixin;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {
    @Accessor("clickCount")
    int secondarykey$getClickCount();

    @Accessor("clickCount")
    void secondarykey$setClickCount(int value);
}
