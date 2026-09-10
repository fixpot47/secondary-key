package dev.fixpot47.secondarykey;

import com.mojang.blaze3d.platform.InputConstants;
import dev.fixpot47.secondarykey.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ToggleKeyMapping;

import java.util.HashSet;
import java.util.Set;

public final class SecondaryKeyManager {
    private static final Set<InputConstants.Key> DOWN_KEYS = new HashSet<>();

    private SecondaryKeyManager() {
    }

    public static void handleClick(InputConstants.Key physicalKey) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.options == null) {
            return;
        }

        for (KeyMapping mapping : minecraft.options.keyMappings) {
            InputConstants.Key secondary = SecondaryKeyConfig.getSecondaryKey(mapping);
            if (secondary == null || !secondary.equals(physicalKey)) {
                continue;
            }

            InputConstants.Key primary = getPrimaryKey(mapping);
            if (secondary.equals(primary)) {
                continue;
            }

            KeyMappingAccessor accessor = (KeyMappingAccessor) (Object) mapping;
            accessor.secondarykey$setClickCount(accessor.secondarykey$getClickCount() + 1);
        }
    }

    public static void handleSet(InputConstants.Key physicalKey, boolean down) {
        if (down) {
            DOWN_KEYS.add(physicalKey);
        } else {
            DOWN_KEYS.remove(physicalKey);
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.options == null) {
            return;
        }

        for (KeyMapping mapping : minecraft.options.keyMappings) {
            InputConstants.Key secondary = SecondaryKeyConfig.getSecondaryKey(mapping);
            if (secondary == null) {
                continue;
            }

            InputConstants.Key primary = getPrimaryKey(mapping);
            if (secondary.equals(primary)) {
                continue;
            }

            if (mapping instanceof ToggleKeyMapping) {
                if (secondary.equals(physicalKey)) {
                    mapping.setDown(down);
                }
                continue;
            }

            if (secondary.equals(physicalKey)) {
                mapping.setDown(down || DOWN_KEYS.contains(primary));
            } else if (primary.equals(physicalKey) && !down && DOWN_KEYS.contains(secondary)) {
                mapping.setDown(true);
            }
        }
    }

    public static void clearPressedKeys() {
        DOWN_KEYS.clear();
    }

    private static InputConstants.Key getPrimaryKey(KeyMapping mapping) {
        try {
            return InputConstants.getKey(mapping.saveString());
        } catch (Exception ignored) {
            return InputConstants.UNKNOWN;
        }
    }
}
