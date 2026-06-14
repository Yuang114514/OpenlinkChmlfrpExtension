package cn.yuang2714.openlink_chmlfrp_extension.fabric;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ConfigFileHolder {
    public static ConfigFileHolder instance = new ConfigFileHolder(FabricLoader.getInstance().getConfigDir());
    
    private static File configFile;
    
    public JsonObject read() throws Exception {
        if (!configFile.exists()) {
            if (!configFile.createNewFile()) throw new IOException("Could not create config file");
        }
        return JsonParser
                .parseReader(new FileReader(configFile, StandardCharsets.UTF_8))
                .getAsJsonObject();
    }
    
    public void write(JsonObject content) throws Exception {
        try (FileOutputStream stream = new FileOutputStream(configFile)) {
            stream.write(content.toString().getBytes(StandardCharsets.UTF_8));
        }
    }
    
    public ConfigFileHolder(Path directory) {
        configFile = new File(directory.toFile(), "openlink_chmlfrp_extension.json");
    }
}
