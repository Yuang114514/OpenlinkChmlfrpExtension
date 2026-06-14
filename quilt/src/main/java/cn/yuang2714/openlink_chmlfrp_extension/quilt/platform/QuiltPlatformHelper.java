package cn.yuang2714.openlink_chmlfrp_extension.quilt.platform;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OCECommand;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.ConfigProvider;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.ConfigProvider.Key;
import cn.yuang2714.openlink_chmlfrp_extension.platform.IPlatformHelper;
import cn.yuang2714.openlink_chmlfrp_extension.quilt.ConfigFileHolder;
import com.google.gson.JsonObject;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.impl.QuiltLoaderImpl;

public class QuiltPlatformHelper implements IPlatformHelper {
    @Override
    public String genUA() {
        return String.format("OpenLinkChmlfrpExtension/%s (Fabric %s; Minecraft %s)",
                QuiltLoader.getModContainer("openlink_chmlfrp_extension").map(mod -> mod.metadata().version().raw()).orElse("unknown"),
                QuiltLoaderImpl.VERSION,
                QuiltLoader.getModContainer("minecraft").map(mod -> mod.metadata().version().raw()).orElse("unknown"));
    }
    
    @Override
    public String getPlatform() {
        return "Quilt" + QuiltLoaderImpl.VERSION;
    }
    
    @Override
    public ConfigProvider getConfigProvider() {
        return new ConfigProvider(
                new Key<>(
                        () -> {
                            try {
                                JsonObject content = ConfigFileHolder.instance.read();
                                return content.get("doAdvancedNodeSort").getAsBoolean();
                            } catch (Exception e) {
                                OCECommand.logger.warn("Failed to read config doAdvancedNodeSort.", e);
                                return false; //默认值
                            }
                        },
                        value -> {
                            try {
                                JsonObject currentContent = ConfigFileHolder.instance.read();
                                if (currentContent.has("doAdvancedNodeSort")) currentContent.remove("doAdvancedNodeSort");
                                currentContent.addProperty("doAdvancedNodeSort", value);
                                ConfigFileHolder.instance.write(currentContent);
                            } catch (Exception e) {
                                OCECommand.logger.warn("Failed to write config doAdvancedNodeSort.", e);
                            }
                        }
                ),
                new Key<>(
                        () -> {
                            try {
                                JsonObject content = ConfigFileHolder.instance.read();
                                return content.get("proxyCreationMaxRetryCount").getAsInt();
                            } catch (Exception e) {
                                OCECommand.logger.warn("Failed to read config proxyCreationMaxRetryCount.", e);
                                return 5; //默认值
                            }
                        },
                        value -> {
                            try {
                                JsonObject currentContent = ConfigFileHolder.instance.read();
                                if (currentContent.has("proxyCreationMaxRetryCount")) currentContent.remove("proxyCreationMaxRetryCount");
                                currentContent.addProperty("proxyCreationMaxRetryCount", value);
                                ConfigFileHolder.instance.write(currentContent);
                            } catch (Exception e) {
                                OCECommand.logger.warn("Failed to write config proxyCreationMaxRetryCount.", e);
                            }
                        }
                )
        );
    }
}
