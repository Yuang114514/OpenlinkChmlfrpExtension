package cn.yuang2714.openlink_chmlfrp_extension.forge;

import net.minecraftforge.fml.common.Mod;

import cn.yuang2714.openlink_chmlfrp_extension.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModForge {
    public ExampleModForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
