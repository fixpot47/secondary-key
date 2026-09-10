package dev.fixpot47.secondarykey.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import dev.fixpot47.secondarykey.SecondaryKeyManager;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    @Inject(method = "click", at = @At("TAIL"))
    private static void secondarykey$handleClick(InputConstants.Key key, CallbackInfo ci) {
        SecondaryKeyManager.handleClick(key);
    }

    @Inject(method = "set", at = @At("TAIL"))
    private static void secondarykey$handleSet(InputConstants.Key key, boolean down, CallbackInfo ci) {
        SecondaryKeyManager.handleSet(key, down);
    }

    @Inject(method = "releaseAll", at = @At("TAIL"))
    private static void secondarykey$clearPressedKeys(CallbackInfo ci) {
        SecondaryKeyManager.clearPressedKeys();
    }
}
