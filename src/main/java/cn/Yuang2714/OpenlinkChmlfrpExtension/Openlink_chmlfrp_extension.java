package cn.Yuang2714.OpenlinkChmlfrpExtension;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Openlink_chmlfrp_extension.MODID)
public class Openlink_chmlfrp_extension {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "openlink_chmlfrp_extension";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public Openlink_chmlfrp_extension() {
        LOGGER.info("Openlink ChmlFrp Extension Loaded.");
    }
}
