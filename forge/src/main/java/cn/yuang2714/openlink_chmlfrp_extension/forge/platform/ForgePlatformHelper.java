package cn.yuang2714.openlink_chmlfrp_extension.forge.platform;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.ConfigProvider;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.ConfigProvider.Key;
import cn.yuang2714.openlink_chmlfrp_extension.forge.ModConfig;
import cn.yuang2714.openlink_chmlfrp_extension.platform.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.versions.forge.ForgeVersion;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public String genUA() {
        return String.format("OpenLinkChmlfrpExtension/%s (Forge %s, Minecraft %s)",
                ModList.get().getModFileById(OpenlinkChmlfrpExtension.MODID).versionString(),
                ForgeVersion.getVersion(),
                ModList.get().getModContainerById("minecraft").map(container -> container.getModInfo().getVersion().toString()).orElse("unknown")
        );
    }
    
    @Override
    public String getPlatform() {
        return "Forge " + ForgeVersion.getVersion();
    }
    
    @Override
    public ConfigProvider getConfigProvider() {
        return new ConfigProvider(
                new Key<>(
                        ModConfig.HOLDER.doAdvancedNodeSort::get,
                        ModConfig.HOLDER.doAdvancedNodeSort::set
                ),
                new Key<>(
                        ModConfig.HOLDER.proxyCreationMaxRetryCount::get,
                        ModConfig.HOLDER.proxyCreationMaxRetryCount::set
                )
        );
    }
}