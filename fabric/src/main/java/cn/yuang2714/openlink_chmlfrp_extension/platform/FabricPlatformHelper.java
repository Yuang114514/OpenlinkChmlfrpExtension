package cn.yuang2714.openlink_chmlfrp_extension.platform;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public String genUA() {
        return String.format("OpenLinkChmlfrpExtension/%s (Fabric %s; Minecraft %s)",
                FabricLoader.getInstance().getModContainer("openlink_chmlfrp_extension").map(mod -> mod.getMetadata().getVersion().toString()).orElse("unknown"),
                FabricLoader.getInstance().getModContainer("fabricloader").map(container -> container.getMetadata().getVersion().toString()).orElse("unknown"),
                FabricLoader.getInstance().getModContainer("minecraft").map(container -> container.getMetadata().getVersion().toString()).orElse("unknown"));
    }
    
    @Override
    public String getPlatform() {
        return "Fabric " + FabricLoader.getInstance().getModContainer("fabricloader").map(container -> container.getMetadata().getVersion().toString()).orElse("unknown");
    }
}
