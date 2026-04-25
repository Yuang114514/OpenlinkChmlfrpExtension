package cn.yuang2714.openlink_chmlfrp_extension.platform;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
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
}