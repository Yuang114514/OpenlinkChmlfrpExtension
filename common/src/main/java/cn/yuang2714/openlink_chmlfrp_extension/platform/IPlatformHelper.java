package cn.yuang2714.openlink_chmlfrp_extension.platform;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.datatypes.ConfigProvider;

public interface IPlatformHelper {
    String genUA();
    String getPlatform();
    ConfigProvider getConfigProvider();
}