package dev.fixpot47.secondarykey;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class SecondaryKeyClient implements ClientModInitializer {
    public static final String MOD_ID = "secondarykey";

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath(MOD_ID, "controls")
    );

    private static KeyMapping openMenuKey;

    @Override
    public void onInitializeClient() {
        SecondaryKeyConfig.load();

        openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.secondarykey.open_menu",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_K,
            CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                client.gui.setScreen(new SecondaryKeysScreen(client.gui.screen()));
            }
        });
    }
}
