package cn.yuang2714.openlink_chmlfrp_extension.forge;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {
    public static final Holder HOLDER;
    public static final ForgeConfigSpec CONFIG_SPEC;
    
    static {
        final Pair<Holder, ForgeConfigSpec> specPair =
                new ForgeConfigSpec.Builder().configure(Holder::new);
        
        HOLDER = specPair.getLeft();
        CONFIG_SPEC = specPair.getRight();
    }
    
    public static class Holder {
        public final ForgeConfigSpec.BooleanValue doAdvancedNodeSort;
        public final ForgeConfigSpec.IntValue proxyCreationMaxRetryCount;
        
        Holder(ForgeConfigSpec.Builder builder) {
            builder.comment("Configs for OpenLink Chmlfrp Extension").push("Config");
            
            doAdvancedNodeSort = builder
                    .comment("Do advanced node selection")
                    .define("doAdvancedNodeSort", true);
            proxyCreationMaxRetryCount = builder
                    .comment("Max retry times when starting proxy")
                    .defineInRange("proxyCreationMaxRetryCount", 5, 0, Integer.MAX_VALUE);
            
            builder.pop();
        }
    }
}
