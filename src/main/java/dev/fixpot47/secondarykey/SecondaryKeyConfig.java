package dev.fixpot47.secondarykey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SecondaryKeyConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("secondarykey.json");

    private static Data data = new Data();

    private SecondaryKeyConfig() {
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            data = new Data();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            Data loaded = GSON.fromJson(reader, Data.class);
            data = loaded != null ? loaded : new Data();
            if (data.bindings == null) {
                data.bindings = new LinkedHashMap<>();
            }
        } catch (Exception ignored) {
            data = new Data();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public static InputConstants.Key getSecondaryKey(KeyMapping mapping) {
        String saved = data.bindings.get(mapping.getName());
        if (saved == null || saved.isBlank()) {
            return null;
        }

        try {
            InputConstants.Key key = InputConstants.getKey(saved);
            return key.equals(InputConstants.UNKNOWN) ? null : key;
        } catch (Exception ignored) {
            return null;
        }
    }

    public static void setSecondaryKey(KeyMapping mapping, InputConstants.Key key) {
        if (key == null || key.equals(InputConstants.UNKNOWN)) {
            data.bindings.remove(mapping.getName());
        } else {
            data.bindings.put(mapping.getName(), key.getName());
        }
        save();
    }

    public static void clearSecondaryKey(KeyMapping mapping) {
        data.bindings.remove(mapping.getName());
        save();
    }

    public static boolean hasSecondaryKey(KeyMapping mapping) {
        return getSecondaryKey(mapping) != null;
    }

    private static final class Data {
        private Map<String, String> bindings = new LinkedHashMap<>();
    }
}
