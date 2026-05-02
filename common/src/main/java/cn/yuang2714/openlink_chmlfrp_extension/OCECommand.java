package cn.yuang2714.openlink_chmlfrp_extension;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.tools.FrpcManagement;
import cn.yuang2714.openlink_chmlfrp_extension.tools.LoggingManagement;
import cn.yuang2714.openlink_chmlfrp_extension.tools.Utils;
import org.slf4j.Logger;

public class OCECommand {
    public static final Logger logger = Utils.genLogger();
    public static final int FAILURE = -1;
    public static final int SUCCESS = 1;
    
    public static int readProxyCreationMaxRetry() {
        return OpenlinkChmlfrpExtension.PREFERENCES.getInt("config_max_retry", 5);
    }
    
    public static int setProxyCreationMaxRetry(int value) {
        if (value <= 1) return FAILURE;
        OpenlinkChmlfrpExtension.PREFERENCES.putInt("config_max_retry", value);
        if (Utils.flushPreferences(logger, "changing settings")) return FAILURE;
        return SUCCESS;
    }
    
    public static int reloadUserInfo() {
        try {
            FrpcManagement.initUserEnv();
            OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_logged_in", LoggingManagement.refreshToken());
            LoggingManagement.reloadUserAddress();
            LoggingManagement.refreshUserInfo();
            if (Utils.flushPreferences(logger, "changing settings")) return FAILURE;
            return SUCCESS;
        } catch (Exception e) {
            logger.error("Failed to reload user info. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
            return FAILURE;
        }
    }
    
    public static boolean readDoAdvancedNodeSort() {
        return OpenlinkChmlfrpExtension.PREFERENCES.getBoolean("advanced_node_sort", false);
    }
    
    public static int setDoAdvancedNodeSort(boolean value) {
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("advanced_node_sort", value);
        if (Utils.flushPreferences(logger, "changing settings")) return FAILURE;
        return SUCCESS;
    }
}
