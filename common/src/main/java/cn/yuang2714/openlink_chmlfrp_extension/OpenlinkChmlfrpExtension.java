package cn.yuang2714.openlink_chmlfrp_extension;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.platform.PlatformServices;
import cn.yuang2714.openlink_chmlfrp_extension.tools.LoggingManagement;
import cn.yuang2714.openlink_chmlfrp_extension.tools.Utils;
import org.slf4j.Logger;

import java.util.prefs.Preferences;

public class OpenlinkChmlfrpExtension {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "openlink_chmlfrp_extension";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = Utils.genLogger();
    public static Preferences PREFERENCES = Preferences.userNodeForPackage(OpenlinkChmlfrpExtension.class);
    
    public static void init() {
        LOGGER.info("""
                Initializing OpenLink Chmlfrp Extension on {}.
                
                
                  ____                       _       _         _        ____   _                  _    ______                 ______           _                       _
                 / __ \\                     | |     (_)       | |      / __ \\ | |                | |  | _____|               |  ____|        _| |_                    (_)
                | |  | | _ __    ___  _ __  | |      _  _ __  | | __  | |  \\_|| |___   _ ___ __  | |  | |____  _ __  _ __    | |____  _   _ |_   _|  ___  _ __   ____  _   ____   _ __ \s
                | |  | || '_ \\  / _ \\| '_ \\ | |     | || '_ \\ | |/ /  | |   _ | /__ \\ | '_  '_ \\ | |  |  ____|| '__|| '_ \\   |  ____|\\ \\_/ /  | |   / _ \\| '_ \\ / ___|| | / __ \\ | '_ \\
                | |__| || |_) ||  __/| | | || |____ | || | | ||   <   | |__/ || /  \\ || | | | | || |_ | |     | |   | |_) |  | |____  > _ <   | |__|  __/| | | |\\___ \\| || |__| || | | |
                 \\____/ | .__/  \\___||_| |_||______||_||_| |_||_|\\_\\   \\____/ |_|  |_||_| |_| |_||___||_|     |_|   | .__/   |______|/_/ \\_\\   \\__/ \\___||_| |_||____/|_| \\____/ |_| |_|
                        | |                                                                                         | |
                        |_|                                                                                         |_|
                """, PlatformServices.PLATFORM.getPlatform());
    }
    
    /*
    函数已乘黄鹤去（放各个Loader里了），此地空余树状图
    
     /oce <command>
       \--\ setProxyCreationMaxRetry
       |  \- 无参数 输出当前配置
       |  \- <value> 设置为value
       \- reloadUserInfo 重新加载用户信息
       \--\ setDoAdvancedNodeSort
       |  \- 无参数 输出当前配置
       |  \- <value> 设置为value
    */
    
    public static void clientTickCallback() {
        if (
                PREFERENCES.getBoolean("is_logged_in", false)
                && (PREFERENCES.getLong("expires_in", 0) <= System.currentTimeMillis())
        ) {
            PREFERENCES.putLong("expires_in", PREFERENCES.getLong("expires_in", 0) + 5000);
            if (LoggingManagement.refreshToken()) LOGGER.info("Refreshed access token success.");
            else {
                PREFERENCES.putBoolean("is_logged_in", false);
                LOGGER.error("Refreshed access token failed.");
            }
        }
    }
}
